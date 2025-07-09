package com.example.battleshiparmanda

import android.R.attr.data
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.dp
import java.lang.ProcessBuilder.Redirect.to
import kotlin.to

sealed class ShipType(val size:Int,val img_path:Int){
    object Ship1:ShipType(size=2,img_path=R.drawable.ship1)
    object Ship2:ShipType(size=3,img_path=R.drawable.ship2)
    object Ship3:ShipType(size=1,img_path=R.drawable.ship3)
    object Ship3_1:ShipType(size=1,img_path=R.drawable.ship3)
}
data class Cell(var cellState: MutableState<CellState>, var offset: Offset)
enum class CellState{
    EMPTY,
    SHIP,
    MISS,
    HIT
}
enum class Orientation{
    HORIZONTAL,
    VERTICAL
}
enum class Players{
    PLAYER1,
    PLAYER2
}
enum class screens{
    HOME,
    SETTINGS,
    GAME
}
enum class Mode{
    DEPLOYING,
    ATTACKING,
    FORTIFYING,
    NONE//used when other player is current player
}
val HIT_SCORE=100
val MISS_SCORE=-10
val GRID_PADDING=8.dp
val CELL_SIZE=52.dp
val GRID_SIZE=5
val TOTAL_SHIP_COUNT=7
data class GameHistoryData(var winner:Players, var winner_player_score:Int, var winner_player_high_score:Int, val loser: Players, var loser_player_score:Int, var loser_player_high_score:Int)