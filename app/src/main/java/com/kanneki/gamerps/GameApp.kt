package com.kanneki.gamerps

import android.app.Application
import android.content.Context

class GameApp: Application() {

    companion object{
        var _context: Application? = null
        fun getContext(): Context{
            return _context!!
        }
    }

    override fun onCreate() {
        super.onCreate()
        _context = this
    }
}