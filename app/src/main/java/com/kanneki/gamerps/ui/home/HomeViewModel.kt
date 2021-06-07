package com.kanneki.gamerps.ui.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kanneki.gamerps.model.DuelModel
import com.kanneki.gamerps.repository.DuelDataRepository
import com.kanneki.gamerps.repository.userDataRepository
import com.kanneki.gamerps.utils.GameSet
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import java.util.*

class HomeViewModel : ViewModel() {

    private val token = MutableLiveData<String>()
    private val _goDuel = MutableLiveData<Boolean>(false)
    val goDuelData get() = _goDuel
    private var _roomKey = ""
    private val _showWaitDialog = MutableLiveData(false)
    val showWaitDialog get() = _showWaitDialog
    private val _editDialog = MutableLiveData(false)
    val editDialog get() = _editDialog

    fun getRoomKey(): String {
        return _roomKey
    }

    fun setToken(id: String){
        token.postValue(id)
    }

    fun inRoomDialog(){
        _editDialog.postValue(true)
    }

    fun setEditDialog(type: Boolean){
        _editDialog.postValue(type)
    }

    fun goDuelPage(type: Boolean){
        _goDuel.postValue(type)
    }

    fun addRoom(){
        _showWaitDialog.postValue(true)
        val roomKey = String.format("%04d", roomRandom())
        DuelDataRepository.repository.getData(roomKey){ result ->
            result?.let {

                if (it["userId"] == null && it["otherId"] == null){
                    addRoomData(roomKey)
                    goDuelPage(true)
                } else {
                    addRoom()
                }
            } ?: run {
                addRoomData(roomKey)
                goDuelPage(true)
            }

            _showWaitDialog.postValue(false)
        }
    }

    private fun addRoomData(roomKey: String){
        _roomKey = roomKey
        val duel = DuelModel(
            userId = token.value,
            otherId = null,
        )

        DuelDataRepository.repository.addData(roomKey, duel)
    }

    private fun roomRandom(): Int {
        return (Math.random()* GameSet.MAX_RANDOM).toInt()
    }

    fun findRoom(id: String){
        DuelDataRepository.repository.getData(id){ result ->
            result?.let {

                if (it["userId"] != null){
                    _roomKey = id
                    goDuelPage(true)
                }
            }
        }
    }


}