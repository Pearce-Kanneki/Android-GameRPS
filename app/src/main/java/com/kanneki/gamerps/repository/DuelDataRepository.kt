package com.kanneki.gamerps.repository

import android.util.Log
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.kanneki.gamerps.model.DuelModel

class DuelDataRepository {

    companion object {
        const val collectionKey = "RPS"
        const val RESULT_OK = 100
        const val RESULT_FAIL = 101
        val repository = DuelDataRepository()
    }

    val db = Firebase.firestore
    var registration:ListenerRegistration? = null

    fun getData(gameId: String, callback:(Map<String, Any>?) -> Unit){

        db.collection(collectionKey)
            .document(gameId)
            .get()
            .addOnSuccessListener { result ->
                callback(result.data)

            }
            .addOnFailureListener { e ->
                callback(null)
            }
    }

    fun snapshotData(gameId: String, callback: (Map<String, Any>?) -> Unit){

        registration = db.collection(collectionKey)
            .document(gameId)
            .addSnapshotListener { value, error ->
                callback(value?.data)
            }
    }

    fun stopSnapshot(){
        registration?.remove()
    }

    fun addData(gameId: String, data: DuelModel){
        db.collection(collectionKey)
            .document(gameId)
            .set(data)

    }

    fun updateData(gameId: String, data: Map<String, Any?>, callback: (Boolean) -> Unit){
        db.collection(collectionKey)
            .document(gameId)
            .update(data)
            .addOnSuccessListener {
                callback(true)
            }
            .addOnFailureListener {
                callback(false)
            }
    }
}