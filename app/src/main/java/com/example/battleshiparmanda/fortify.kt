package com.example.battleshiparmanda

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

fun clearplayergrid(player:Player){
    for(i in 0 until player.grid_size){
        for(j in 0 until player.grid_size){
            if(player.grid[i][j].cellState==CellState.EMPTY || (player.grid[i][j].cellState==CellState.SHIP)){
                player.grid[i][j].cellState=CellState.EMPTY
            }
        }
    }
}
@Composable
fun fortify(heading:String,description:String,player1_turn: Boolean,isdeploy:Boolean,ondelete:() ->Unit,onsave:() ->Unit,modifier: Modifier = Modifier){
    Box(
        modifier= Modifier.fillMaxWidth()
            .wrapContentHeight()
            .then(modifier)
            .graphicsLayer { rotationZ =if(player1_turn) 0f else 180f}
            .background(colorResource(R.color.gray),shape= RoundedCornerShape(topStart = 48.dp, topEnd = 48.dp)),
    ){
        Column(horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
            modifier= Modifier.fillMaxWidth().padding(top=8.dp,bottom=8.dp)){
            Text(
                text=heading,
                fontSize = 60.sp,
                textAlign = TextAlign.Center,
                fontFamily = FontFamily(Font(R.font.fortifydeploy)),
                color= Color.White,
                modifier= Modifier.align(Alignment.CenterHorizontally)
            )
            Text(
                text=description,
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                color= Color.White,
                modifier= Modifier.align(Alignment.CenterHorizontally)
            )
            Spacer(modifier= Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth().align(
                Alignment.CenterHorizontally)) {
                Button(
                    onClick = {
                        onsave()
                    },
                    shape = RoundedCornerShape(16.dp),
                    colors= ButtonDefaults.buttonColors(
                        containerColor = Color.Cyan
                    )
                ){
                    Text(
                        text="Save",
                        fontSize = 30.sp,
                        fontFamily = FontFamily(Font(R.font.fortifydeploy)),
                        textAlign = TextAlign.Center,
                        color= Color.White,
                    )
                }
                if(isdeploy){
                    //place the auto button
                    Spacer(modifier= Modifier.width(30.dp))
                    Button(
                        onClick = {

                        },
                        shape = RoundedCornerShape(16.dp),
                        colors= ButtonDefaults.buttonColors(
                            containerColor = colorResource(R.color.purple)
                        )
                    ){
                        Text(
                            text = "AUTO",
                            fontSize = 30.sp,
                            fontFamily = FontFamily(Font(R.font.fortifydeploy)),
                            textAlign = TextAlign.Center,
                            color= Color.White,

                            )
                    }
                }
            }
            Spacer(modifier= Modifier.height(13.dp))
            Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth().align(
                Alignment.CenterHorizontally)) {
                Image(
                    painter= painterResource(R.drawable.dustbin),
                    contentDescription = null,
                    modifier= Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .clickable { ondelete() }
                    ,
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier= Modifier.width(32.dp))
                Image(
                    painter= painterResource(R.drawable.correct),
                    contentDescription = null,
                    modifier= Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .clickable { onsave() },
                    contentScale = ContentScale.Crop
                )
            }
            Spacer(modifier= Modifier.height(10.dp))
        }
    }
}