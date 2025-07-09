package com.example.battleshiparmanda

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowInsetsController
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.TransformableState
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role.Companion.Image
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.view.WindowCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.privacysandbox.tools.core.model.Type
import com.example.battleshiparmanda.ui.theme.BATTLESHIPARMANDATheme


class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        window.decorView.apply {
            systemUiVisibility = (
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                            or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            or View.SYSTEM_UI_FLAG_FULLSCREEN
                    )
        }
        window.decorView.setOnSystemUiVisibilityChangeListener { visibility ->
            // If system bars are visible, reapply immersive mode
            if (visibility and View.SYSTEM_UI_FLAG_FULLSCREEN == 0) {
                window.decorView.apply {
                    systemUiVisibility = (
                            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                    or View.SYSTEM_UI_FLAG_FULLSCREEN
                            )
                }
            }
        }

        setContent {
            BATTLESHIPARMANDATheme {
                StartGame()
            }
        }
    }
}
@Composable
fun StartGame(gameViewModel: GameViewModel=viewModel(), navController:NavHostController=rememberNavController()){
    NavHost(navController=navController, startDestination = screens.HOME.name,modifier=Modifier.fillMaxSize().statusBarsPadding()){
        composable(screens.HOME.name){
            gameViewModel.GetPlayersHistoryAndSetHighScore(context = LocalContext.current)
            StartScreen(settingsclick = {navController.navigate(screens.SETTINGS.name)}, PlayClick = {navController.navigate(screens.GAME.name)})
        }
        composable(screens.SETTINGS.name) {
            SettingsUI(gameViewModel=gameViewModel, navigateUp = {navController.navigateUp()})
        }
        composable(screens.GAME.name){
            gameViewModel.ResetGame()
            MainGame(gameViewModel=gameViewModel, navController=navController)
        }
    }
}
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
                .size(200.dp, 200.dp)
                .offset(y=-10.dp)
                .clip(CircleShape)
                .clickable{
                    PlayClick()
                }

        )
        Image(
            painter= painterResource(R.drawable.settings),
            contentDescription = null,
            modifier=Modifier
                .size(150.dp,150.dp)
                .align(Alignment.BottomStart)
                .padding(24.dp)
                .clip(CircleShape)
                .clickable{
                    settingsclick()
                }
        )
        Image(
            painter= painterResource(R.drawable.info),
            contentDescription = null,
            modifier=Modifier
                .size(150.dp,150.dp)
                .align(Alignment.BottomEnd)
                .padding(24.dp)
                .clip(CircleShape)
                .clickable{
                    helpopen=true
                }
        )
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
fun get_player_high_score(player_name:Players,lst:MutableList<GameHistoryData>):Int{
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