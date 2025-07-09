package com.example.battleshiparmanda

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntOffset
import kotlin.math.abs
import kotlin.math.roundToInt


fun GetRandomGridPositions(for_player: Player):Map<ShipType,Pair<List<IntOffset>, Orientation>>{
    val all_grid_pos=mutableListOf<IntOffset>()
    val final=mutableMapOf(
        ShipType.Ship1 to Pair(mutableListOf<IntOffset>(), Orientation.HORIZONTAL),
        ShipType.Ship2 to Pair(mutableListOf<IntOffset>(), Orientation.HORIZONTAL),
        ShipType.Ship3 to Pair(mutableListOf<IntOffset>(), Orientation.HORIZONTAL),
        ShipType.Ship3_1 to Pair(mutableListOf<IntOffset>(), Orientation.HORIZONTAL),
        )
    for(shiptype in final.keys){
        var random_start_idx= GetRandomStartIndex(for_player=for_player, exclude = all_grid_pos)
        var random_orientation= Orientation.entries.random()
        var lst=GetGridPositionsFromStartIndex(
            start_position = random_start_idx,
            ship_size = shiptype.size,
            ship_orientation = random_orientation)
        //Log.d("general","random_start_index:$random_start_idx,random_orientation:$random_orientation,lst:$lst")
        while (lst.any{it in all_grid_pos} || !IsInBoundary(start_idx = random_start_idx, orientation = random_orientation,for_player=for_player, ship_size = shiptype.size)){
            random_start_idx= GetRandomStartIndex(for_player=for_player, exclude = all_grid_pos)
            random_orientation= Orientation.entries.random()
            lst=GetGridPositionsFromStartIndex(
                start_position = random_start_idx,
                ship_size = shiptype.size,
                ship_orientation = random_orientation)
        }
        all_grid_pos.addAll(lst)
        final[shiptype]=(Pair(lst,random_orientation))
    }
    return final
}
fun IsInBoundary(start_idx: IntOffset,orientation: Orientation,for_player: Player,ship_size: Int): Boolean{
    if(start_idx.x<0 || start_idx.y<0 || start_idx.x>=for_player.grid.size || start_idx.y>=for_player.grid.size){
        return false
    }
    if(orientation== Orientation.HORIZONTAL){
        for(i in 0 until ship_size){
            if(start_idx.y+i>=for_player.grid.size ){
                PrintGrid(player = for_player)
                return false
            }
        }
    }
    else{
        for(i in 0 until ship_size){
            if(start_idx.x+i>=for_player.grid.size){
//              PrintGrid(player = for_player)
                return false
            }
        }
    }
    return true
}
fun GetRandomStartIndex(for_player: Player,exclude:List<IntOffset>): IntOffset{
    var randomrow=(0 until for_player.grid.size).random()
    var randomcol=(0 until for_player.grid.size).random()
    var final= IntOffset(randomrow,randomcol)
    while(final in exclude){
        randomrow=(0 until for_player.grid.size).random()
        randomcol=(0 until for_player.grid.size).random()
        final=IntOffset(randomrow,randomcol)
    }
    return final
}
fun GetGridPositionsFromStartIndex(start_position:IntOffset,ship_size:Int,ship_orientation: Orientation):MutableList<IntOffset>{
    var lst = mutableListOf<IntOffset>()
    for(i in 0 until ship_size){
        if(ship_orientation==Orientation.HORIZONTAL){
            lst.add(IntOffset(start_position.x,start_position.y+i))
        }
        else{
            lst.add(IntOffset(start_position.x + i,start_position.y))
        }
    }
    return lst
}

fun GetGridPositionsFromStartIndex(start_position:IntOffset,ship: Ship):MutableList<IntOffset>{
    var lst = mutableListOf<IntOffset>()
    for(i in 0 until ship.shipType.size){
        if(ship.tmp_orientation==Orientation.HORIZONTAL){
            lst.add(IntOffset(start_position.x,start_position.y+i))
        }
        else{
            lst.add(IntOffset(start_position.x + i,start_position.y))
        }
    }
    return lst
}
fun HasValidShips(player: Player):Boolean{
    val grid_indices:MutableList<IntOffset> =mutableListOf()
    player.ships.forEach { ship ->
        if(IsValidShip(
                start_idx = ship.tmp_ship_grid_start_idx,
                for_player = player,
                ship=ship,
            grid_indices=grid_indices)
        )
        {
            grid_indices.addAll(GetGridPositionsFromStartIndex(start_position = ship.tmp_ship_grid_start_idx,ship=ship))
        }
        else{
            return false
        }
    }
    return true
}
fun IsValidShip(start_idx: IntOffset,for_player: Player,ship: Ship,grid_indices: MutableList<IntOffset>): Boolean{
    //TODO
    if(start_idx.x<0 || start_idx.y<0 || start_idx.x>=for_player.grid.size || start_idx.y>=for_player.grid.size){
        return false
    }
    if(!IsInBoundary(
            start_idx=start_idx,
        orientation = ship.tmp_orientation,
        for_player=for_player,
        ship_size = ship.shipType.size
        )){
        return false
    }
    val lst=GetGridPositionsFromStartIndex(start_position =start_idx,ship=ship)
    if(lst.any{it in grid_indices}){
        Log.d("general","lst has common items in it")
        PrintGrid(player = for_player)
        return false
    }
    return true
}
fun PrintGrid(player: Player) {
    for (row in player.grid) {
        val rowString = row.joinToString(" ") { it.cellState.value.toString() }
        Log.d("general", rowString)
    }
    Log.d("general","\n")
}
fun IsHitOrMiss(cellState: CellState):Boolean{
    return (cellState==CellState.HIT || cellState==CellState.MISS)
}
fun DoVibrate(context: Context){
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
fun IsDefeated(currently_being_attacked_player:Player):Boolean{
    Log.d("general","${currently_being_attacked_player.player} ships:${currently_being_attacked_player.ships}")
    for(ship in currently_being_attacked_player.ships){
        if(ship.grid_positions.isNotEmpty()){
            return false
        }
    }
    return true
}