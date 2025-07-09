package com.example.battleshiparmanda

import android.R.attr.bottom
import android.R.attr.mode
import android.R.attr.orientation
import android.R.attr.right
import android.R.attr.text
import android.R.attr.translationX
import android.R.attr.translationY
import android.R.attr.visible
import android.content.Context
import android.graphics.Paint
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
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonElevation
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.times
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.consumePositionChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
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
    val localdensity =LocalDensity.current
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
    Canvas(modifier=Modifier.fillMaxSize()) {
        drawArc(
            color = Color.DarkGray,
            startAngle = -90f,
            sweepAngle = 180f,
            useCenter = false,
            topLeft = Offset(-150f, size.height/2f-150f),
            size = Size(300f, 300f),
            style = Stroke(width = 20f)
        )
        drawArc(
            color = Color.LightGray,
            startAngle = -90f,
            sweepAngle = 180f,
            useCenter = true,
            topLeft = Offset(-140f, size.height/2f-140f),
            size = Size(280f, 280f),
        )
        clipRect(
            left = 0f,
            top =  size.height/2f-140f+(1-gameViewModel.opp_player.health.value)*280,
            right = 140f,
            bottom =size.height/2f+140f
        ) {
            drawArc(
                color = if(gameViewModel.opp_player.health.value>0.5f) Color.Green else Color.Red,
                startAngle = -90f,
                sweepAngle = 180f,
                useCenter = true,
                topLeft = Offset(-140f, size.height/2f-140f),
                size = Size(280f,280f)
            )
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
    val yoffset by animateDpAsState(targetValue=if(gameViewModel.player1.iscurrentplayer) -145.dp else 145.dp, animationSpec = tween(durationMillis = 500,easing= LinearEasing))
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
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .offset(
                    x = 0.dp,
                    y = yoffset
                )
        ) {
            AnimatedVisibility(
                visible = (gameViewModel.cur_player.mode.value == Mode.FORTIFYING || gameViewModel.cur_player.mode.value == Mode.DEPLOYING),
                enter = slideInVertically(animationSpec = tween(durationMillis = 1000)){fullHeight -> fullHeight } + fadeIn(animationSpec = tween(durationMillis = 1000)) ,
                exit = slideOutVertically(animationSpec = tween(durationMillis = 1000)){fullHeight -> fullHeight } + fadeOut(animationSpec = tween(durationMillis = 1000)) ,
                modifier = Modifier
                    .align(Alignment.Center)
            ) {
                Fortify(
                    gameViewModel = gameViewModel,
                    heading = if (gameViewModel.cur_player.mode.value == Mode.DEPLOYING) "DEPLOY" else "FORTIFY",
                    description = if (gameViewModel.cur_player.mode.value == Mode.DEPLOYING)
                        "Click and Place Ships for \nthe best strategy"
                    else
                        "Drag and move \nundamaged ships to safety",
                    onFailure = { should_show_error_dialog = true }
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
            checked = (for_player.mode.value==Mode.DEPLOYING || for_player.mode.value==Mode.FORTIFYING),
            enabled = (for_player.mode.value==Mode.FORTIFYING || for_player.mode.value==Mode.ATTACKING),
            colors= SwitchDefaults.colors(
                checkedTrackColor = Color.DarkGray,
                uncheckedTrackColor = Color.LightGray,
            ),
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
            modifier = Modifier
                .align(Alignment.CenterEnd)
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
    val bg_color=Color(android.graphics.Color.parseColor("#00f0ff"))
    Dialog(
        onDismissRequest = {})
    {
        Box(
            modifier=Modifier
                .background(color=Color.Cyan,shape= RoundedCornerShape(24.dp))
                .height(310.dp)
                .width(330.dp)
                .drawBehind{
                    val size=this.size
                    drawContext.canvas.nativeCanvas.apply {
                        drawRoundRect(
                            0f,0f,size.width,size.height,24.dp.toPx(),24.dp.toPx(),
                            Paint().apply {
                                color=bg_color.toArgb()
                                setShadowLayer(
                                    30.dp.toPx(),
                                    0f,0f,
                                    Color.White.copy(alpha = 0.5f).toArgb()
                                )
                            }
                        )
                    }
                }
                .then(modifier)) {
            Column(modifier=Modifier.align(Alignment.TopCenter).fillMaxWidth()) {
                Box(modifier = Modifier
                    .background(color=Color.White,shape= RoundedCornerShape(24.dp))
                    .fillMaxWidth()
                    .height(230.dp)
                    ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier=Modifier
                            .fillMaxSize()
                            .padding(top= dimensionResource(R.dimen.large_padding)*2)) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                            Box(
                                modifier= Modifier
                                    .height(40.dp)
                                    .background(color=colorResource(R.color.med_gold))
                                    .weight(1f)
                            )
                            Box(
                                modifier = Modifier
                                    .background(color = colorResource(R.color.light_gold))
                            ) {
                                Text(
                                    text = winner.player.name.substring(
                                        0,
                                        winner.player.name.lastIndex
                                    ) + " " + winner.player.name.last() + " WINS",
                                    textAlign = TextAlign.Center,
                                    fontSize = 32.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = colorResource(R.color.dark_gold),
                                    modifier=Modifier
                                        .align(Alignment.Center)
                                        .padding(12.dp)
                                )
                            }
                            Box(
                                modifier= Modifier
                                    .height(40.dp)
                                    .background(color=colorResource(R.color.med_gold))
                                    .weight(1f)
                            )
                        }
                        Row(horizontalArrangement = Arrangement.Center,modifier=Modifier.fillMaxWidth()) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "HIGHSCORE",
                                    fontSize = 20.sp,
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
                                )
                                Text(
                                    text = "POINTS",
                                    fontSize = 20.sp,
                                    color = Color.Black,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center
                                )
                            }
                            Spacer(modifier=Modifier.width(60.dp))
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "SCORE",
                                    fontSize = 20.sp,
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
                                )
                                Text(
                                    text = "POINTS",
                                    fontSize = 20.sp,
                                    color = Color.Black,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier=Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(bottom=dimensionResource(R.dimen.large_padding)+4.dp)){
                Button(
                    onClick = {
                        ondismiss()
                        navController.navigateUp()
                        gameViewModel.ResetGame()
                        navController.navigate(screens.GAME.name)
                    },
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = dimensionResource(R.dimen.med_padding)),
                    elevation = ButtonDefaults.buttonElevation(4.dp),
                    colors=ButtonDefaults.buttonColors(containerColor = colorResource(R.color.little_black))) {
                    Text(
                        text="PLAY AGAIN",
                        fontWeight = FontWeight.Bold,
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
                elevation = ButtonDefaults.buttonElevation(4.dp)
            ) {
                Text(
                    text="HOME",
                    color=Color.Black,
                    fontWeight = FontWeight.Bold,
                )
            }
                }
        }
    }
}