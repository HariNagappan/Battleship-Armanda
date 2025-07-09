package com.example.battleshiparmanda

import android.R.attr.top
import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.fontResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

@Composable
fun SettingsUI(gameViewModel: GameViewModel, navigateUp:() ->Unit) {
    var show_history_dialog by remember{ mutableStateOf(false) }
    Box(modifier=Modifier
        .background(colorResource(R.color.light_gray)))
        {
        Column(
            modifier = Modifier
                .fillMaxSize())
        {
            Box(
                modifier = Modifier.padding(8.dp).fillMaxWidth()
            ) {
                Text(
                    text = stringResource(R.string.settings),
                    fontSize = 30.sp,
                    fontFamily = FontFamily(Font(R.font.heading)),
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .align(Alignment.Center)
                )
                IconButton(onClick = navigateUp) {
                    Icon(
                        //painter = painterResource(R.drawable.back),
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = null,
                        tint=colorResource(R.color.white),
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .border(width = 1.dp,color=colorResource(R.color.white),shape=CircleShape)
                            .padding(4.dp)
                            .size(40.dp)
                    )
                }
            }
            Box(
                modifier=Modifier
                    .fillMaxWidth()
                    .padding(4.dp)
                    .clickable {
                        show_history_dialog=true
                    }
            ){
                Text(
                    text="Game History",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color=Color.White,
                    modifier=Modifier.align(Alignment.Center)
                )
            }
            if(show_history_dialog){
                HistoryDialog(
                    history_list = gameViewModel.history_list
                    ,onDismiss = {
                    show_history_dialog=false
                })
            }
        }
    }
}
@Composable
fun HistoryDialog(history_list:List<GameHistoryData>, onDismiss:() -> Unit){
    Dialog(
        onDismissRequest = {onDismiss()}
    ) {
        Card(modifier = Modifier
            .fillMaxWidth()
            .height(500.dp)
            .padding(5.dp),
            colors = CardDefaults.cardColors(containerColor = colorResource(R.color.light_gray)),
            shape = RoundedCornerShape(16.dp)
        ) {
            Box(modifier=Modifier.fillMaxSize()){
                if(history_list.size==0){
                    Text(
                        text="No Games Played Yet",
                        fontSize = 20.sp,
                        color=Color.White,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                Column(modifier = Modifier.fillMaxSize()) {
                    Box(
                        modifier = Modifier
                            .padding(8.dp)
                    ) {
                        Text(
                            text = "Game History",
                            textAlign = TextAlign.Center,
                            fontSize = 20.sp,
                            fontFamily = FontFamily.Monospace,
                            color = Color.White,
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.Center)

                        )
                        Image(
                            painter = painterResource(R.drawable.close),
                            contentDescription = "close",
                            modifier = Modifier
                                .clip(CircleShape)
                                .size(32.dp)
                                .clickable {
                                    onDismiss()
                                }
                                .align(Alignment.TopEnd),
                            contentScale = ContentScale.Crop)
                    }
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(8.dp)
                    ) {
                        if (history_list.size > 0) {
                            for (i in 0 until history_list.size) {
                                HistoryCard(
                                    gameHistoryData = history_list[history_list.size - i - 1],
                                    curgame = history_list.size - i
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
fun HistoryCard(gameHistoryData: GameHistoryData,curgame:Int){
    val isplayer1winner=gameHistoryData.winner==Players.PLAYER1
    Card(
       elevation =CardDefaults.cardElevation(20.dp),
        colors = CardDefaults.cardColors(containerColor = colorResource(R.color.little_black)),
        modifier=Modifier
            .padding(4.dp)
            .fillMaxWidth()

    ){
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
            modifier=Modifier
                .fillMaxWidth()
                .padding(top=dimensionResource(R.dimen.med_padding), bottom = dimensionResource(R.dimen.med_padding))
        ) {
            Text(
                text="Game $curgame",
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                textAlign = TextAlign.Center,
                color=Color.White
            )
            Text(
                text="Winner: "+if(isplayer1winner) "Player 1" else "Player 2",
                color= Color.Cyan
            )
            Row(horizontalArrangement = Arrangement.Center){
                Column(verticalArrangement = Arrangement.SpaceEvenly, horizontalAlignment = Alignment.CenterHorizontally){
                    Text(
                        text="Player 1",
                        fontWeight = FontWeight.Bold,
                        color= if(isplayer1winner) Color.Cyan else Color.Red
                    )
                    Text(
                        text="Score: " + if(isplayer1winner) gameHistoryData.winner_player_score else gameHistoryData.loser_player_score,
                        fontWeight = FontWeight.Bold,
                        color= colorResource(R.color.silver)
                    )
                    Text(
                        text="High Score: " + if(isplayer1winner) gameHistoryData.winner_player_high_score else gameHistoryData.loser_player_high_score,
                        fontWeight = FontWeight.Bold,
                        color= colorResource(R.color.light_gold)
                    )
                }
                Spacer(modifier=Modifier.width(35.dp))
                Column(verticalArrangement = Arrangement.SpaceEvenly, horizontalAlignment = Alignment.CenterHorizontally){
                    Text(
                        text="Player 2",
                        fontWeight = FontWeight.Bold,
                        color= if(!isplayer1winner) Color.Cyan else Color.Red
                    )
                    Text(
                        text="Score: " + if(!isplayer1winner) gameHistoryData.winner_player_score else gameHistoryData.loser_player_score,
                        fontWeight = FontWeight.Bold,
                        color= colorResource(R.color.silver)
                    )
                    Text(
                        text="High Score: " + if(!isplayer1winner) gameHistoryData.winner_player_high_score else gameHistoryData.loser_player_high_score,
                        fontWeight = FontWeight.Bold,
                        color= colorResource(R.color.light_gold)
                    )
                }
            }
        }
    }
}