package com.kanneki.gamerps.ui.duel

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.kanneki.gamerps.R
import com.kanneki.gamerps.databinding.FragmentDuelBinding
import com.kanneki.gamerps.model.DuelModel
import com.kanneki.gamerps.repository.DuelDataRepository
import com.kanneki.gamerps.ui.main.MainViewModel
import com.kanneki.gamerps.utils.GameSet

class DuelFragment : Fragment() {

    companion object{
        const val TYPE_CODE = "roomKey"
    }

    private lateinit var mBinding:FragmentDuelBinding
    private val viewModel: DuelViewModel by viewModels()
    private val mainViewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        mBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_duel,
            container,
            false
        )

        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.apply {
            getString(TYPE_CODE)?.let { viewModel.setRoomKey(it) }
        }
        viewModel.duelProcess.observe(viewLifecycleOwner, { type ->
            when(type){
                DuelType.DUEL_WAIT_START -> {
                    viewModel.setContextMessage(getString(R.string.gameWaitGame))
                }
                DuelType.DUEL_WAIT_SELECT ->{
                    viewModel.setContextMessage(getString(R.string.gamePleseItem))
                }
            }
        })
        viewModel.getBack.observe(viewLifecycleOwner, {type ->
            if (type){ findNavController().popBackStack() }
        })
        viewModel.duelProcess.observe(viewLifecycleOwner, {type ->
            viewModel.setNextGame(type)
        })
        with(mBinding){
            lifecycleOwner = this@DuelFragment
            vm = viewModel
        }
    }

    override fun onStart() {
        super.onStart()

        viewModel.getRoomKey.observe(viewLifecycleOwner, { key ->
            DuelDataRepository.repository.snapshotData(key) { data ->
                data?.let { noEmptyData ->
                    if (!viewModel.isRoomHost) {
                        viewModel.isRoomHost =
                            (noEmptyData[DuelModel.userId] == mainViewModel.token.value)
                    }
                    setWin(
                        noEmptyData[DuelModel.userWin].toString(),
                        noEmptyData[DuelModel.otherWin].toString()
                    )

                    when {
                        noEmptyData[DuelModel.otherId] == null && !viewModel.isRoomHost -> {
                            inRoom(key)
                        }
                        noEmptyData[DuelModel.otherSelect] == false &&
                                noEmptyData[DuelModel.userSelect] == false &&
                                viewModel.getRestart() -> {
                            viewModel.setDuelProcess(DuelType.DUEL_WAIT_SELECT)
                            viewModel.setRestart(false)
                        }
                        noEmptyData[DuelModel.userId] != null &&
                                noEmptyData[DuelModel.otherId] == null -> {
                            viewModel.setDuelProcess(DuelType.DUEL_WAIT_START)
                        }
                        noEmptyData[DuelModel.otherId] != null &&
                                viewModel.duelProcess.value != DuelType.DUEL_WAIT_SELECT &&
                                viewModel.duelProcess.value != DuelType.DUEL_END_GAME &&
                                !viewModel.getRestart() -> {
                            viewModel.setDuelProcess(DuelType.DUEL_WAIT_SELECT)
                        }
                        noEmptyData[DuelModel.userSelect] == true &&
                                noEmptyData[DuelModel.otherSelect] == true &&
                                viewModel.duelProcess.value != DuelType.DUEL_END_GAME -> {
                            if (noEmptyData[DuelModel.userSelectItem].toString() == GameSet.NOT.toString() ||
                                noEmptyData[DuelModel.otherSelectItem].toString() == GameSet.NOT.toString()
                            ) {
                                viewModel.updateSelect()
                            } else {
                                judgmentGame(key, noEmptyData)
                            }
                        }
                    } // end when

                }
            }
        })

    }

    private fun setWin(myWin: String, otherWin: String){
        val myWinStr = if (viewModel.isRoomHost) { myWin } else { otherWin }
        val otherWinStr = if (!viewModel.isRoomHost) { myWin } else { otherWin }
        viewModel.setGameWL(
            resources.getString(
                R.string.game_win_loss,
                myWinStr,
                otherWinStr
            )
        )
    }

    private fun judgmentGame(roomKey: String, data: Map<String, Any>){
        val user = data[DuelModel.userSelectItem].toString().toInt()
        val other = data[DuelModel.otherSelectItem].toString().toInt()

        if (viewModel.isRoomHost){
            val game = GameSet.gameSet(user, other)
            when(game){
                GameSet.DUEL_WIN -> {
                    val newData = mapOf(DuelModel.userWin to data[DuelModel.userWin].toString().toInt() +1)
                    DuelDataRepository.repository.updateData(roomKey, newData){}
                }
                GameSet.DUEL_LOSS -> {
                    val newData = mapOf(DuelModel.otherWin to data[DuelModel.otherWin].toString().toInt() +1)
                    DuelDataRepository.repository.updateData(roomKey, newData){}
                }
            }
        }
        viewModel.judgmentGame(user, other)
    }

    private fun inRoom(roomKey: String){
        val data = mapOf(DuelModel.otherId to mainViewModel.token.value)
        DuelDataRepository.repository.updateData(roomKey, data){ type ->
            if (type){
                viewModel.setDuelProcess(DuelType.DUEL_WAIT_SELECT)
            }
        }
    }

    override fun onStop() {
        super.onStop()
        DuelDataRepository.repository.stopSnapshot()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        val keyWord = if (viewModel.isRoomHost) DuelModel.userId else DuelModel.otherId
        viewModel.isRoomHost = true
        val data = mapOf(keyWord to null)
        DuelDataRepository.repository.updateData(viewModel.getRoomKey.value ?: "", data){

        }
    }
}