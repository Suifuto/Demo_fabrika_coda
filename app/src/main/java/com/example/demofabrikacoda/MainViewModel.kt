package com.example.demofabrikacoda

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.demofabrikacoda.data.AppConst
import com.example.demofabrikacoda.data.PingModel
import com.example.demofabrikacoda.data.toConvert
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.peergos.HashedBlock

class MainViewModel : ViewModel() {
    private val exceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
        _status.value = throwable.toConvert()
    }

    private val ipfsRepository = IpfsRepository()
    private val pingRepository = PingRepository(
        viewModelScope,
        ipfsRepository
    )

    private val _data = MutableStateFlow<List<HashedBlock>>(emptyList())
    val data: StateFlow<List<HashedBlock>> = _data.asStateFlow()

    private val _status = MutableStateFlow<String?>("Ожидает инициализации ноды")
    val status: StateFlow<String?> = _status

    val dataPing: StateFlow<List<PingModel>> = pingRepository.history
    val statusPing: StateFlow<String?> = pingRepository.status

    val blockHistory = mutableListOf<HashedBlock>()

    fun loadDataFromNetwork(cid: String) {
        viewModelScope.launch(Dispatchers.IO + exceptionHandler) {
            _status.value = "Старт получения cid даты"
            try {
                val result = ipfsRepository.getDataFromNode(AppConst.TEST_NODE, cid)
                blockHistory.addAll(result)
                _data.update { currentList ->
                    currentList + result
                }
//                Log.d("IPFS", result.toString())
                _status.value = "Блок получен успешно"
            } catch (e: Exception) {
//                Log.d("IPFS", "Error $e")
                _status.value = e.toString()
            }
            startPing()
        }
    }

    fun startIpfs() {
        ipfsRepository.startIpfs(AppConst.TEST_NODE)
    }

    fun stopIpfs() {
        ipfsRepository.stopIpfs()
    }

    fun startPing() {
        pingRepository.startPingCycle()
    }

    fun stopPing() {
        pingRepository.stopPingCycle()
    }

    override fun onCleared() {
        super.onCleared()
        pingRepository.stopPingCycle()
        ipfsRepository.stopIpfs()
    }
}
