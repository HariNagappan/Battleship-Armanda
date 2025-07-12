package com.example.battleshiparmanda

import android.R.attr.bottom
import android.R.attr.top
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

@Composable
fun StartScreen(settingsclick:()->Unit,PlayClick:()->Unit){
    var helpopen by remember{mutableStateOf(false)}

    Box(
        modifier=Modifier
            .fillMaxSize()
            .padding(WindowInsets.systemBars.asPaddingValues()),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(R.drawable.main_menu_background),
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            contentDescription = null
        )
        Column (
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top=30.dp),
            verticalArrangement = Arrangement.spacedBy(5.dp),
            horizontalAlignment = Alignment.CenterHorizontally

        ){
            Text(
                text = "BATTLESHIP",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 35.sp,
                fontFamily = FontFamily(Font(R.font.heading)),
                textAlign = TextAlign.Center
            )
            Text(
                text = "ARMANDA",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 35.sp,
                fontFamily = FontFamily(Font(R.font.heading)),
                textAlign = TextAlign.Center
            )
        }
        Image(
            painter = painterResource(R.drawable.play),
            contentDescription = null,
            modifier = Modifier
                .size(165.dp)
                .clip(CircleShape)
                .clickable{
                    PlayClick()
                }

        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier=Modifier.fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(
                    start=16.dp,
                    end=16.dp,
                    bottom = WindowInsets.systemBars.asPaddingValues().calculateBottomPadding()+36.dp)) {
            Image(
                painter = painterResource(R.drawable.settings),
                contentDescription = null,
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .clickable {
                        settingsclick()
                    }
            )
            Spacer(modifier=Modifier.weight(1f))
            Image(
                painter = painterResource(R.drawable.info),
                contentDescription = null,
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .clickable {
                        helpopen = true
                    }
            )
        }
        if(helpopen){
            HelpDialog(stringResource(R.string.help), stringResource(R.string.game_help),{helpopen=false})
        }
    }

}
@Composable
fun HelpDialog(Heading:String,Body:String,onDismiss:() ->Unit){
    Dialog(
        onDismissRequest = onDismiss
    )
    {
        Card(modifier = Modifier
            .fillMaxWidth()
            .height(500.dp)
            .padding(5.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column() {
                Box (modifier=Modifier
                    .padding(8.dp)){
                    Text(
                        text = Heading,
                        textAlign = TextAlign.Center,
                        fontSize = 20.sp,
                        fontFamily = FontFamily.Monospace,
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
                Column (
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(8.dp)
                ){
                    Text(
                        text=Body,
                        fontSize = 15.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxSize()
                    )
                }
            }
        }
    }
}
fun GetPlayerHighScore(player_name:Players,lst:MutableList<GameHistoryData>):Int{
    var high=0
    for(i in lst){
        if(i.winner==player_name){
            if(high<i.winner_player_score){
                high=i.winner_player_score
            }
        }
        else{
            if(high<i.loser_player_score){
                high=i.loser_player_score
            }
        }
    }
    return high
}