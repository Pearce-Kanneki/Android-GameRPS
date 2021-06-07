package com.kanneki.gamerps.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.createDataStore
import com.kanneki.gamerps.GameApp
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class userDataRepository {

    companion object{
        const val userToken = "token"
        const val userName = "name"

        val dataStore: DataStore<Preferences> by lazy {
            GameApp.getContext().createDataStore("setting")
        }

        private val userTokenData = stringPreferencesKey(userToken)
        private val userNameData = stringPreferencesKey(userName)

        suspend fun addDataStroe(type: String, data:String){
            dataStore.edit { setting ->
                when(type){
                    userToken -> setting[userTokenData] = data
                    userName -> setting[userNameData] = data
                }

            }
        }

        fun getDataStrore(type: String):Flow<String?> = dataStore.data.map {
            when(type){
                userToken -> it[userTokenData]
                userName -> it[userNameData]
                else -> ""
            }

        }
    }
}