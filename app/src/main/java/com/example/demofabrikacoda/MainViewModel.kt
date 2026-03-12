package com.example.demofabrikacoda

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.demofabrikacoda.data.MainState
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.peergos.HashedBlock

class MainViewModel : ViewModel() {
    private val _data = MutableStateFlow<MainState>(MainState("Ожидает инициализации"))
    val data: StateFlow<MainState> = _data

    private val exceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
        // TODO Обработка ошибок
        _data.value = MainState(throwable.toString())
    }

    fun loadDataFromNetwork(cid: String) {
        viewModelScope.launch(Dispatchers.IO + exceptionHandler) {
            try {
                val result = Utils.getDataFromNode(Utils.TEST_NODE, cid)
                _data.value = MainState(
                    "Success",
                    result
                )
                Log.d("IPFS", result.toString())
            } catch (e: Exception) {
                Log.d("IPFS", "Error $e")
                _data.value = MainState(e.toString())
            }
        }
    }
}
