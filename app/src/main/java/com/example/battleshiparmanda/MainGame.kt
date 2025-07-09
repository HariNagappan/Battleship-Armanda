package com.example.battleshiparmanda

import android.R.attr.mode
import android.R.attr.orientation
import android.R.attr.text
import android.R.attr.translationX
import android.R.attr.translationY
import android.R.attr.visible
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.text.Layout
import android.util.Log
import android.util.Log.i
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonElevation
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.times
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.consumePositionChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.fontResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextPainter.paint
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.center
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toOffset
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.squareup.kotlinpoet.INT
import kotlinx.coroutines.delay
import java.lang.System.exit
import java.util.Collections.rotate
import kotlin.collections.toMutableList
import kotlin.math.abs
import kotlin.math.absoluteValue
import kotlin.math.roundToInt

@Composable
fun MainGame(gameViewModel: GameViewModel, navController:NavController){
    var should_show_win_dialog by remember { mutableStateOf(false) }
    Box(modifier = Modifier.fillMaxSize()){
        MakeUI(gameViewModel=gameViewModel,
            navController=navController)

        LaunchedEffect(gameViewModel.player1.iswinner.value){
            if(gameViewModel.player1.iswinner.value)
                should_show_win_dialog=true
        }
        LaunchedEffect(gameViewModel.player2.iswinner.value){
            if(gameViewModel.player2.iswinner.value)
                should_show_win_dialog=true
        }
        if(should_show_win_dialog){
            WinDialog(
                gameViewModel=gameViewModel,
                navController=navController,
                ondismiss = {should_show_win_dialog=false},
                modifier=Modifier)
        }
    }
}

@Composable
fun MakeUI(gameViewModel: GameViewModel,navController: NavController){
    val localdensity= LocalDensity.current
    val p2_background by animateColorAsState(
        targetValue = if (gameViewModel.player1.iscurrentplayer) colorResource(R.color.turqoise) else colorResource(R.color.little_black),
        animationSpec = tween(durationMillis = 1000)
    )
    val p1_background by animateColorAsState(
        targetValue = if (gameViewModel.player2.iscurrentplayer) colorResource(R.color.turqoise) else colorResource(R.color.little_black),
        animationSpec = tween(durationMillis = 1000)
    )
    val yoffset by animateDpAsState(targetValue=if(gameViewModel.player1.mode.value== Mode.FORTIFYING) -140.dp else 140.dp, animationSpec = tween(durationMillis = 500,easing= LinearEasing))
    var should_show_error_dialog by remember { mutableStateOf(false) }
    
    Box(
        modifier=Modifier.fillMaxSize()
    ){
        Box(modifier=Modifier
            .align(Alignment.Center)
            .offset(
                x = 0.dp,
                y = yoffset
            )
                ) {
        }
        Column(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier.weight(1f).background(p2_background)
            ) {
                PlayerArea(
                    gameViewModel = gameViewModel,
                    for_player = gameViewModel.player2,
                    torotate = true,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            Box(
                modifier = Modifier.weight(1f).background(p1_background)
            ) {

                PlayerArea(
                    gameViewModel = gameViewModel,
                    for_player = gameViewModel.player1,
                    torotate = false,
                    modifier = Modifier.align(Alignment.Center),
                )
            }
        }
        if(gameViewModel.cur_player.mode.value== Mode.FORTIFYING || gameViewModel.cur_player.mode.value== Mode.DEPLOYING)
        {
            Box(modifier=Modifier
                .align(Alignment.Center)
                .fillMaxWidth()
                .offset(y=if(gameViewModel.player1.iscurrentplayer) -145.dp else 145.dp)) {
                Fortify(
                    gameViewModel = gameViewModel,
                    heading = if (gameViewModel.cur_player.mode.value == Mode.DEPLOYING) "DEPLOY" else "FORTIFY",
                    description = if (gameViewModel.cur_player.mode.value == Mode.DEPLOYING) "Click and Place Ships for \nthe best strategy" else "Drag and move \nundamaged ships to safety",
                    onFailure = {should_show_error_dialog=true}
                )
            }
        }
    }
        if(should_show_error_dialog){
            val curplayer=if(gameViewModel.player1.iscurrentplayer) gameViewModel.player1 else gameViewModel.player2
            //printGrid(curplayer)
            curplayer.ships.forEach {
                //Log.d("allships","player:${curplayer.player.name},${it.ship}:${it.positions_in_grid}")
            }
            AlertDialog(
                title = {
                    Text(text ="ERROR PLACING SHIPS")
                },
                text = {
                    Text(text = "Cannot place ships, Please place the ships correctly")
                },
                onDismissRequest = {
                    should_show_error_dialog=false
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            should_show_error_dialog=false
                        }
                    ) {
                        Text("OK")
                    }
                },
                dismissButton = {},
                modifier = Modifier.graphicsLayer { rotationZ=if(gameViewModel.player1.iscurrentplayer) 0f else 180f}
            )
    }

}

