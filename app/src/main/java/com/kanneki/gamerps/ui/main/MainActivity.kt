package com.kanneki.gamerps.ui.main

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.kanneki.gamerps.GameApp
import com.kanneki.gamerps.R
import com.kanneki.gamerps.databinding.ActivityMainBinding
import com.kanneki.gamerps.utils.UserDialog
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private val mainBinding: ActivityMainBinding by lazy {
        DataBindingUtil.setContentView(this, R.layout.activity_main)
    }

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

       GlobalScope.launch {
           viewModel.findUserToken()
       }

        with(mainBinding){
            lifecycleOwner = this@MainActivity
            vm = viewModel
        }
    }

    override fun onStart() {
        super.onStart()

        val dialog = UserDialog(this)

        viewModel.showWaitDialog.observe(this, { show ->
            if (show) dialog.showWait() else dialog.goneWait()
        })
    }


}