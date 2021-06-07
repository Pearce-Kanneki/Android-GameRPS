package com.kanneki.gamerps.binding

import android.view.View
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import coil.load
import com.kanneki.gamerps.R
import com.kanneki.gamerps.utils.GameSet

@BindingAdapter("game_imager")
fun gameImageAdapter(view: ImageView, type:Int){

    val imager = when(type){
        GameSet.PAPER -> R.drawable.paper
        GameSet.ROCK -> R.drawable.rock
        GameSet.SCISSORE -> R.drawable.scissors
        else -> R.drawable.what
    }

    view.load(imager)
}

@BindingAdapter("setShow")
fun setShowAdapter(view: View, type: Int){

    view.visibility = if (type == GameSet.NOT) View.VISIBLE else View.INVISIBLE
}

@BindingAdapter("setGone")
fun setGoneAdapter(view: View, type: Int){

    view.visibility = if (type == GameSet.NOT) View.INVISIBLE else View.VISIBLE
}

@BindingAdapter("setBooleanShow")
fun setBooleanAdapter(view: View, type:Boolean){
    view.visibility = if (type) View.VISIBLE else View.INVISIBLE
}