@Composable
fun PlayerArea(gameViewModel: GameViewModel, for_player: Player, torotate:Boolean, modifier: Modifier=Modifier){
    Box(modifier=Modifier
        .fillMaxSize()
        .graphicsLayer { rotationZ= if(torotate) 180f else 0f }
        ){
        Box(modifier=Modifier.wrapContentSize().zIndex(3f)){
            DummyDock(
                for_player=for_player,
                gameViewModel=gameViewModel
            )
            for_player.ships.forEach {ship->
                ship.isvisible=(for_player.iscurrentplayer) && for_player.mode.value!=Mode.ATTACKING
            }
            ShipDock(
                for_player=for_player,
                gameViewModel=gameViewModel)
        }
        Box(
            modifier=Modifier.align(Alignment.Center).zIndex(3f),
            contentAlignment = Alignment.Center
        )
        {
            AnimatedVisibility(
                visible = (for_player.iscurrentplayer && for_player.mode.value== Mode.ATTACKING),
                enter= slideInVertically(animationSpec = tween(durationMillis = 1000)) { fullHeight->fullHeight} + fadeIn(animationSpec = tween(durationMillis = 1000)),
                exit= slideOutVertically (animationSpec = tween( durationMillis = 1000)){fullHeight->fullHeight} + fadeOut(animationSpec = tween(durationMillis = 1000))
            ){
                Image(
                    painter = painterResource(R.drawable.cannon),
                    contentDescription = null,
                    modifier=Modifier.align(Alignment.Center).graphicsLayer {
                        rotationZ=180f
                        transformOrigin= TransformOrigin.Center}
                )
            }
        }
        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxSize()
                .then(modifier)
        ) {
            DrawGrid(gameViewModel=gameViewModel, for_player=for_player, modifier=Modifier.align(Alignment.CenterHorizontally))
            AnimatedVisibility(
                visible=for_player.iscurrentplayer,
            ) {
                BottomBar(for_player=for_player,gameViewModel=gameViewModel, remaining_attacks = if(for_player.mode.value==Mode.ATTACKING) for_player.remaining_attacks.value else 0)
            }
        }
    }
}
@Composable
fun ShipDock(for_player: Player,gameViewModel: GameViewModel,modifier:Modifier=Modifier){
        //Row(modifier=Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
        Box(modifier=Modifier.fillMaxWidth()){
            for_player.ships.forEach { ship ->
                MovableShip(
                    ship = ship,
                    gameViewModel=gameViewModel,
                    for_player = for_player,
                    cell_size = CELL_SIZE,
                    padding = 8.dp,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
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
                            //TODO error comin here layouts are not a part of same hierarchy
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
                                var rel_ship_coords=Offset.Zero
                                ship.tmp_offset=tmp_ship_offset
                                if(tmp_orientation== Orientation.VERTICAL)
                                    rel_ship_coords = for_player.grid_layout_coords!!.localPositionOf(ship_coords!!, Offset(0f,ship_coords.size.height.toFloat()))
                                else
                                    rel_ship_coords = for_player.grid_layout_coords!!.localPositionOf(ship_coords!!, Offset.Zero)
                                xidx = (rel_ship_coords.x / with(localdensity) { (CELL_SIZE + GRID_PADDING).toPx() }).roundToInt()
                                yidx = (rel_ship_coords.y / with(localdensity) { (CELL_SIZE + GRID_PADDING).toPx() }).roundToInt()
                                //TODO change this to snap
                                ship.tmp_ship_grid_start_idx= IntOffset(yidx,xidx) }
                                Log.d("general", "xidx:$xidx,yidx:$yidx")
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
                    enabled = ship.attacked_count.value==0,
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }) {
                    tmp_orientation=if(tmp_orientation== Orientation.VERTICAL) Orientation.HORIZONTAL else Orientation.VERTICAL
                    ship.tmp_orientation=tmp_orientation
                    Log.d("general","clicked:$ship_coords")
                }
                .then(modifier)
        ) {
            Image(
                painter = painterResource(ship.shipType.img_path),
                contentDescription = null,
                contentScale = ContentScale.FillBounds,
                colorFilter = paint
            )
        }
    }
}

