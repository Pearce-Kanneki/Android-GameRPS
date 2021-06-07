package com.kanneki.gamerps.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.kanneki.gamerps.R
import com.kanneki.gamerps.databinding.FragmentHomeBinding
import com.kanneki.gamerps.ui.duel.DuelFragment
import com.kanneki.gamerps.ui.main.MainViewModel
import com.kanneki.gamerps.utils.UserDialog
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private lateinit var mBinding: FragmentHomeBinding
    private val viewModel: HomeViewModel by viewModels()
    private val mainViewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        mBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_home,
            container,
            false
        )

        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(mBinding){
            lifecycleOwner = this@HomeFragment
            vm = viewModel
        }
    }

    override fun onStart() {
        super.onStart()

        viewModel.goDuelData.observe(viewLifecycleOwner,{ type ->

            if (type){
                val bundle = Bundle().apply {
                    putString(DuelFragment.TYPE_CODE, viewModel.getRoomKey())
                }
                findNavController().navigate(R.id.action_homeFragment_to_duelFragment, bundle)
                viewModel.goDuelPage(false)
            }
        })

        viewModel.showWaitDialog.observe(viewLifecycleOwner, { show ->
            mainViewModel.setWaitDialog(show)
        })
        viewModel.editDialog.observe(viewLifecycleOwner, { show ->
            if (show){
                UserDialog(requireContext()).showEditDialog { data ->
                    data?.let {
                        viewModel.findRoom(it)
                    }
                }
                viewModel.setEditDialog(false)
            }
        })
        mainViewModel.token.observe(viewLifecycleOwner, { token ->
            viewModel.setToken(token)
        })
    }
}