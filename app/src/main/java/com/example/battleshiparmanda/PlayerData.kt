package com.example.battleshiparmanda

import android.R.attr.orientation
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.unit.IntOffset

data class Player(
    val player: Players=Players.PLAYER1,
    var grid: MutableList<MutableList<Cell>> = mutableListOf<MutableList<Cell>>(),
    var grid_layout_coords: LayoutCoordinates?=null,
    var ships:MutableList<Ship>,
    var iscurrentplayer:Boolean=true,
    var remaining_attacks:MutableState<Int> =mutableStateOf(3),
    var cur_score:Int=0,
    var mode: MutableState<Mode>,
    var iswinner: MutableState<Boolean> = mutableStateOf(false),
    var high_score:Int=0,
    var reset_ships_to_prev: MutableState<Int> =mutableStateOf(0),
    var health: MutableState<Float> =mutableStateOf(1f)

)
data class Ship(val shipType: ShipType,
                var isvisible:Boolean=true,
                var attacked_count:MutableState<Int> =mutableIntStateOf(0),
                var prev_offset: Offset=Offset.Zero,
                var prev_orientation: Orientation= Orientation.HORIZONTAL,
                var tmp_offset:Offset=prev_offset,
                var tmp_orientation: Orientation = prev_orientation,
                val prev_pos_in_screen: Offset=Offset.Zero,
                var tmp_pos_in_screen: Offset=Offset.Zero,
                var grid_positions:MutableList<IntOffset> = mutableListOf(),
                var org_pos_in_screen: Offset= Offset.Zero,
                var org_relative_grid_pos: Offset= Offset.Zero,
                var tmp_ship_grid_start_idx: IntOffset= IntOffset.Zero,
                )