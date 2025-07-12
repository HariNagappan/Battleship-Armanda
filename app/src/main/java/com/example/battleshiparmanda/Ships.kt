package com.example.battleshiparmanda

import android.graphics.Paint
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.foundation.layout.Row
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextPainter.paint
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

@Composable
fun ShipDock(for_player: Player,gameViewModel: GameViewModel,modifier:Modifier=Modifier){
    //Row(modifier=Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
    Box(modifier=Modifier.fillMaxWidth()){
        //Row(modifier=Modifier.align(Alignment.Center)) {
            //TODO Fix Row Bug
            for_player.ships.forEach { ship ->
                MovableShip(
                    ship = ship,
                    gameViewModel = gameViewModel,
                    for_player = for_player,
                    cell_size = CELL_SIZE,
                    padding = 8.dp,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        //}
    }
}

@Composable
fun MovableShip(ship:Ship,for_player: Player,gameViewModel: GameViewModel,cell_size:Dp,padding: Dp,modifier:Modifier=Modifier){
    val shipLength = ship.shipType.size
    val orgsizex = cell_size * shipLength + padding * (shipLength - 1)
    val opp_player=gameViewModel.GetOppositePlayer(cur_player = for_player)
    val orgsizey= cell_size
    var should_gray_out=(ship.grid_positions.isEmpty() && for_player.mode.value!= Mode.DEPLOYING && opp_player.mode.value!=Mode.DEPLOYING)
    val matrix = ColorMatrix().apply { setToSaturation(if(should_gray_out) 0f else 1f) } // 0f = greyscale
    val paint = ColorFilter.colorMatrix(matrix)

    var tmp_ship_offset by remember{mutableStateOf(ship.prev_offset)}
    var tmp_orientation by remember { mutableStateOf(ship.tmp_orientation) }
    val localdensity=LocalDensity.current
    var got_org_coord = remember{false}
    var xidx = remember { 0 }
    var yidx = remember { 0 }
    var ship_coords: LayoutCoordinates?=remember{ null }
    val context = LocalContext.current
    LaunchedEffect(for_player.reset_ships_to_prev.value) {
        if(ship.attacked_count.value==0){
            tmp_ship_offset=ship.prev_offset
            tmp_orientation=ship.prev_orientation
            Log.d("general","reset ships executed2:$tmp_ship_offset")
            ship.tmp_offset=ship.prev_offset
        }
    }
    LaunchedEffect(ship.attacked_count.value) {
        should_gray_out=(ship.grid_positions.isEmpty() && for_player.mode.value!= Mode.DEPLOYING && opp_player.mode.value!=Mode.DEPLOYING)
    }
    if(ship.isvisible || should_gray_out) {
        Box(
            modifier = Modifier
                .size(orgsizex, orgsizey)
                .graphicsLayer {
                    translationX = tmp_ship_offset.x
                    translationY = tmp_ship_offset.y
                    rotationZ = if(tmp_orientation== Orientation.VERTICAL) 90f else 0f
                }
                .onGloballyPositioned{coord->
                    if(for_player.grid_layout_coords!=null) {
                        ship_coords=coord
                        if(got_org_coord==false){
                            ship.org_pos_in_screen = coord.localToWindow(Offset.Zero)
                            ship.org_relative_grid_pos = for_player.grid_layout_coords!!.localPositionOf(coord, Offset.Zero)
                            got_org_coord=true
                        }
                    }
                }
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = {
                            if(ship.attacked_count.value==0){
                                Log.d("general","dragstart:${gameViewModel.cur_player.reset_ships_to_prev}")
                            }
                        },
                        onDrag = { change, dragAmount ->
                            if(ship.attacked_count.value==0) {
                                val corrected = if (tmp_orientation == Orientation.VERTICAL) {
                                    Offset(-dragAmount.y, dragAmount.x)
                                } else {
                                    dragAmount
                                }
                                tmp_ship_offset += corrected

                                change.consume()
                            }
                        },
                        onDragEnd = {
                            if(for_player.grid_layout_coords!=null && ship.attacked_count.value==0) {
                                var rel_ship_coords = Offset.Zero
                                ship.tmp_offset = tmp_ship_offset
                                val stride =
                                    with(localdensity) { CELL_SIZE.toPx() } + with(localdensity) { GRID_PADDING.toPx() }

                                if (tmp_orientation == Orientation.VERTICAL)
                                    rel_ship_coords =
                                        for_player.grid_layout_coords!!.localPositionOf(
                                            ship_coords!!,
                                            Offset(0f, ship_coords.size.height.toFloat())
                                        )
                                else
                                    rel_ship_coords =
                                        for_player.grid_layout_coords!!.localPositionOf(
                                            ship_coords!!,
                                            Offset.Zero
                                        )
                                xidx = (rel_ship_coords.x / with(localdensity) { (CELL_SIZE + GRID_PADDING).toPx() }).roundToInt()
                                yidx = (rel_ship_coords.y / with(localdensity) { (CELL_SIZE + GRID_PADDING).toPx() }).roundToInt()
                                //TODO change this to snap
                                val start_idx=IntOffset(yidx, xidx)
                                ship.tmp_ship_grid_start_idx = start_idx
//                                if (IsInBoundary(
//                                        start_idx = start_idx,
//                                        orientation = tmp_orientation,
//                                        for_player = for_player,
//                                        ship_size = ship.shipType.size
//                                    )
//                                ) {
//                                    if(tmp_orientation== Orientation.HORIZONTAL){
//                                        tmp_ship_offset = for_player.grid[start_idx.x][start_idx.y].offset-ship.org_relative_grid_pos
//                                    }
//                                    else{
//                                        tmp_ship_offset = for_player.grid[start_idx.x][start_idx.y].offset-ship.org_relative_grid_pos
//                                    }
//                                } else {
//                                    tmp_ship_offset = ship.prev_offset
//                                    tmp_orientation = ship.prev_orientation
//                                    ship.tmp_offset=tmp_ship_offset
//                                    ship.tmp_orientation=tmp_orientation
//                                }
//                                Log.d("general", "xidx:$xidx,yidx:$yidx")
                            }
                            /*
                            if (IsValidShip(
                                        start_idx = IntOffset(xidx, yidx),
                                        for_player = for_player,
                                        ship = ship
                                    )
                                ) {
                                    //Log.d("general","yes valid ship,matching: $idx")
                                    if (tmp_orientation == Orientation.HORIZONTAL) {
                                        tmp_ship_offset =
                                            for_player.grid[yidx][xidx].offset - ship.org_relative_grid_pos
                                        Log.d(
                                            "general",
                                            "orientation of ship is horizontal,tmp_ship_offset:$tmp_ship_offset"
                                        )
                                    } else {
                                        tmp_ship_offset = for_player.grid[yidx][xidx].offset - ship.org_relative_grid_pos-Offset(with(localdensity) { (CELL_SIZE + GRID_PADDING).toPx() },-with(localdensity) { (CELL_SIZE + GRID_PADDING).toPx() })
                                        Log.d(
                                            "general",
                                            "orientation of ship is vertical,tmp_ship_offset:$tmp_ship_offset"
                                        )
                                    }
                                    //ship.tmp_offset=tmp_ship_offset
                                } else {
                                    tmp_ship_offset = ship.tmp_offset
                                    Log.d("general", "no invalid ship")
                                }
                                Log.d("general", "${ship.shipType} moved to $tmp_ship_offset")
                                ship.tmp_offset = tmp_ship_offset
                                Log.d(
                                    "general",
                                    "ship:${ship.shipType},tmp_top_left_screen_position:${ship.tmp_offset}"
                                )
                                */
                        },
                    )
                }
                .clickable(
                    //enabled = ship.attacked_count.value==0,
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }) {
                    if(ship.attacked_count.value==0) {
                        tmp_orientation =
                            if (tmp_orientation == Orientation.VERTICAL) Orientation.HORIZONTAL else Orientation.VERTICAL
                        ship.tmp_orientation = tmp_orientation
                        Log.d("general", "clicked:$ship_coords")
                    }
                    else{
                        DoVibrate(context = context)
                    }
                }
                .then(modifier)
        ) {
            Image(
                painter = painterResource(ship.shipType.img_path),
                contentDescription = null,
                contentScale = ContentScale.FillBounds,
                colorFilter = paint,

            )
        }
    }
}
