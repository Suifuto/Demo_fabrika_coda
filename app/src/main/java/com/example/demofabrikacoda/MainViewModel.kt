package com.example.demofabrikacoda

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    private val _data = MutableStateFlow<Result<String>?>(null)
    val data: StateFlow<Result<String>?> = _data

    private val exceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
        _data.value = Result.failure(throwable)
    }

    fun loadDataFromNetwork(cid: String) {
        viewModelScope.launch(Dispatchers.IO + exceptionHandler) {
            try {
                val result = Utils.getDataFromNode(Utils.TEST_NODE, cid)

                _data.value = Result.success(result)
                Log.d("IPFS", result)
            } catch (e: Exception) {
                Log.d("IPFS", "Error $e")
                _data.value = Result.failure(e)
            }
        }
    }
}
