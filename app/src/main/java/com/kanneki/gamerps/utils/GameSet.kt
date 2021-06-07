package com.kanneki.gamerps.utils

object GameSet {

    const val NOT = 0
    const val SCISSORE = 1
    const val ROCK = 2
    const val PAPER = 3

    const val MAX_RANDOM = 9999

    // game set
    const val DUEL_WIN = 101
    const val DUEL_LOSS = 102
    const val DUEL_SAFE = 103

    fun gameSet(mySelect: Int, otherSelect: Int): Int {
        return when{
            (mySelect == otherSelect)-> DUEL_SAFE
            (mySelect - otherSelect == 1) -> DUEL_WIN
            (mySelect - otherSelect == -2) -> DUEL_WIN
            else -> DUEL_LOSS
        }



    }
}