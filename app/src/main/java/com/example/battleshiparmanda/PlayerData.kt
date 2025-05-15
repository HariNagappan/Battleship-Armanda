package com.example.battleshiparmanda

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset

class Player(
    val player: Players=Players.PLAYER1,
    var grid: MutableList<MutableList<Cell>> = mutableListOf<MutableList<Cell>>(),
//    var ships: MutableMap<Ships, MutableList<Pair<Int, Int>>> = mutableMapOf(
//        Ships.SHIP1 to mutableListOf(),
//        Ships.SHIP2 to mutableListOf(),
//        Ships.SHIP3 to mutableListOf(),
//        Ships.SHIP3_1 to mutableListOf(),
//        ),
    var ships:MutableList<Ship>,
    private var iscurrent:Boolean=true,
    private var isattack:Boolean=true,
    private var remaining:Int=3,
    var cur_score:Int=0,
    private var isdeploy:Boolean=true,
    var iswinner:Boolean=false,
    var high_score:Int=0//update this in start of the game by shared prefs
){
    val grid_size=grid.size
    var remaining_attacks by mutableStateOf(remaining)
    var iscurrentplayer by mutableStateOf(iscurrent)
    var isattacking by mutableStateOf(isattack)
    var isdeploying by mutableStateOf(isdeploy)
    val shipBackupOffsets = mutableMapOf<Ship, Offset>()
    val shipBackupOrientations = mutableMapOf<Ship, Orientation>()
}
//change required variableto mutablestate
class Ship(val ship: Ships,
           val size:Int,
           var isvisi:Boolean=true,
           var isattacked:Boolean=false,
           var start_position_in_screen:Pair<Pair<Float,Float>,Orientation> =Pair(Pair(0f,0f),Orientation.HORIZONTAL),
           var positions_in_grid:MutableList<Pair<Int,Int>> = mutableListOf(),
           private var off:Offset=Offset.Zero)
{
    var img_offset by mutableStateOf(off)
    var tmp_start_position_in_screen by mutableStateOf(start_position_in_screen)
    var isvisible by mutableStateOf(isvisi)
}