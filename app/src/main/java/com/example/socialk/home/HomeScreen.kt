package com.example.socialk.home

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.IconButton
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Surface
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.*
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.socialk.*
import com.example.socialk.R
import com.example.socialk.components.ActivityItem
import com.example.socialk.components.BottomBar
import com.example.socialk.components.BottomBarRow
import com.example.socialk.signinsignup.AuthViewModel
import com.example.socialk.ui.theme.Inter
import com.example.socialk.ui.theme.Ocean1
import com.example.socialk.ui.theme.SocialTheme
import java.util.*

sealed class HomeEvent {
    object GoToProfile : HomeEvent()
    object LogOut : HomeEvent()
    object GoToMemories : HomeEvent()
    object GoToSettings : HomeEvent()
}

@Composable
fun HomeScreen(
    viewModel: AuthViewModel?,
    onEvent: (HomeEvent) -> Unit,
    bottomNavEvent: (Destinations) -> Unit
) {
    val isDark = isSystemInDarkTheme()
    val scaffoldState= rememberScaffoldState()

        androidx.compose.material.Scaffold(
            scaffoldState =scaffoldState,
            bottomBar ={
                BottomBar(
                    onTabSelected = { screen -> bottomNavEvent(screen) },
                    currentScreen = Home
                )
            },
            content = { it->
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(SocialTheme.colors.uiBackground), color = SocialTheme.colors.uiBackground
                ) {
                HomeScreenContent(it,isDark=isDark,onEvent={homeEvent ->onEvent(homeEvent)  })
                }

            }
        )




}

@Composable
fun HomeScreenContent(padding:PaddingValues,isDark:Boolean,onEvent: (HomeEvent) -> Unit){

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
            Spacer(modifier = Modifier.height(56.dp))
            ActivityItem(
                "Adamo",
                "URl",
                "Starts in 12 minutes",
                "LONG TITLEVERY LONG TITLEVERY LONG TITLEVERY LONG TITLEVERY LONG TITLE",
                "LONG TITLEVERY LONG TITLEVERY LONG TITLEVERY LONG TITLEVERY LONG TITLE",
                "DATE",
                "15:15 - 15:15",
                "location"
            )
            ActivityItem(
                "Adsamo",
                "UsRl",
                "Starts isn 12 minutes",
                "LONG TITLEVERY LONG TIsTLEVERY LONG TITLEVERY LONG TITLEVERY LONG TITLE",
                "LONG TITLsEVERY LONG TITLEVERY LONG TITLEVERY LONG TITLEVERY LONG TITLE",
                "DATE",
                "15:15 - 15:15",
                "location"
            )
            ActivityItem(
                "Adamo",
                "URl",
                "Starts in 12 minutes",
                "LONG TITLEVERY LONG TITLEVERY LONG TITLEVERY LONG TITLEVERY LONG TITLE",
                "LONG TITLEVERY LONG TITLEVERY LONG TITLEVERY LONG TITLEVERY LONG TITLE",
                "DATE",
                "15:15 - 15:15",
                "location"
            )
            Spacer(modifier = Modifier.height(56.dp))


        }

    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(56.dp)
            .padding(vertical = 12.dp), contentAlignment = Alignment.TopCenter
    ) {
        topBar(
            isDark,
            onEvent = { onEvent(HomeEvent.GoToMemories) },
            picked_screen = "Activities"
        )
    }


    Box(modifier = Modifier, contentAlignment = Alignment.BottomEnd) {


    }


}

@Composable
fun topBar(
    isDark: Boolean,
    modifier: Modifier = Modifier,
    onEvent: () -> Unit,
    picked_screen: String
) {
    Row(
        modifier = Modifier
            .width(200.dp)
            .height(48.dp)
            .padding(horizontal = 8.dp), horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (picked_screen.equals("Activities")) {
            cardHighlited(text = "Activities", isDark = isDark)
            Spacer(Modifier.width(6.dp))
            cardnotHighlited(text = "Memories", onEvent = onEvent)
        } else {
            cardnotHighlited(text = "Activities", onEvent = onEvent)
            Spacer(Modifier.width(6.dp))
            cardHighlited(text = "Memories", isDark = isDark)
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun cardnotHighlited(text: String, onEvent: () -> Unit) {
    Card(
        modifier = Modifier
            .width(90.dp)
            .height(40.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors
            (containerColor = Color.Transparent),
        onClick = onEvent
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = text, fontSize = 14.sp, color = Color(0xffB0B0B0), style = TextStyle(
                    fontFamily = Inter,
                    fontWeight = FontWeight.Medium
                ), textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun cardHighlited(isDark: Boolean, text: String) {
    Column(
        modifier = Modifier
            .height(40.dp)
            .width(90.dp)
            .padding(6.dp),
        horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = text,
            fontSize = 14.sp,
            color = if (isDark) Color.White else Color(0xFF25232A),
            modifier = Modifier,
            style = TextStyle(fontFamily = Inter, fontWeight = FontWeight.SemiBold),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .background(color = if (isDark) Color.White else Color(0xFF25232A))
                .height(1.dp)
                .width(40.dp)
        )
    }

}


@Preview
@Composable
fun previewHomeScreen() {
    SocialTheme {
        HomeScreen(null, onEvent = {}, bottomNavEvent = {})
    }
}


@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
fun previewHomeScreenDark() {
    SocialTheme {
        HomeScreen(null, onEvent = {}, bottomNavEvent = {})
    }
}


