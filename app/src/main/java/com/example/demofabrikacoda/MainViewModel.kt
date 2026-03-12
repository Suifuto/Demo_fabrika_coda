package com.example.demofabrikacoda

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.demofabrikacoda.data.MainState
import com.example.demofabrikacoda.data.PingModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.peergos.HashedBlock

class MainViewModel : ViewModel() {
    private val exceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
        // TODO Обработка ошибок
        _error.value = throwable.toString()
    }

    private val exceptionHandlerPing = CoroutineExceptionHandler { coroutineContext, throwable ->
        // TODO Обработка ошибок
        _error.value = throwable.toString()
    }

    private val pingRepository = PingRepository(
        viewModelScope,
        exceptionHandlerPing
    )

    private val _data = MutableStateFlow<MainState>(MainState("Ожидает инициализации"))
    val data: StateFlow<MainState> = _data

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    val dataPing: StateFlow<List<PingModel>> = pingRepository.history

    val blockHistory = mutableListOf<HashedBlock>()

    fun loadDataFromNetwork(cid: String) {
        viewModelScope.launch(Dispatchers.IO + exceptionHandler) {
            try {
                val result = Utils.getDataFromNode(Utils.TEST_NODE, cid)
                blockHistory.addAll(result)
                _data.value = MainState(
                    "Success",
                    blockHistory
                )
                Log.d("IPFS", result.toString())
            } catch (e: Exception) {
                Log.d("IPFS", "Error $e")
                _data.value = MainState(e.toString())
            }
            pingRepository.startPingCycle()
        }
    }

    override fun onCleared() {
        super.onCleared()
        pingRepository.stopPingCycle()
        Utils.stopIpfs()
    }
}
