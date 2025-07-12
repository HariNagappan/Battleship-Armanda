package com.example.battleshiparmanda

import android.R.attr.padding
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

@Composable
fun DummyDock(for_player: Player,gameViewModel: GameViewModel,modifier: Modifier=Modifier){
    val opp_player=gameViewModel.GetOppositePlayer(cur_player = for_player)
    if(for_player.mode.value!= Mode.DEPLOYING && opp_player.mode.value!=Mode.DEPLOYING) {
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier=Modifier
                .fillMaxWidth()
                .padding(top= dimensionResource(R.dimen.med_padding))) {
            for_player.ships.forEach { ship ->
                DummyShip(
                    ship = ship,
                    for_player = for_player,
                    gameViewModel = gameViewModel
                )
                Spacer(modifier=Modifier.width(8.dp))
            }
        }
    }
}
@Composable
fun DummyShip(ship: Ship, for_player: Player,gameViewModel: GameViewModel, modifier: Modifier=Modifier){
    val opp_player=gameViewModel.GetOppositePlayer(cur_player = for_player)
    var should_gray_out=(ship.grid_positions.isEmpty() && for_player.mode.value!= Mode.DEPLOYING && opp_player.mode.value!=Mode.DEPLOYING)
    val matrix = ColorMatrix().apply { setToSaturation(if(should_gray_out) 0f else 1f) } // 0f = greyscale
    val paint = ColorFilter.colorMatrix(matrix)
    val orgsizex = CELL_SIZE * ship.shipType.size + 8.dp * (ship.shipType.size - 1)
    val orgsizey= CELL_SIZE
    val aspect_ratio=0.6f
    LaunchedEffect(ship.attacked_count.value) {
        if(ship.attacked_count.value>0){
            should_gray_out=(ship.grid_positions.isEmpty() && for_player.mode.value!= Mode.DEPLOYING && opp_player.mode.value!=Mode.DEPLOYING)
        }
    }
    Box(
        modifier = Modifier.size(orgsizex*aspect_ratio, orgsizey*aspect_ratio)
    ) {
        Image(
            painter = painterResource(ship.shipType.img_path),
            contentDescription = "Ship",
            contentScale = ContentScale.FillBounds,
            colorFilter = paint
        )
    }
}