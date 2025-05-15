package com.example.battleshiparmanda

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset

enum class Ships{
    SHIP1,
    SHIP2,
    SHIP3,
    SHIP3_1
}
data class Cell(private var state: CellState,var offset: Offset){
    var cellState by mutableStateOf(state)
}
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
val Ship_Sizes= mapOf(
    Ships.SHIP1 to 2,
    Ships.SHIP2 to 3,
    Ships.SHIP3 to 1,
    Ships.SHIP3_1 to 1
)
val ship_images= mapOf(
    Ships.SHIP1 to R.drawable.ship1 ,
    Ships.SHIP2 to R.drawable.ship2,
    Ships.SHIP3 to R.drawable.ship3,
    Ships.SHIP3_1 to R.drawable.ship3
)
//var all_ships_p1= mutableListOf(Ships.SHIP1,Ships.SHIP2,Ships.SHIP3,Ships.SHIP3_1)
//var all_ships_p2=mutableListOf(Ships.SHIP1,Ships.SHIP2,Ships.SHIP3,Ships.SHIP3_1)
//var all_ships_p1_offset= mutableMapOf(
//    Ships.SHIP1 to Offset.Zero,
//    Ships.SHIP2 to Offset.Zero,
//    Ships.SHIP3 to Offset.Zero,
//    Ships.SHIP3_1 to Offset.Zero)
//var all_ships_p2_offset= mutableMapOf(
//    Ships.SHIP1 to Offset.Zero,
//    Ships.SHIP2 to Offset.Zero,
//    Ships.SHIP3 to Offset.Zero,
//    Ships.SHIP3_1 to Offset.Zero)
var player_2_grid_start_offset=Offset.Zero
var player_1_grid_start_offset=Offset.Zero
var player_2_area_start_offset=Offset.Zero
var player_1_area_start_offset=Offset.Zero
//var all_ships_p1_coordinates= mutableMapOf(
//    Ships.SHIP1 to Pair(Offset.Zero,Orientation.HORIZONTAL),
//    Ships.SHIP2 to Pair(Offset.Zero,Orientation.HORIZONTAL),
//    Ships.SHIP3 to Pair(Offset.Zero,Orientation.HORIZONTAL),
//    Ships.SHIP3_1 to Pair(Offset.Zero,Orientation.HORIZONTAL))
//var all_ships_p2_coordinates= mutableMapOf(
//    Ships.SHIP1 to Pair(Offset.Zero,Orientation.HORIZONTAL),
//    Ships.SHIP2 to Pair(Offset.Zero,Orientation.HORIZONTAL),
//    Ships.SHIP3 to Pair(Offset.Zero,Orientation.HORIZONTAL),
//    Ships.SHIP3_1 to Pair(Offset.Zero,Orientation.HORIZONTAL))
val HIT_SCORE=100
val MISS_SCORE=-10
var tmp_all_ships_p1_offset= mutableMapOf(
    Ships.SHIP1 to Offset.Zero,
    Ships.SHIP2 to Offset.Zero,
    Ships.SHIP3 to Offset.Zero,
    Ships.SHIP3_1 to Offset.Zero)
var tmp_all_ships_p2_offset= mutableMapOf(
    Ships.SHIP1 to Offset.Zero,
    Ships.SHIP2 to Offset.Zero,
    Ships.SHIP3 to Offset.Zero,
    Ships.SHIP3_1 to Offset.Zero)
var dummy_ships_p1_coordinates= mutableMapOf(
Ships.SHIP1 to Offset.Zero,
Ships.SHIP2 to Offset.Zero,
Ships.SHIP3 to Offset.Zero,
Ships.SHIP3_1 to Offset.Zero)
var dummy_ships_p2_coordinates= mutableMapOf(
    Ships.SHIP1 to Offset.Zero,
    Ships.SHIP2 to Offset.Zero,
    Ships.SHIP3 to Offset.Zero,
    Ships.SHIP3_1 to Offset.Zero)


var all_game_history:MutableList<game_history_data> = mutableListOf()
data class game_history_data(var winner:Players,var winner_player_score:Int,var winner_player_high_score:Int,var loser_player_score:Int,var loser_player_high_score:Int)