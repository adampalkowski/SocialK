package com.example.socialk.map

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.socialk.Chats
import com.example.socialk.Destinations
import com.example.socialk.bottomTabRowScreens
import com.example.socialk.components.BottomBarRow
import com.example.socialk.ui.theme.Ocean1
import com.example.socialk.ui.theme.Ocean3
import com.example.socialk.ui.theme.SocialTheme


sealed class MapEvent{
    object GoToProfile : MapEvent()
    object LogOut : MapEvent()
    object GoToSettings : MapEvent()

}

@Composable
fun MapScreen( onEvent: (MapEvent) -> Unit, bottomNavEvent:(Destinations)->Unit){
    Surface(modifier = Modifier
        .fillMaxSize()
       , color = SocialTheme.colors.uiBackground
    ) {
        Box(modifier = Modifier
            .fillMaxWidth()
            .heightIn(56.dp)
            .padding(vertical = 16.dp), contentAlignment = Alignment.TopEnd){


        }
        Box(modifier = Modifier
            .fillMaxWidth()
            .heightIn(56.dp)
            ,contentAlignment = Alignment.BottomCenter){
            BottomBarRow(allScreens = bottomTabRowScreens, onTabSelected = { screen->bottomNavEvent(screen)},currentScreen = com.example.socialk.Map)
        }
    }
}
