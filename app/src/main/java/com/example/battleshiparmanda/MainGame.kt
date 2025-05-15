package com.example.battleshiparmanda

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
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
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.fontResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.squareup.kotlinpoet.INT
import kotlinx.coroutines.delay
import kotlin.math.abs
import kotlin.math.roundToInt

@Composable
fun maingame(navController:NavController){
    var player1 by remember { mutableStateOf(Player(player = Players.PLAYER1,
                                                    grid =  makegrid(5),
                                                    ships = mutableListOf(
                                                        Ship(ship=Ships.SHIP1,size= Ship_Sizes[Ships.SHIP1]!!),
                                                        Ship(ship=Ships.SHIP2,size= Ship_Sizes[Ships.SHIP2]!!),
                                                        Ship(ship=Ships.SHIP3,size= Ship_Sizes[Ships.SHIP3]!!),
                                                        Ship(ship=Ships.SHIP3_1,size= Ship_Sizes[Ships.SHIP3_1]!!)),
                                                    iscurrent = true,
                                                    remaining = 3,
                                                    cur_score = 0,
                                                    isdeploy = true)) }
    var player2 by remember { mutableStateOf(Player(player = Players.PLAYER2,
                                                    grid =  makegrid(5),
                                                    ships = mutableListOf(
                                                        Ship(ship=Ships.SHIP1,size= Ship_Sizes[Ships.SHIP1]!!),
                                                        Ship(ship=Ships.SHIP2,size= Ship_Sizes[Ships.SHIP2]!!),
                                                        Ship(ship=Ships.SHIP3,size= Ship_Sizes[Ships.SHIP3]!!),
                                                        Ship(ship=Ships.SHIP3_1,size= Ship_Sizes[Ships.SHIP3_1]!!)),
                                                    iscurrent = false,
                                                    remaining = 3,
                                                    cur_score = 0,
                                                    isdeploy = true))}
    val context= LocalContext.current
    val sharedPref = context.getSharedPreferences(stringResource(R.string.shared_pref_filename), Context.MODE_PRIVATE)
    val json = sharedPref.getString(stringResource(R.string.player_history_key), "")
    Log.d("game_history_list","json: $json")
    val type = object : TypeToken<MutableList<game_history_data>>() {}.type
    var history_list: MutableList<game_history_data> = mutableListOf()
    if(json!=""){
        history_list= Gson().fromJson(json, type)
    }
    player1.high_score= get_player_high_score(player1.player,history_list)
    player2.high_score= get_player_high_score(player2.player,history_list)
    all_game_history=history_list.toMutableList()
    makeUI(player1=player1,player2=player2, changeplayer = {
        if(player1.iscurrentplayer) {
            player1.iscurrentplayer=false
            player2.iscurrentplayer=true
            player2.isattacking=true
            player2.remaining_attacks=3
    } else {
        player1.iscurrentplayer=true
        player2.iscurrentplayer=false
            player1.isattacking=true
            player1.remaining_attacks=3
    } },
        navController=navController)
}
fun makegrid(size:Int):MutableList<MutableList<Cell>>{
    val tmp = mutableListOf<MutableList<Cell>>()
    for (i in 0 until size) {
        val row = mutableListOf<Cell>()
        for (j in 0 until size) {
            row.add(Cell(state = CellState.EMPTY, offset = Offset.Zero))//dont forget to update the offset to required for each player
        }
        tmp.add(row)
    }
    return tmp
}
@Composable
fun makeUI(player1:Player,player2:Player,changeplayer:()->Unit={},navController: NavController){
    var player1_turn by remember{ mutableStateOf(true)}
    player1_turn=player1.iscurrentplayer
    val localdensity= LocalDensity.current
    val p2_background by animateColorAsState(
        targetValue = if (player1.iscurrentplayer) colorResource(R.color.turqoise) else colorResource(R.color.little_black),
        animationSpec = tween(durationMillis = 1000)
    )
    val p1_background by animateColorAsState(
        targetValue = if (player2.iscurrentplayer) colorResource(R.color.turqoise) else colorResource(R.color.little_black),
        animationSpec = tween(durationMillis = 1000)
    )
    val should_show_fortify=((!player1.isattacking && player1_turn) || (!player2.isattacking && !player1_turn) || player1.isdeploying || player2.isdeploying)
    var is_game_finished by remember { mutableStateOf(false) }
    val yoffset by animateDpAsState(targetValue=if((!player1.isattacking && player1_turn) || player1.isdeploying) -140.dp else 140.dp, animationSpec = tween(durationMillis = 500,easing= LinearEasing))
    //var is_reset by remember{mutableStateOf(false)}
    var show_error_dialog by remember { mutableStateOf(false) }
    Box(
        modifier=Modifier.fillMaxSize()
    ){
        //fix the fortify animation for player1
        Box(modifier=Modifier
            .align(Alignment.Center)
            .offset(
                x = 0.dp,
                y = yoffset
            )
            .zIndex(2f)
                ) {
                AnimatedVisibility(
                    visible=should_show_fortify,
                    enter = slideInVertically(animationSpec = tween(durationMillis = 1000)){fullHeight -> fullHeight } + fadeIn(animationSpec = tween(durationMillis = 1000)) ,
                    exit = slideOutVertically(animationSpec = tween(durationMillis = 1000)){fullHeight -> fullHeight } + fadeOut(animationSpec = tween(durationMillis = 1000))
                ) {
                    fortify(
                        heading = if ((player1.iscurrentplayer && player1.isdeploying) || (player2.iscurrentplayer && player2.isdeploying)) "DEPLOY" else "FORTIFY",
                        description = if ((player1.iscurrentplayer && player1.isdeploying) || (player2.iscurrentplayer && player2.isdeploying)) "Click and Place Ships for \nthe best strategy" else "Drag and move \nundamaged ships to safety",
                        ondelete = {
                            if (player1_turn) {
                                for (i in player1.ships) {
                                    if (!i.isattacked) {
                                        i.img_offset = Offset.Zero
                                        i.tmp_start_position_in_screen = i.start_position_in_screen
                                        tmp_all_ships_p1_offset[i.ship] = Offset.Zero
                                    }
                                }
                                clearplayergrid(player1)
                            }
                            else {
                                for(i in player2.ships) {
                                    if (!i.isattacked) {
                                        i.img_offset = Offset(0f,0f)
                                        i.tmp_start_position_in_screen = i.start_position_in_screen
                                        tmp_all_ships_p1_offset[i.ship] = Offset.Zero
                                    }
                                }
                                clearplayergrid(player2)
                            }
                        },
                        onsave = {
                            if (possible_place_ships(
                                    cell_size = 52.dp,
                                    padding = 8.dp,
                                    player1 = player1,
                                    player2 = player2,
                                    localdensity = localdensity
                                )
                            ) {
                                for (key1 in tmp_all_ships_p1_offset.keys) {
                                    player1.ships.find {it.ship == key1 }!!.let {obj ->
                                        obj.img_offset=tmp_all_ships_p1_offset[key1]!!.copy()
                                    }
                                }
                                for (key2 in tmp_all_ships_p2_offset.keys) {
                                    player2.ships.find {it.ship == key2 }!!.let {obj ->
                                        obj.img_offset=tmp_all_ships_p2_offset[key2]!!.copy()
                                    }
                                }
                                if (player1.iscurrentplayer && player1.isdeploying) {
                                    player1.isdeploying = false
                                }
                                if (player2.iscurrentplayer && player2.isdeploying) {
                                    player2.isdeploying = false
                                }
                                if (player1.iscurrentplayer && player1.isattacking && !player1.isdeploying) {
                                    player1.isattacking = false
                                    player2.isattacking = true
                                }
                                if (player2.iscurrentplayer && player2.isattacking && !player2.isdeploying) {
                                    player2.isattacking = false
                                    player1.isattacking = true
                                }
                                copy_tmp_to_org_coordinates(if(player1_turn) player1 else player2)
                                if(player1_turn){
                                    player1.ships.forEach{obj ->
                                        obj.start_position_in_screen=obj.tmp_start_position_in_screen
                                    }
                                }
                                else{
                                    player2.ships.forEach{obj ->
                                        obj.start_position_in_screen=obj.tmp_start_position_in_screen
                                    }
                                }
                                changeplayer()
                                player1_turn = !player1_turn
                            } else {
                                //show dialog not possible
                                reset_all_grid_positions_of_ships(player = if(player1_turn) player1 else player2)
                                show_error_dialog=true
                            }
                        },
                        isdeploy = player1.isdeploying || player2.isdeploying,
                        player1_turn = player1_turn,
                        modifier = Modifier
                    )
                }
        }
        Column(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier.weight(1f).background(
                    if (player1.iscurrentplayer) colorResource(R.color.turqoise) else colorResource(R.color.little_black)
                )
            ) {
                Box(
                    modifier=Modifier.fillMaxSize().background(p2_background)
                ) {
                    playerarea(
                        player = player2,
                        player1=player1,
                        player2 = player2,
                        rotate = true,
                        attack = player2.isattacking,
                        text = if ((player1.iscurrentplayer && player1.isdeploying) || (player2.iscurrentplayer && player2.isdeploying)) "DEPLOY" else if (player2.isattacking) "ATTACK" else "FORTIFY",
                        cannon_visible = player2.isattacking && player2.iscurrentplayer && !player2.isdeploying,
                        modifier = Modifier.align(Alignment.Center),
                        onswitchclick = {
                            if(!player2.isdeploying && player2.remaining_attacks==3) {
                                player2.isattacking = !player2.isattacking
                            }
                            if(!player2.isattacking){
                                player2.ships.forEach { obj->
                                    obj.tmp_start_position_in_screen=obj.start_position_in_screen
                                    obj.img_offset= convert_to_offset(obj.start_position_in_screen.first)
                                }
                            }
                        },
                        onwin={
                            player1.iswinner=true
                            is_game_finished=true})
                }
            }
            Box(
                modifier = Modifier.weight(1f).background(p1_background)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize()
                        .background(Color.Black.copy(alpha = if ((!player2.isattacking && !player1_turn) || player2.isdeploying) 0.5f else 0f))
                ) {
                    playerarea(
                        player = player1,
                        player1=player1,
                        player2 = player2,
                        rotate = false,
                        attack = player1.isattacking,
                        text = if ((player1.iscurrentplayer && player1.isdeploying) || (player2.iscurrentplayer && player2.isdeploying)) "DEPLOY" else if (player1.isattacking) "ATTACK" else "FORTIFY",
                        cannon_visible = player1.isattacking && player1.iscurrentplayer && !player1.isdeploying,
                        modifier = Modifier.align(Alignment.Center),
                        onswitchclick = {
                            if(!player1.isdeploying && player1.remaining_attacks==3) {
                                player1.isattacking = !player1.isattacking
                            }
                            if(!player1.isattacking){
                                player1.ships.forEach { obj->
                                    obj.tmp_start_position_in_screen=obj.start_position_in_screen
                                    obj.img_offset= convert_to_offset(obj.start_position_in_screen.first)
                                }
                            }
                        },
                        onwin = {
                            player2.iswinner=true
                            is_game_finished=true})
                }
            }
        }
        if(is_game_finished){
            Log.d("winner","is player1 winner:${player1.iswinner}")
            show_win_dialog(player1=player1,player2=player2, navController = navController,modifier=Modifier.align(Alignment.Center))
        }
        if(show_error_dialog){
            val curplayer=if(player1.iscurrentplayer) player1 else player2
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
                    show_error_dialog=false
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            show_error_dialog=false
                        }
                    ) {
                        Text("OK")
                    }
                },
                dismissButton = {},
                modifier = Modifier.graphicsLayer { rotationZ=if(player1_turn) 0f else 180f}
            )
        }
    }
}
fun possible_snap_ship(positions:MutableList<Pair<Int,Int>>,grid:List<List<Cell>>):Boolean {
    //given that all ships lie in the grid,check if correctly placed requires ship position coordinates in grid
    for(i in positions){
        if(grid[i.first][i.second].cellState!=CellState.EMPTY){
                return false
            }
        }
    return true
}
//if below function return false, dont forget to reset the position_in_grid of each ship for playerx
fun possible_place_ships(cell_size: Dp,padding: Dp,player1: Player,player2: Player,localdensity: Density):Boolean{
    var temp_grid= List(player1.grid_size){List(player1.grid_size){Cell(CellState.EMPTY, Offset.Zero)} } //this grid is for which we put the new values
    var curcol=0
    var currow=0
    var cur_idxs= mutableListOf<Pair<Int,Int>>()//store positions of each ship in grid

    if(player1.iscurrentplayer){
        //copy the hit/miss in temp_grid
        for(row in 0 until temp_grid.size){
            for(col in 0 until temp_grid.size){
                if(ishitormiss(player1.grid[row][col].cellState)){
                    temp_grid[row][col].cellState=player1.grid[row][col].cellState
                }
            }
        }
        for(ship in player1.ships){
            //if already attacked, copy remaining portion of ship
            cur_idxs= mutableListOf()
            if(ship.isattacked){
                ship.positions_in_grid.forEach {
                    temp_grid[it.first][it.second].cellState=CellState.SHIP
                }
                continue
            }
            curcol= ((convert_to_offset(ship.tmp_start_position_in_screen.first) - player_1_grid_start_offset)/(with(localdensity){(cell_size+padding).toPx()})).x.roundToInt()
            currow= ((convert_to_offset(ship.tmp_start_position_in_screen.first) - player_1_grid_start_offset)/(with(localdensity){(cell_size+padding).toPx()})).y.roundToInt()

            if(ship.tmp_start_position_in_screen.second==Orientation.HORIZONTAL){
                if(curcol<0 || curcol>4 || currow<0 || currow>4){
                    return false
                }
                if(curcol>(5- Ship_Sizes[ship.ship]!!)){
                    return false
                }
            }
            else{
                curcol -=1
                if(curcol<0 || curcol>4 || currow<0 || currow>4){
                    return false
                }
                if(currow>5- Ship_Sizes[ship.ship]!!){
                    return false
                }
            }
            val start_pos=Pair(currow,curcol)
            cur_idxs.addAll(get_grid_positions(start_position = start_pos,ship=ship))
            Log.d("allships","${ship.ship},$start_pos,$cur_idxs")

            //check for collision of ships
            if(possible_snap_ship(positions=cur_idxs, grid = temp_grid)){
                cur_idxs.forEach { pair->
                    temp_grid[pair.first][pair.second].cellState=CellState.SHIP
                }
            }
            else{
//                Log.d("allships","yes here,$cur_idxs")
//                for (row in temp_grid) {
//                    val rowString = row.joinToString(" ") { it.cellState.toString() }
//                    Log.d("allships", rowString)
//                }
//                Log.d("allships","\n")
                return false
            }
            ship.positions_in_grid=cur_idxs
        }
        //copy from temp_grid to playerx
        for(row in 0 until player1.grid_size){
            for(col in 0 until player1.grid_size){
                player1.grid[row][col].cellState=temp_grid[row][col].cellState
            }
        }
    }
    else{
        //copy the hit/miss in temp_grid
        for(row in 0 until temp_grid.size){
            for(col in 0 until temp_grid.size){
                if(ishitormiss(player2.grid[row][col].cellState)){
                    temp_grid[row][col].cellState=player2.grid[row][col].cellState
                }
            }
        }
        Log.d("playersample","${player2.ships}")
        for(ship in player2.ships){
            cur_idxs= mutableListOf()
            if(ship.isattacked){
                ship.positions_in_grid.forEach {
                    temp_grid[it.first][it.second].cellState=CellState.SHIP
                }
                continue
            }
            curcol= abs(((convert_to_offset(ship.tmp_start_position_in_screen.first) - player_2_grid_start_offset)/(with(localdensity){(cell_size+padding).toPx()})).x.roundToInt())
            currow= abs(((convert_to_offset(ship.tmp_start_position_in_screen.first) - player_2_grid_start_offset)/(with(localdensity){(cell_size+padding).toPx()})).y.roundToInt())

            if(ship.tmp_start_position_in_screen.second==Orientation.HORIZONTAL){
                if(curcol<0 || curcol>4 || currow<0 || currow>4){
                    return false
                }
                if(curcol>(5- Ship_Sizes[ship.ship]!!)){
                    return false
                }
            }
            else{
                curcol-=1
                if(curcol<0 || curcol>4 || currow<0 || currow>4){
                    return false
                }
                if(currow>5- Ship_Sizes[ship.ship]!!){
                    return false
                }
            }
            //valid idx's
            //check for collision of ships
            val start_pos=Pair(currow,curcol)
            cur_idxs.addAll(get_grid_positions(start_position = start_pos,ship=ship))

            if(possible_snap_ship(positions=cur_idxs, grid = temp_grid)){
                cur_idxs.forEach { pair->
                    temp_grid[pair.first][pair.second].cellState=CellState.SHIP
                }
            }
            else{
                return false
            }
            ship.positions_in_grid=cur_idxs
        }
        for(row in 0 until player2.grid_size){
            for(col in 0 until player2.grid_size){
                player2.grid[row][col].cellState=temp_grid[row][col].cellState
            }
        }
    }
    Log.d("allships","possible to place ships!")
    return true
}
fun get_ship_position_map(ships: MutableList<Ship>):MutableMap<Ships,MutableList<Pair<Int,Int>>>{
    var final_map= mutableMapOf<Ships,MutableList<Pair<Int,Int>>>()
    for(i in ships){
        //final_map[i.ship]= get_grid_positions(i.tmp_start_position_in_screen,i.ship)
    }
    return final_map
}
fun get_grid_positions(start_position:Pair<Int,Int>,ship: Ship):MutableList<Pair<Int,Int>>{
    var lst = mutableListOf<Pair<Int,Int>>()
    for(i in 0 until ship.size){
        if(ship.tmp_start_position_in_screen.second==Orientation.HORIZONTAL){
            lst.add(Pair(start_position.first,start_position.second+i))
        }
        else{
            lst.add(Pair(start_position.first + i,start_position.second))
        }
    }
    return lst
}
fun printGrid(player: Player) {
    for (row in player.grid) {
        val rowString = row.joinToString(" ") { it.cellState.toString() }
        Log.d("allships", rowString)
    }
    Log.d("allships","\n")
}
fun reset_all_grid_positions_of_ships(player: Player){
    for(i in player.ships){
        i.positions_in_grid.clear()
    }
}
fun reset_ships_to_original_position(player: Player){
    player.ships.forEach {
        it.img_offset=Offset(0f,0f)
    }
}
fun reset_all_global(){
    for(i in tmp_all_ships_p1_offset.keys){
        tmp_all_ships_p1_offset[i]= Offset.Zero
        tmp_all_ships_p2_offset[i]= Offset.Zero
    }

}
fun ishitormiss(cellState: CellState):Boolean{
    return (cellState==CellState.HIT || cellState==CellState.MISS)
}
@Composable
fun shipdock(ships:MutableList<Ship>,parent_size:IntSize,player: Player,modifier:Modifier=Modifier){
    Box(modifier=Modifier.fillMaxSize().padding(top=10.dp)){
        for (i in ships) {
            movableship(
                ship = i,
                player = player,
                cell_size = 52.dp,
                padding = 8.dp,
                shipmodifier = Modifier.align(Alignment.TopCenter)
                )
        }
    }
}//check if orientatoin is working properly
@Composable
fun dummydock(ships:MutableList<Ship>,player: Player,modifier:Modifier){
    Row(horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically,modifier=Modifier.padding(top=4.dp).then(modifier)){
        for(ship in ships){
            dummyship(ship=ship,cell_size=52.dp,padding=8.dp, player =player )
            Spacer(modifier=Modifier.width(8.dp))
        }
    }
}
@Composable
fun dummyship(ship:Ship,player: Player,cell_size:Dp,padding: Dp){
    val shipLength = Ship_Sizes[ship.ship]!!
    val orgsizex = cell_size * shipLength + padding * (shipLength - 1)
    val orgsizey= cell_size
    var aspect_ratio =0.6f
    var final_sizex = orgsizex*aspect_ratio
    var final_sizey =orgsizey*aspect_ratio
    Box(modifier=Modifier
        .size(final_sizex,final_sizey)
        .onGloballyPositioned { coord->
            if(player.player==Players.PLAYER1) {
                dummy_ships_p1_coordinates[ship.ship] = coord.localToWindow(Offset.Zero)
            }
            else{
                dummy_ships_p2_coordinates[ship.ship] = coord.localToWindow(Offset.Zero)
            }
        }
        .alpha(if(player.isdeploying) 0f else 1f)
    )
    {
        Image(
            painter = painterResource(ship_images[ship.ship]!!),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
        )
    }

}
fun to_offset(pair: Pair<Float,Float>):Offset{
    return Offset(pair.first,pair.second)
}
@Composable
fun movableship(ship:Ship,player: Player,cell_size:Dp,padding: Dp,shipmodifier:Modifier=Modifier){
    var orientation by remember { mutableStateOf(Orientation.HORIZONTAL) }
    val shipLength = Ship_Sizes[ship.ship]!!
    var rotate by remember { mutableStateOf(orientation==Orientation.VERTICAL) }
    val orgsizex = cell_size * shipLength + padding * (shipLength - 1)
    val orgsizey= cell_size
    var aspect_ratio by remember { mutableStateOf(0.6f) }
    //var final_sizex by remember{ mutableStateOf(orgsizex*aspect_ratio)}
    //var final_sizey by remember{ mutableStateOf(orgsizey*aspect_ratio)}
    val final_sizex by animateDpAsState(targetValue=orgsizex*aspect_ratio,animationSpec=tween(durationMillis =  250))
    val final_sizey by animateDpAsState(targetValue=orgsizey*aspect_ratio,animationSpec=tween(durationMillis = 250))
    var img_offset by remember { mutableStateOf(ship.img_offset) }
    var isDragging by remember { mutableStateOf(false) }
    LaunchedEffect(ship.img_offset) {
        img_offset = ship.img_offset
    }
    val condition_for_gray_out =(ship.positions_in_grid.size==0 && !player.isdeploying && ship.isattacked)
    val matrix = ColorMatrix().apply { setToSaturation(if(condition_for_gray_out) 0f else 1f) } // 0f = greyscale
    val paint = ColorFilter.colorMatrix(matrix)
    val is_possible_to_drag=(ship.positions_in_grid.size == Ship_Sizes[ship.ship] || player.isdeploying)
    var img_coordinates by remember { mutableStateOf(Offset.Zero) }//used in ondragend
    //val cur_rotation_degree by animateFloatAsState(targetValue = if(rotate) 90f else 0f)

    Log.d("shippieesiadn","$ship,$orgsizex")
//    for(i in ships){
//        Log.d("samplething","${i.name}")
//    }
    Log.d("samplething","----------------------")
    //run app and check if the playerx.isdeploying is working correctly instead of deploy px
    if(ship.isvisible || condition_for_gray_out) {
        Box(
            modifier = Modifier
                .size(final_sizex, final_sizey)
                .graphicsLayer {
                    translationX = img_offset.x
                    translationY = img_offset.y
                    rotationZ = if(rotate) 90f else 0f
                    transformOrigin = TransformOrigin.Center
                }
                .onGloballyPositioned { coord ->
                    img_coordinates=coord.localToWindow(Offset.Zero)
                }
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = {
                            if (ship.isvisible && is_possible_to_drag) {
                                isDragging = true
                                aspect_ratio=1f
                            }
                        },
                        onDrag = { change, dragAmount ->
                            if (ship.isvisible && is_possible_to_drag) {
                                val corrected = if (rotate) {
                                    // when rotated 90°, x→y and y→–x
                                    Offset(-dragAmount.y, dragAmount.x)
                                } else {
                                    dragAmount
                                }
                                    img_offset += corrected
                                change.consumePositionChange()
                            }
                        },
                        onDragEnd = {
                            player.ships.find { it.ship==ship.ship }!!.let{ obj ->
                                obj.tmp_start_position_in_screen=Pair(convert_to_pair(img_coordinates),orientation)//here is the problem
                            }
                            Log.d("allships","orientation:$orientation")
                            if (ship.isvisible && is_possible_to_drag) {
                                if (player.player == Players.PLAYER1 && player.iscurrentplayer) {
                                    tmp_all_ships_p1_offset[ship.ship] = img_offset
                                    //Log.d("playersample","${all_ships_p1_offset[ship]},$player_1_area_start_offset,${all_ships_p1_coordinates[ship]},${(all_ships_p1_coordinates[ship]!!.first -player_1_grid_start_offset)/(cell_size+padding).toPx()}")
                                }
                                if (player.player == Players.PLAYER2 && player.iscurrentplayer) {
                                    tmp_all_ships_p2_offset[ship.ship] = img_offset
                                    //Log.d("playersample","${tmp_all_ships_p2_offset[ship]},$player_2_area_start_offset,${all_ships_p2_coordinates[ship]},${(all_ships_p2_coordinates[ship]!!.first -player_2_grid_start_offset)/(cell_size+padding).toPx()}")
                                }
                            }
                        },
                    )
                }
                .clickable(
                    enabled = ship.isvisible && !condition_for_gray_out && !ship.isattacked,
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }) {
                    orientation = if (orientation == Orientation.HORIZONTAL) Orientation.VERTICAL else Orientation.HORIZONTAL
                    rotate = !rotate
                }
                .then(shipmodifier)
        ) {
            Image(
                painter = painterResource(ship_images[ship.ship]!!),
                contentDescription = null,
                contentScale = ContentScale.FillBounds,
                colorFilter = paint
            )
        }
    }
}
@Composable
fun playerarea(player: Player,player1: Player,player2: Player,rotate:Boolean,attack:Boolean,text: String,cannon_visible:Boolean,modifier: Modifier=Modifier,onswitchclick: () -> Unit,onwin: () -> Unit){
    var parent_size by remember { mutableStateOf(IntSize.Zero) }
    Box(modifier=Modifier
        .fillMaxSize()
        .graphicsLayer { rotationZ= if(rotate) 180f else 0f }
        .onGloballyPositioned {
            if(player.player==Players.PLAYER1){
                player_1_area_start_offset=it.localToWindow(Offset.Zero)
            }
            else{
                player_2_area_start_offset=it.localToWindow(Offset.Zero)
            }
            parent_size=it.size
        }){
        Box(modifier=Modifier.wrapContentSize().zIndex(3f)){
            if(player1.isdeploying || player1.isattacking || player2.isdeploying || player2.isattacking){
                dummydock(ships=if(player.player==Players.PLAYER1) player1.ships else player2.ships, player = player,modifier=Modifier.align(Alignment.TopCenter))
            }
            //copy dummy ships coordinates to player.ship.coordinates
            //Log.d("allships","dummy_ships_p1_coordinates:$dummy_ships_p1_coordinates")
            //Log.d("allships","dummy_ships_p2_coordinates:$dummy_ships_p2_coordinates")

            if(player1.isdeploying){
                player1.ships.forEach {
                    //it.start_position_in_screen=Pair(convert_to_pair(dummy_ships_p1_coordinates[it.ship]!!),Orientation.HORIZONTAL)
                    //it.img_offset=it.img_offset
                }
            }
            if(player2.isdeploying){
                player2.ships.forEach {
                    //it.start_position_in_screen=Pair(convert_to_pair(dummy_ships_p2_coordinates[it.ship]!!),Orientation.HORIZONTAL)
                }
            }
            if(player.player==Players.PLAYER1){
                player1.ships.forEach {
                    it.isvisible=player.iscurrentplayer && !cannon_visible
                }
            }
            else{
                player2.ships.forEach {
                    it.isvisible=player.iscurrentplayer && !cannon_visible
                }

            }
            shipdock(ships=if(player.player==Players.PLAYER1) player1.ships else player2.ships,
                parent_size=parent_size,
                player=player)
        }
        Box(
            modifier=Modifier.align(Alignment.Center).zIndex(3f),
            contentAlignment = Alignment.Center
        )
        {
            AnimatedVisibility(
                visible = cannon_visible,
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
            drawgrid(player=player, player1 = player1,player2=player2, to_be_black = player.iscurrentplayer && attack ,padding=8.dp,modifier=Modifier.align(Alignment.CenterHorizontally), onwin = onwin)
            AnimatedVisibility(
                visible=player.iscurrentplayer,
                //enter = fadeIn(animationSpec = tween(durationMillis = 500)),
                //exit = fadeOut(animationSpec = tween(durationMillis = 500))
            ) {
                bottombar(remaining_attacks = if(attack) player.remaining_attacks else 0,text=text, is_on = attack, onswitchclick = onswitchclick)
            }
        }
    }
}
@Composable
fun drawgrid(player:Player,player1: Player,player2: Player,to_be_black:Boolean,padding:Dp,modifier:Modifier=Modifier,onwin:() -> Unit){
    //for a 5*5 grid only
    val context = LocalContext.current
    Log.d("officesetts","$player_1_grid_start_offset,$player_2_grid_start_offset")
    Box(modifier=Modifier
        .padding(top=45.dp)
        .onGloballyPositioned {coords->
            if(player.player==Players.PLAYER1){
                player_1_grid_start_offset=coords.localToWindow(Offset.Zero)
            }
            else{
                player_2_grid_start_offset=coords.localToWindow(Offset.Zero)
            }
        }
        .then(modifier)) {
        Column(
            verticalArrangement = Arrangement.spacedBy(padding),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(16.dp).then(modifier)
        ) {
            for (row in 0 until player.grid_size) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(padding),
                    modifier=Modifier.align(Alignment.CenterHorizontally)
                ) {
                    for (col in 0 until player.grid_size) {
//                        val cell_background by animateColorAsState(
//                            targetValue = (colorResource(if(to_be_black) R.color.gray else R.color.very_light_gray).copy(alpha = if(player.grid[row][col].cellState==CellState.EMPTY || player.grid[row][col].cellState==CellState.SHIP) 1f else 0f)),
//                            animationSpec = tween(durationMillis = 1000)
//                        )
                        val cell_background=colorResource(if(to_be_black) R.color.gray else R.color.very_light_gray).copy(alpha = if(player.grid[row][col].cellState==CellState.EMPTY || player.grid[row][col].cellState==CellState.SHIP) 1f else 0f)
                        Surface(
                            onClick = {
                                if(player.player==Players.PLAYER2 && !player.iscurrentplayer){
                                    if(player1.isattacking && !player1.isdeploying) {//only allow to change state if other player is attacking
                                        if (player.grid[row][col].cellState == CellState.SHIP) {
                                            player.grid[row][col].cellState = CellState.HIT
                                            player.ships.find { Pair(row,col) in it.positions_in_grid}!!.let { obj->
                                                obj.isattacked=true
                                            }
                                            player1.cur_score += HIT_SCORE
//                                      //remove the ship value from the dict and decrement attack
                                            for (i in player.ships) {
                                                if (Pair(row, col) in i.positions_in_grid) {
                                                    i.positions_in_grid.remove(Pair(row, col))
                                                    break
                                                }
                                            }
                                            Log.d("alphasplayersample", "${player.ships}")
                                            if (player1.remaining_attacks == 1) {
                                                player1.iscurrentplayer = false
                                                player2.iscurrentplayer = true
                                                player2.isattacking = true
                                                player2.remaining_attacks = 3
                                            } else {
                                                player1.remaining_attacks -= 1
                                            }
                                        } else if (player.grid[row][col].cellState == CellState.EMPTY) {
                                            player.grid[row][col].cellState = CellState.MISS
                                            player1.cur_score += if(player1.cur_score>0) MISS_SCORE else 0
                                            if (player1.remaining_attacks == 1) {
                                                player1.iscurrentplayer = false
                                                player2.iscurrentplayer = true
                                                player2.isattacking = true
                                                player2.remaining_attacks = 3
                                            } else {
                                                player1.remaining_attacks -= 1
                                            }
                                        } else {
                                            //handle if the missed or hit is touched
                                            do_vibrate(context=context)
                                        }
                                    }
                                }
                                else if(player.player==Players.PLAYER1 && !player.iscurrentplayer){
                                    if(player2.isattacking && !player2.isdeploying) {
                                        if (player.grid[row][col].cellState == CellState.SHIP) {
                                            player.grid[row][col].cellState = CellState.HIT
                                            player2.cur_score += HIT_SCORE
                                            player.ships.find { Pair(row,col) in it.positions_in_grid}!!.let { obj->
                                                obj.isattacked=true
                                            }
//                                  //remove the ship value from the dict and decrement attack
                                            for (i in player.ships) {
                                                if (Pair(row, col) in i.positions_in_grid) {
                                                    i.positions_in_grid.remove(Pair(row, col))
                                                    break
                                                }
                                            }
                                            if (player2.remaining_attacks == 1) {
                                                player2.iscurrentplayer = false
                                                player1.iscurrentplayer = true
                                                player1.isattacking = true
                                                player1.remaining_attacks = 3
                                            } else {
                                                player2.remaining_attacks -= 1
                                            }
                                        } else if (player.grid[row][col].cellState == CellState.EMPTY) {
                                            player.grid[row][col].cellState = CellState.MISS
                                            player2.cur_score += if(player2.cur_score>0) MISS_SCORE else 0
                                            if (player2.remaining_attacks == 1) {
                                                player2.iscurrentplayer = false
                                                player1.iscurrentplayer = true
                                                player1.isattacking = true
                                                player1.remaining_attacks = 3
                                            } else {
                                                player2.remaining_attacks -= 1
                                            }
                                        } else {
                                            //handle if the missed or hit is touched
                                            do_vibrate(context = context)
                                        }
                                    }
                                }
                                if(is_defeated(player) && !player1.isdeploying && !player2.isdeploying){
                                    Log.d("player_won","${player.player.name}")
                                    //store new game into history
                                    val winner_player=if(player.player==Players.PLAYER2) player1 else player2
                                    val sharedPref = context.getSharedPreferences(context.getString(R.string.shared_pref_filename), Context.MODE_PRIVATE)
                                    val editor = sharedPref.edit()
                                    if(winner_player.high_score<winner_player.cur_score){
                                        winner_player.high_score=winner_player.cur_score
                                    }
                                    if(player.high_score<player.cur_score){
                                        player.high_score=player.cur_score
                                    }
                                    all_game_history.add(game_history_data(winner = winner_player.player, winner_player_score = winner_player.cur_score, winner_player_high_score = winner_player.high_score, loser_player_score = player.cur_score, loser_player_high_score = player.high_score))
                                    val gson= Gson()
                                    val json=gson.toJson(all_game_history)
                                    editor.putString(context.getString(R.string.player_history_key),json)
                                    editor.apply()
                                    Log.d("game_history_list","$json")
                                    onwin()
                                }
                                //Log.d("playersample","iscross=$iscross,iscircle=$iscircle")
                                Log.d("alphaplayersample","${player.grid[row][col].cellState==CellState.EMPTY || player.grid[row][col].cellState==CellState.SHIP}")
                            },
                            //change backgournd color of each cell to transperent if hit or miss
                            color = cell_background,
                            shape = RoundedCornerShape(3.dp),
                            modifier = Modifier
                                .size(52.dp)
                                .shadow(32.dp, RoundedCornerShape(3.dp))
                                .onGloballyPositioned { coord ->
                                    if(player.player==Players.PLAYER1){
                                        player1.grid[row][col].offset=coord.localToWindow(Offset.Zero)
                                    }
                                    else{
                                        player2.grid[row][col].offset=coord.localToWindow(Offset.Zero)
                                    }
                                }
                        ) {
                            //Log.d("playersample","iscross=$iscross,iscircle=$iscircle")
                            if(player.grid[row][col].cellState==CellState.HIT || player.grid[row][col].cellState==CellState.MISS){
                                Log.d("playersample","${player.ships},$row,$col,yess you got it boiz")
                                Image(
                                    painter= painterResource(if(player.grid[row][col].cellState==CellState.HIT) R.drawable.redcircle else R.drawable.whitecross),
                                    contentDescription = null,
                                    contentScale = ContentScale.FillBounds,
                                    modifier=Modifier
                                        .fillMaxSize()
                                        .padding(4.dp)
                                        .zIndex(4f)
                                        .background(Color.Transparent),
                                    colorFilter = ColorFilter.tint(Color.Black.copy(alpha=if(player.iscurrentplayer && player.isattacking) 0.5f else 0f), blendMode = BlendMode.Darken)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
@Composable
fun bottombar(remaining_attacks:Int=3,text:String="ATTACK",is_on:Boolean=true,onswitchclick:() ->Unit={},modifier: Modifier=Modifier){
    //change only remainging_attack,ison to change the ui
        Box(
            modifier = Modifier
                .background(
                    color = if (is_on) colorResource(R.color.bottomattack) else Color.White,
                    shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                )
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(start = 8.dp,end=16.dp)//,top=4.dp,bottom=4.dp)
                .then(modifier)
        ) {
            draw_circles(
                totalcount = 3,
                remaining_attacks = remaining_attacks,
                circlesize = 26.dp,
                modifier = Modifier.align(Alignment.CenterStart).wrapContentSize()
            )
            Text(
                text = text,
                fontSize = 28.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.Center),
                color = if (is_on) Color.White else Color.Black,
                fontFamily = FontFamily(Font(R.font.bottombar)),
            )
            Switch(
                checked = is_on,
                onCheckedChange = {
                    onswitchclick()
                },
                modifier = Modifier.align(Alignment.CenterEnd)
            )
        }
}
fun is_defeated(currently_being_attacked_player:Player):Boolean{
    for(i in currently_being_attacked_player.ships){
        if(i.positions_in_grid!!.size!=0){
            return false
        }
    }
    return true
}
@Composable
fun draw_circles(totalcount:Int=3,remaining_attacks: Int,circlesize:Dp,modifier: Modifier=Modifier){
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
fun do_vibrate(context: Context){
    val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        vibrator.vibrate(
            VibrationEffect.createOneShot(
                350, // duration in milliseconds
                VibrationEffect.DEFAULT_AMPLITUDE
            )
        )
    } else {
        // Deprecated in Android O+
        vibrator.vibrate(350)
    }
}
@Composable
fun show_win_dialog(player1:Player,player2: Player,navController: NavController,modifier:Modifier)
{
    val isplayer1win=player1.iswinner
    var cur_score=if(isplayer1win) player1.cur_score else player2.cur_score
    var high_score=if(isplayer1win) player1.high_score else player2.high_score
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
                                text = if (isplayer1win) "PLAYER 1 WINS" else "PLAYER 2 WINS",
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
                                    text = "$high_score",
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
                                    text = "$cur_score",
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
                        navController.popBackStack()
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
                    navController.popBackStack()
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
fun copy_tmp_to_org_coordinates(player: Player){

}
fun convert_to_pair(offset: Offset):Pair<Float,Float>{
    return Pair(offset.x,offset.y)
}
fun convert_to_offset(pair: Pair<Float,Float>):Offset{
    return Offset(pair.first,pair.second)
}