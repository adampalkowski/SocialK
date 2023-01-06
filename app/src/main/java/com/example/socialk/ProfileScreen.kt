package com.example.socialk

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.IconButton
import androidx.compose.material.Surface
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.socialk.chat.ChatEvent
import com.example.socialk.components.BottomBarRow
import com.example.socialk.home.HomeEvent
import com.example.socialk.ui.theme.Ocean1
import com.example.socialk.ui.theme.SocialTheme

sealed class ProfileEvent{
    object GoToProfile : ProfileEvent()
    object LogOut : ProfileEvent()
    object GoToSettings : ProfileEvent()

}
@Composable
fun ProfileScreen(onEvent: (ProfileEvent) -> Unit, bottomNavEvent:(Destinations)->Unit){
    Surface(modifier = Modifier
        .fillMaxSize()
       , color = SocialTheme.colors.uiBackground
    ) {
        Box(
            modifier = Modifier
                .heightIn(56.dp)
                .padding(vertical = 16.dp), contentAlignment = Alignment.TopEnd
        ) {
            IconButton(onClick = { onEvent(ProfileEvent.GoToSettings) }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_settings),
                    tint= SocialTheme.colors.iconPrimary,
                    contentDescription = null
                )
            }
        }

        Box(modifier = Modifier
            .fillMaxWidth()
            .heightIn(56.dp)
            .padding(vertical = 16.dp), contentAlignment = Alignment.TopEnd){


        }
        Box(modifier = Modifier
            .fillMaxWidth()
            .heightIn(56.dp)
            ,contentAlignment = Alignment.BottomCenter){
            BottomBarRow(allScreens = bottomTabRowScreens, onTabSelected = { screen->bottomNavEvent(screen)},currentScreen = Profile)
        }
    }
}
