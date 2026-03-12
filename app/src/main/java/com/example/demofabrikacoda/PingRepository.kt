package com.example.demofabrikacoda

import android.util.Log
import com.example.demofabrikacoda.data.PingModel
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
    private val coroutineExceptionHandler: CoroutineExceptionHandler
) {

    private val _history = MutableStateFlow<List<PingModel>>(emptyList())
    val history: StateFlow<List<PingModel>> = _history.asStateFlow()

    private var pingJob: Job? = null

    fun startPingCycle() {
        if (pingJob?.isActive == true) return

        pingJob = coroutineScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            while (isActive) {
                val result = Utils.runIpfsPing()

                // История (FIFO, макс 10)
                result?.let {
                    _history.update { currentList ->
                        val newList = currentList + result
                        if (newList.size > 10) {
                            newList.drop(1)
                        } else {
                            newList
                        }
                    }
                }

                //Расчет задержки в мс
                val targetDelay = 2000L
                val timestampDiff = Instant.now().minusMillis(result?.timestamp?.toEpochMilli() ?: 0L)
                val needDelay =  targetDelay - (timestampDiff.toEpochMilli())
                Log.d("IPFS-ping", "timestampDiff $timestampDiff needDelay $needDelay")

                if (needDelay > 0) {
                    delay(needDelay)
                }
            }
        }
    }

    fun stopPingCycle() {
        pingJob?.cancel()
        pingJob = null
    }
}