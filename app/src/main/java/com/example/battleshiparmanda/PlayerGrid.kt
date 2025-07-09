package com.example.battleshiparmanda

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import kotlin.math.abs


@Composable
fun DrawGrid(gameViewModel: GameViewModel, for_player:Player,modifier:Modifier=Modifier){
    val context = LocalContext.current
    val opp_player=gameViewModel.GetOppositePlayer(for_player)
    Box(modifier=Modifier
        .padding(top=45.dp)
        .onGloballyPositioned {coords->
            for_player.grid_layout_coords=coords
        }
        .then(modifier)) {
        Column(
            verticalArrangement = Arrangement.spacedBy(GRID_PADDING),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(16.dp).then(modifier)
        ) {
            for (row in 0 until for_player.grid.size) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(GRID_PADDING),
                    modifier=Modifier.align(Alignment.CenterHorizontally)
                ) {
                    for (col in 0 until for_player.grid.size) {
                        val cell_background=colorResource(if(for_player.mode.value==Mode.ATTACKING) R.color.gray else R.color.very_light_gray).copy(alpha = if(for_player.grid[row][col].cellState.value==CellState.EMPTY || for_player.grid[row][col].cellState.value==CellState.SHIP) 1f else 0f)
                        Surface(
                            onClick = {
                                if (opp_player.iscurrentplayer && opp_player.mode.value==Mode.ATTACKING) {
                                    val cur_cell = IntOffset(row, col)
                                    if (for_player.grid[row][col].cellState.value == CellState.SHIP) {
                                        //attack hit
                                        for_player.grid[row][col].cellState.value = CellState.HIT
                                        for_player.ships.find { cur_cell in it.grid_positions }?.let { ship ->
                                                ship.attacked_count.value +=1
                                            }
                                        opp_player.cur_score += HIT_SCORE

                                        for (ship in for_player.ships) {
                                            if (cur_cell in ship.grid_positions) {
                                                ship.grid_positions.remove(cur_cell)
                                                break
                                            }
                                        }
                                        Log.d("general", "FromDrawGrid:cur_player.ships:${for_player.ships}")
                                        if (opp_player.remaining_attacks.value== 1) {
                                            gameViewModel.ChangePlayerTurn()
                                        } else {
                                            opp_player.remaining_attacks.value-= 1
                                        }
                                    }
                                    else if (for_player.grid[row][col].cellState.value == CellState.EMPTY) {
                                        //attack missed
                                        for_player.grid[row][col].cellState.value = CellState.MISS
                                        opp_player.cur_score += if (for_player.cur_score >= abs(MISS_SCORE)) MISS_SCORE else 0

                                        if (opp_player.remaining_attacks.value== 1) {
                                            gameViewModel.ChangePlayerTurn()
                                        } else {
                                            opp_player.remaining_attacks.value-= 1
                                        }
                                    } else {
                                        DoVibrate(context = context)
                                    }
                                    if (IsDefeated(for_player) && for_player.mode.value != Mode.DEPLOYING && opp_player.mode.value != Mode.DEPLOYING) {
                                        //Log.d("player_won","${player.player.name}")
                                        //store new game into history
                                        PrintGrid(player = for_player)
                                        val winner_player = opp_player
                                        val loser_player = for_player
                                        if (winner_player.high_score < winner_player.cur_score) {
                                            winner_player.high_score = winner_player.cur_score
                                        }
                                        gameViewModel.AddAndSaveNewGameHistory(
                                            gameHistoryData = GameHistoryData(
                                                winner = winner_player.player,
                                                winner_player_score = winner_player.cur_score,
                                                winner_player_high_score = winner_player.high_score,
                                                loser = loser_player.player,
                                                loser_player_score = loser_player.cur_score,
                                                loser_player_high_score = loser_player.high_score
                                            ), context = context
                                        )
                                        opp_player.iswinner.value = true
                                    }
                                    //Log.d("playersample","iscross=$iscross,iscircle=$iscircle")
                                    //Log.d("alphaplayersample","${player.grid[row][col].cellState==CellState.EMPTY || player.grid[row][col].cellState==CellState.SHIP}")
                                }
                            },
                            //change backgournd color of each cell to transperent if hit or miss
                            color = cell_background,
                            shape = RoundedCornerShape(3.dp),
                            modifier = Modifier
                                .size(52.dp)
                                .shadow(32.dp, RoundedCornerShape(3.dp))
                                .onGloballyPositioned { coord ->
                                    if(for_player.grid_layout_coords!=null) {
                                        for_player.grid[row][col].offset = for_player.grid_layout_coords!!.localPositionOf(coord,Offset.Zero)
                                    }
                                }
                        ) {
                            //Log.d("playersample","iscross=$iscross,iscircle=$iscircle")
                            if(for_player.grid[row][col].cellState.value==CellState.HIT || for_player.grid[row][col].cellState.value==CellState.MISS){
                                //Log.d("playersample","${player.ships},$row,$col,yess you got it boiz")
                                Image(
                                    painter= painterResource(if(for_player.grid[row][col].cellState.value==CellState.HIT) R.drawable.redcircle else R.drawable.whitecross),
                                    contentDescription = null,
                                    contentScale = ContentScale.FillBounds,
                                    modifier=Modifier
                                        .fillMaxSize()
                                        .padding(4.dp)
                                        .zIndex(4f)
                                        .background(Color.Transparent),
                                    colorFilter = ColorFilter.tint(Color.Black.copy(alpha=if(for_player.iscurrentplayer && for_player.mode.value==Mode.ATTACKING) 0.5f else 0f), blendMode = BlendMode.SrcAtop)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
fun MakeGrid(size:Int):MutableList<MutableList<Cell>>{
    val tmp = mutableListOf<MutableList<Cell>>()
    for (i in 0 until size) {
        val row = mutableListOf<Cell>()
        for (j in 0 until size) {
            row.add(Cell(cellState = mutableStateOf(CellState.EMPTY), offset = Offset.Zero))//dont forget to update the offset to required for each player
        }
        tmp.add(row)
    }
    return tmp
}