@Composable
fun BottomBar(for_player: Player,gameViewModel: GameViewModel, remaining_attacks:Int=3,modifier: Modifier=Modifier){
    Box(
        modifier = Modifier
            .background(
                color = if (for_player.mode.value==Mode.ATTACKING) colorResource(R.color.bottomattack) else Color.White,
                shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
            )
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(start = 8.dp,end=16.dp)//,top=4.dp,bottom=4.dp)
            .then(modifier)
    ) {
        DrawCircles(
            totalcount = 3,
            remaining_attacks = remaining_attacks,
            circlesize = 26.dp,
            modifier = Modifier.align(Alignment.CenterStart).wrapContentSize()
        )
        Text(
            text =for_player.mode.value.name.toString().substring(0,for_player.mode.value.name.toString().length -3) ,
            fontSize = 28.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.align(Alignment.Center),
            color = if (for_player.mode.value==Mode.ATTACKING) Color.White else Color.Black,
            fontFamily = FontFamily(Font(R.font.bottombar)),
        )
        Switch(
            checked = (for_player.mode.value==Mode.ATTACKING),
            onCheckedChange = {
                if(for_player.remaining_attacks.value==3) {
                    if (for_player.mode.value == Mode.ATTACKING) {
                        for_player.mode.value = Mode.FORTIFYING
                    } else if (for_player.mode.value == Mode.FORTIFYING) {
                        for_player.mode.value = Mode.ATTACKING
                        gameViewModel.ResetShipsToPreviousPosition(player=for_player)
                    }
                }
            },
            modifier = Modifier.align(Alignment.CenterEnd)
        )
    }
}
@Composable
fun DrawCircles(totalcount:Int=3,remaining_attacks: Int,circlesize:Dp,modifier: Modifier=Modifier){
    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = modifier) {
        for (i in 0 until remaining_attacks) {
            Box(
                modifier=Modifier
                    .size(circlesize)
                    .background(Color.White, shape = CircleShape)
            )
        }
        for (i in 0 until totalcount - remaining_attacks) {
            Box(
                modifier=Modifier
                    .size(circlesize)
                    .background(colorResource(R.color.gray), shape = CircleShape)

            )
        }
    }
}
@Composable
fun WinDialog(gameViewModel: GameViewModel, navController: NavController,ondismiss:()->Unit ,modifier:Modifier) {
    val winner=if(gameViewModel.player1.iswinner.value) gameViewModel.player1 else gameViewModel.player2
    Dialog(onDismissRequest = {})
    {
        Box(modifier=Modifier.background(color=Color.Cyan,shape= RoundedCornerShape(24.dp)).height(280.dp).width(280.dp).then(modifier)) {
            Column(modifier=Modifier.align(Alignment.TopCenter)) {
                Box(modifier = Modifier.background(color=Color.White,shape= RoundedCornerShape(24.dp)).fillMaxWidth().height(210.dp)) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally,modifier=Modifier.fillMaxSize().padding(top=8.dp,bottom=16.dp)) {
                        Box(
                            modifier = Modifier
                                .background(color = colorResource(R.color.light_gold))
                                .padding(8.dp)
                        ) {
                            Text(
                                text = winner.player.name.substring(0,winner.player.name.lastIndex) + " " + winner.player.name.last() + " Wins",
                                textAlign = TextAlign.Center,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = colorResource(R.color.dark_gold)
                            )
                            }
                        Row(horizontalArrangement = Arrangement.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "HIGHSCORE",
                                    fontSize = 16.sp,
                                    textAlign = TextAlign.Center,
                                    fontWeight = FontWeight.Bold,
                                    color = colorResource(R.color.light_gold)
                                )
                                Text(
                                    text = "${winner.high_score}",
                                    fontSize = 50.sp,
                                    textAlign = TextAlign.Center,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black,
                                    modifier = Modifier
                                        .padding(top = 8.dp, bottom = 4.dp)
                                )
                                Text(
                                    text = "POINTS",
                                    fontSize = 16.sp,
                                    color = Color.Black,
                                    fontWeight = FontWeight.Bold,

                                    textAlign = TextAlign.Center
                                )
                            }
                            Spacer(modifier=Modifier.width(40.dp))
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "SCORE",
                                    fontSize = 16.sp,
                                    textAlign = TextAlign.Center,
                                    fontWeight = FontWeight.Bold,

                                    color = colorResource(R.color.light_gold)
                                )
                                Text(
                                    text = "${winner.cur_score}",
                                    fontSize = 50.sp,
                                    textAlign = TextAlign.Center,
                                    color = Color.Black,
                                    fontWeight = FontWeight.Bold,

                                    modifier = Modifier
                                        .padding(top = 8.dp, bottom = 4.dp)
                                )
                                Text(
                                    text = "POINTS",
                                    fontSize = 16.sp,
                                    color = Color.Black,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }
            Row(horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically,modifier=Modifier.padding(8.dp).align(Alignment.BottomCenter).padding(top=4.dp)){
                Button(
                    onClick = {
                        ondismiss()
                        navController.navigateUp()
                        gameViewModel.ResetGame()
                        navController.navigate(screens.GAME.name)
                    },
                    shape = RoundedCornerShape(8.dp),
                    elevation = ButtonDefaults.buttonElevation(16.dp),
                    colors=ButtonDefaults.buttonColors(containerColor = colorResource(R.color.little_black)
                )) {
                    Text(
                        text="PLAY AGAIN",
                        //fontSize = 16.sp,
                        color=Color.White
                    )
            }
                Spacer(modifier=Modifier.width(32.dp))
            Button(
                onClick = {
                    ondismiss()
                    gameViewModel.ResetGame()
                    navController.navigateUp()
                },
                shape = RoundedCornerShape(8.dp),
                colors=ButtonDefaults.buttonColors(containerColor = Color.White),
                elevation = ButtonDefaults.buttonElevation(16.dp)
            ) {
                Text(
                    text="HOME",
                    //fontSize = 16.sp,
                    color=Color.Black
                )
            }
                }
        }
    }
}
