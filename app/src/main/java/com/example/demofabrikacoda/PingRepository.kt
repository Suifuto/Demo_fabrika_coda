package com.example.demofabrikacoda

import android.util.Log
import com.example.demofabrikacoda.data.PingModel
import com.example.demofabrikacoda.data.toConvert
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.time.Instant

class PingRepository(
    private val coroutineScope: CoroutineScope,
    private val ipfsRepository: IpfsRepository
) {
    private val exceptionHandlerPing = CoroutineExceptionHandler { coroutineContext, throwable ->
        _status.value = throwable.toConvert() + "\n Соединение с нодой разорвано"
        stopPingCycle()
        ipfsRepository.stopIpfs()
    }

    private val _history = MutableStateFlow<List<PingModel>>(emptyList())
    val history: StateFlow<List<PingModel>> = _history.asStateFlow()

    private val _status = MutableStateFlow<String?>("Ожидает инициализации ноды")
    val status: StateFlow<String?> = _status

    private var pingJob: Job? = null

    fun startPingCycle() {
        if (pingJob?.isActive == true) {
            _status.value = "Пинг активен"
            return
        }
        _status.value = "Инициализация пинг"

        pingJob = coroutineScope.launch(Dispatchers.IO + exceptionHandlerPing) {
            while (isActive) {
                val startTimeStamp = Instant.now()
                val result = ipfsRepository.runIpfsPing()

                // История (FIFO, макс 10)
                if (result != null) {
                    _history.update { currentList ->
                        val newList = currentList + PingModel(timestamp = startTimeStamp, latency = result)
                        if (newList.size > 10) {
                            newList.drop(1)
                        } else {
                            newList
                        }
                    }
                }
                _status.value = ""

                val needDelay = getTargetDelay(startTimeStamp = startTimeStamp)
                if (needDelay > 0) {
                    delay(needDelay)
                }
            }
        }
    }

    /**
     * Расчет задержки в мс
     */
    private fun getTargetDelay(targetDelay: Long = 3000L, startTimeStamp: Instant): Long {
        val timestampDiff = Instant.now().minusMillis(startTimeStamp.toEpochMilli())
//        Log.d("IPFS-ping", "timestampDiff $timestampDiff needDelay $needDelay")
        return targetDelay - (timestampDiff.toEpochMilli())
    }

    fun stopPingCycle() {
        _status.value = "Стоп пинг"
        pingJob?.cancel()
        pingJob = null
    }
}