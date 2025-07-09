package com.example.battleshiparmanda

import android.R.attr.mode
import android.content.Context
import android.util.Log
import android.util.Log.i
import android.widget.Switch
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.roundToInt

class GameViewModel: ViewModel() {
    val player1= Player(player = Players.PLAYER1,
        grid =  MakeGrid(GRID_SIZE),
        ships = mutableListOf(
            Ship(shipType= ShipType.Ship1),
            Ship(shipType= ShipType.Ship2),
            Ship(shipType= ShipType.Ship3),
            Ship(shipType= ShipType.Ship3_1)),
        iscurrentplayer = true,
        remaining_attacks = mutableStateOf(3),
        cur_score = 0,
        mode = mutableStateOf(Mode.DEPLOYING))
    val player2= Player(player = Players.PLAYER2,
        grid =  MakeGrid(GRID_SIZE),
        ships = mutableListOf(
            Ship(shipType= ShipType.Ship1),
            Ship(shipType= ShipType.Ship2),
            Ship(shipType= ShipType.Ship3),
            Ship(shipType= ShipType.Ship3_1)),
        iscurrentplayer = false,
        remaining_attacks = mutableStateOf(3),
        cur_score = 0,
        mode = mutableStateOf(Mode.DEPLOYING))
    var cur_player=player1
    var opp_player=player2
    var history_list = mutableListOf<GameHistoryData>()

    fun GetPlayersHistoryAndSetHighScore(context: Context){
        val sharedPref = context.getSharedPreferences(context.getString(R.string.shared_pref_filename), Context.MODE_PRIVATE)
        val json = sharedPref.getString(context.getString(R.string.player_history_key), "")
        val type = object : TypeToken<MutableList<GameHistoryData>>() {}.type
        if(json!=""){
            history_list= Gson().fromJson(json, type)
        }
        player1.high_score= get_player_high_score(Players.PLAYER1,history_list)
        player2.high_score= get_player_high_score(Players.PLAYER2,history_list)
    }
    fun ChangePlayerTurn(){
        if(player1.mode.value==Mode.DEPLOYING){
            player1.iscurrentplayer=false
            player2.iscurrentplayer=true
            player1.mode.value= Mode.NONE
            player2.mode.value= Mode.DEPLOYING
            cur_player=player2
            opp_player=player1
        }
        else{
            if(player1.iscurrentplayer){
                player1.iscurrentplayer=false
                player1.mode.value= Mode.NONE
                player2.mode.value= Mode.ATTACKING
                player2.iscurrentplayer=true
                player2.remaining_attacks.value=3
                cur_player=player2
                opp_player=player1
            }
            else{
                player2.iscurrentplayer=false
                player2.mode.value= Mode.NONE
                player1.mode.value= Mode.ATTACKING
                player1.iscurrentplayer=true
                player1.remaining_attacks.value=3
                cur_player=player1
                opp_player=player2
            }
        }
    }
    fun SwitchToAttackOrFortify(cur_mode: Mode){
        if(cur_mode== Mode.ATTACKING){
            cur_player.mode.value= Mode.FORTIFYING
        }
        else if(cur_mode== Mode.FORTIFYING){
            cur_player.mode.value= Mode.ATTACKING
        }
    }
    fun PossibleToPlaceShips(cell_size: Dp,localdensity: Density):Boolean{
        cur_player.ships.forEach { ship->

        }
        return true
    }
    fun CopyTmpToOrignalCoordinates(player: Player){
        player.ships.forEach { ship->
            ship.prev_offset=ship.tmp_offset
            ship.prev_orientation=ship.tmp_orientation
        }
    }
    fun CopyOriginalToTmpCoordinates(player: Player){
        player.ships.forEach {ship->
            ship.tmp_offset=ship.prev_offset
            ship.tmp_orientation=ship.prev_orientation

        }
    }
    fun AddAndSaveNewGameHistory(gameHistoryData: GameHistoryData, context: Context){
        val sharedPref = context.getSharedPreferences(context.getString(R.string.shared_pref_filename), Context.MODE_PRIVATE)
        history_list.add(gameHistoryData)
        val editor = sharedPref.edit()
        val gson= Gson()
        val json=gson.toJson(history_list)
        editor.putString(context.getString(R.string.player_history_key),json)
        editor.apply()
    }
    fun ResetGame(){
        ResetGameForPlayer(player=player1)
        ResetGameForPlayer(player=player2)
        player1.iscurrentplayer=true
        player2.iscurrentplayer=false
        player1.remaining_attacks.value=3
        player2.remaining_attacks.value=3
    }
    private fun ResetGameForPlayer(player: Player){
        player.ships.clear()
        player.ships.addAll(listOf(Ship(shipType= ShipType.Ship1),
            Ship(shipType= ShipType.Ship2),
            Ship(shipType= ShipType.Ship3),
            Ship(shipType= ShipType.Ship3_1))
        )
        player.grid_layout_coords=null
        player.iswinner.value=false
        player.cur_score=0
        player.mode.value=Mode.DEPLOYING
        player.health.value=1f
        ClearGrid(player=player)
    }
    fun ResetShipsToPreviousPosition(player: Player){
        player.reset_ships_to_prev.value+=1
    }
    fun GetOppositePlayer(cur_player: Player): Player{
        if(cur_player.player== Players.PLAYER1){
            return player2
        }
        return player1
    }
    fun RegisterShips(player: Player){
        player.ships.forEach { ship->
            val grid_positions=GetGridPositionsFromStartIndex(start_position = ship.tmp_ship_grid_start_idx,ship=ship)
            for(pos in grid_positions){
                player.grid[pos.x][pos.y].cellState.value= CellState.SHIP
            }
            ship.grid_positions=grid_positions
        }
    }
    fun DeRegisterOldShips(player: Player){
        player.ships.forEach {ship->
            if(ship.attacked_count.value==0) {
                for (pos in ship.grid_positions) {
                    player.grid[pos.x][pos.y].cellState.value= CellState.EMPTY
                }
            }
        }
    }
    fun ClearGrid(player:Player){
        player.grid.let { it.forEach { col->
            col.forEach { cell->
                cell.cellState.value= CellState.EMPTY
            }
        } }
    }
}