package com.kanneki.gamerps.ui.duel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kanneki.gamerps.GameApp
import com.kanneki.gamerps.R
import com.kanneki.gamerps.model.DuelModel
import com.kanneki.gamerps.repository.DuelDataRepository
import com.kanneki.gamerps.utils.GameSet

class DuelViewModel : ViewModel() {

    val _selectItem = MutableLiveData<Int>(GameSet.NOT)
    val selectItem: LiveData<Int> get() = _selectItem
    val otherSelectItem = MutableLiveData(GameSet.NOT)
    val roomKey = MutableLiveData<String>()
    val getRoomKey: LiveData<String> get() = roomKey
    val contextMessage = MutableLiveData<String>()
    private val _back = MutableLiveData<Boolean>(false)
    val getBack get() = _back
    var isRoomHost = false
    private val _duelProcess = MutableLiveData<Int>(DuelType.DUEL_WAIT_START)
    val duelProcess:LiveData<Int> get() = _duelProcess
    val nextGameButtonShow = MutableLiveData(false)
    private var reStart = false
    val gameWinLoss = MutableLiveData<String>()

    fun setBack(type: Boolean){
        _back.postValue(type)
    }

    fun setGameWL(str: String){
        gameWinLoss.postValue(str)
    }

    fun setRestart(type: Boolean){
        reStart = type
    }

    fun getRestart() = reStart

    fun setSelectItem(type: Int){
        if (duelProcess.value == DuelType.DUEL_WAIT_SELECT){
            _selectItem.postValue(type)

            if (type != GameSet.NOT){
                val keyWord = if (isRoomHost) DuelModel.userSelect else DuelModel.otherSelect
                val data = mapOf(keyWord to true)
                DuelDataRepository.repository.updateData(getRoomKey.value ?: "", data){}
            }
        }
    }

    fun setRoomKey(id: String){
        roomKey.postValue(id)
    }

    fun setContextMessage(message: String){
        contextMessage.postValue(message)
    }

    fun setDuelProcess(type: Int){
        _duelProcess.postValue(type)
    }

    fun updateSelect(){
        val keyWord = if (isRoomHost) DuelModel.userSelectItem else DuelModel.otherSelectItem
        val data = mapOf(keyWord to selectItem.value)
        DuelDataRepository.repository.updateData(getRoomKey.value ?: "", data){}
    }

    fun judgmentGame(userSelect: Int, otherSelect: Int){
        val mySelect = if (isRoomHost) userSelect else otherSelect
        val oSelect = if (!isRoomHost) userSelect else otherSelect
        val judgment = GameSet.gameSet(mySelect, oSelect)

        val str = when(judgment){
            GameSet.DUEL_WIN -> GameApp.getContext().getString(R.string.gameWin)
            GameSet.DUEL_LOSS -> GameApp.getContext().getString(R.string.gameLoss)
            GameSet.DUEL_SAFE -> GameApp.getContext().getString(R.string.gameTid)
            else -> GameApp.getContext().getString(R.string.gamePleseItem)
        }

        otherSelectItem.postValue(oSelect)
        contextMessage.postValue(str)
        _duelProcess.postValue(DuelType.DUEL_END_GAME)
    }

    fun setNextGame(type: Int){
        nextGameButtonShow.postValue(type == DuelType.DUEL_END_GAME)
    }

    fun nextGame(){
        _selectItem.postValue(GameSet.NOT)
        otherSelectItem.postValue(GameSet.NOT)
        _duelProcess.postValue(DuelType.DUEL_WAIT_START)
        setRestart(true)

        if (isRoomHost){
            val data = mapOf(
                DuelModel.userSelect to false,
                DuelModel.userSelectItem to 0
            )
            DuelDataRepository.repository.updateData(getRoomKey.value ?: "", data){}
        } else {
            val data = mapOf(
                DuelModel.otherSelect to false,
                DuelModel.otherSelectItem to 0
            )
            DuelDataRepository.repository.updateData(getRoomKey.value ?: "", data){}
        }
    }
}

class DuelType{
    companion object{
        const val DUEL_WAIT_START = 900
        const val DUEL_WAIT_SELECT = 901
        const val DUEL_END_GAME = 902
    }
}