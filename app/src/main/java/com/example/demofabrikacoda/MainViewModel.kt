package com.example.demofabrikacoda

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    companion object {
        const val TEST_NODE =
            "/dns4/ipfs.infra.cf.team/tcp/4001/p2p/12D3KooWKiqj21VphU2eE25438to5xeny6eP6d3PXT93ZczagPLT"
        const val TEST_CID = "QmTBimFzPPP2QsB7TQGc2dr4BZD4i7Gm2X1mNtb6DqN9Dr"
    }

    private val _data = MutableStateFlow<Result<String>?>(null)
    val data: StateFlow<Result<String>?> = _data

    fun loadDataFromNetwork() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = Utils.getDataFromNode(TEST_NODE, TEST_CID)

                _data.value = Result.success(result)
            } catch (e: Exception) {
                _data.value = Result.failure(e)
            }
        }
    }
}
