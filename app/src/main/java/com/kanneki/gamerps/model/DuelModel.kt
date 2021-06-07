package com.kanneki.gamerps.model

data class DuelModel(
    var userId: String?,
    var otherId: String?,
    var userSelect: Boolean = false,
    var otherSelect: Boolean = false,
    var userSelectItem: Int = 0,
    var otherSelectItem: Int = 0,
    var userWin: Int = 0,
    var otherWin: Int = 0,
    var nextGame: Boolean = false
){
    companion object{
        const val userId = "userId"
        const val otherId = "otherId"
        const val userSelect = "userSelect"
        const val otherSelect = "otherSelect"
        const val userSelectItem = "userSelectItem"
        const val otherSelectItem = "otherSelectItem"
        const val userWin = "userWin"
        const val otherWin = "otherWin"
        const val nextGame = "nextGame"
    }
}
