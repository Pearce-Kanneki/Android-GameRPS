package com.kanneki.gamerps.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kanneki.gamerps.repository.userDataRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import java.util.*

class MainViewModel: ViewModel() {

    private val _token = MutableLiveData<String>()
    val token: LiveData<String> get() = _token
    private val _showWaitDialog = MutableLiveData(false)
    val showWaitDialog get() = _showWaitDialog

    fun setWaitDialog(type: Boolean){
        _showWaitDialog.postValue(type)
    }

    suspend fun findUserToken() {
        userDataRepository.getDataStrore(userDataRepository.userToken)
            .flowOn(Dispatchers.IO)
            .collectLatest { data ->
                data?.let {
                    _token.postValue(it)
                }?: run{
                    val newToke = UUID.randomUUID().toString()
                    _token.postValue(newToke)
                    addUserToken(newToke)
                }
            }
    }

    private fun addUserToken(newToke: String){
        GlobalScope.launch {
            userDataRepository.addDataStroe(userDataRepository.userToken, newToke)
        }
    }
}