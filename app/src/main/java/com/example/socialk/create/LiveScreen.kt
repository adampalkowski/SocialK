package com.example.socialk.create

import android.content.res.Configuration
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.socialk.Create
import com.example.socialk.Destinations
import com.example.socialk.R
import com.example.socialk.bottomTabRowScreens
import com.example.socialk.components.BottomBar
import com.example.socialk.components.BottomBarRow
import com.example.socialk.home.cardHighlited
import com.example.socialk.home.cardnotHighlited
import com.example.socialk.ui.theme.Inter
import com.example.socialk.ui.theme.SocialTheme


sealed class LiveEvent{
    object GoToProfile : LiveEvent()
    object LogOut : LiveEvent()
    object GoToSettings : LiveEvent()
    object GoToHome : LiveEvent()
    object GoToLive : LiveEvent()
    object GoToEvent : LiveEvent()
    object GoToActivity: LiveEvent()

}
@Composable
fun LiveScreen (onEvent: (LiveEvent) -> Unit, bottomNavEvent:(Destinations)->Unit){
    Surface(modifier = Modifier
        .fillMaxSize()
        .background(SocialTheme.colors.uiBackground),color= SocialTheme.colors.uiBackground
    ) {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(12.dp))
            activityPickerLive(isSystemInDarkTheme(), onEvent = { event -> onEvent(event) })
            Spacer(modifier = Modifier.height(12.dp))


            createField(
                column = false,
                action = {
                    //TODO HARDCODED  TIME length, implement onclick,hardoced color
                    ClickableText(text = AnnotatedString("1 hour"), style = TextStyle(
                        color = Color(0xFF494949),
                        fontSize = 18.sp,
                        fontFamily = Inter,
                        fontWeight = FontWeight.SemiBold,
                    ), onClick = {})
                },
                title = "Time length",
                icon = R.drawable.ic_hourglass
            )
            createField(
                column = false,
                action = {
                    //todo hardcoced false switch , on change ??
                    Switch(colors = SwitchDefaults.colors(checkedThumbColor =Color.Black ,
                    uncheckedThumbColor = Color.Gray,
                    checkedTrackColor =Color.Black ,
                    uncheckedTrackColor =  Color.Gray),checked = false, onCheckedChange = {})
                },
                title = "Current location",
                icon = R.drawable.ic_my_location
            )

            Spacer(modifier = Modifier.height(48.dp))
            CreateActivityButton(onClick = {}, text = "Create activity")
            Spacer(modifier = Modifier.height(64.dp))
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(56.dp), contentAlignment = Alignment.BottomCenter
        ) {
            BottomBarRow(
                allScreens = bottomTabRowScreens,
                onTabSelected = { screen -> bottomNavEvent(screen) },
                currentScreen = Create
            )
        }
    }

        Box(modifier = Modifier
            .fillMaxWidth()
            .heightIn(56.dp)
            .padding(vertical = 16.dp), contentAlignment = Alignment.TopEnd){


        }
        BottomBar(onTabSelected = { screen->bottomNavEvent(screen)},currentScreen = Create)

}

@Composable
fun activityPickerLive(isDark:Boolean,modifier: Modifier = Modifier, onEvent: (LiveEvent) -> Unit) {
    Row(modifier = Modifier
        .fillMaxWidth()
        .height(48.dp)
        .padding(horizontal = 8.dp), horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically) {

        cardHighlited(text="Live", isDark = isDark )
        Spacer(Modifier.width(6.dp))
        cardnotHighlited(text="Activity",onEvent=  {onEvent(LiveEvent.GoToActivity)} )
        Spacer(Modifier.width(6.dp))
        cardnotHighlited(text="Event",onEvent=  {onEvent(LiveEvent.GoToEvent)} )
    }

}

    @Preview(showBackground = true)
@Composable
fun previewLiveScreen () {
    SocialTheme{
        LiveScreen(onEvent = {}, bottomNavEvent = {})
    }
}
@Preview(showBackground = true,uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun previewLiveScreenDark(){
    SocialTheme{
        LiveScreen(onEvent = {}, bottomNavEvent = {})
    }
}
