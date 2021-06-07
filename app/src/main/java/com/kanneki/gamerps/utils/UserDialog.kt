package com.kanneki.gamerps.utils

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import com.kanneki.gamerps.R
import kotlinx.android.synthetic.main.dialog_room.*

class UserDialog(val context: Context) {

    val editDialog = AlertDialog.Builder(context)
        .setTitle(R.string.roomPassword)
        .setView(R.layout.dialog_room)
        .create()

    fun showEditDialog(callback: (String?) -> Unit){

        editDialog.setButton(Dialog.BUTTON_POSITIVE, "確認"){ _, _ ->
            callback(editDialog.editResult.text.toString())
            editDialog.dismiss()
        }

        editDialog.setButton(Dialog.BUTTON_NEGATIVE, "取消"){ _, _ ->
            callback(null)
            editDialog.dismiss()
        }

        editDialog.show()
    }

    var waitDialog = AlertDialog.Builder(context)
        .setTitle("")
        .setMessage(R.string.wait)
        .create()

    fun showWait(){
        waitDialog.show()
    }

    fun goneWait(){
        waitDialog.dismiss()
    }
}