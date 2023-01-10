package com.example.socialk.chat

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
import com.example.socialk.*
import com.example.socialk.R
import com.example.socialk.components.BottomBar
import com.example.socialk.components.BottomBarRow
import com.example.socialk.signinsignup.AuthViewModel
import com.example.socialk.ui.theme.Ocean1
import com.example.socialk.ui.theme.SocialTheme


sealed class ChatEvent{
    object GoToProfile : ChatEvent()
    object LogOut : ChatEvent()
    object GoToSettings : ChatEvent()

}

@Composable
fun ChatScreen( onEvent: (ChatEvent) -> Unit, bottomNavEvent:(Destinations)->Unit){
    Surface(modifier = Modifier
        .fillMaxSize()
        , color = SocialTheme.colors.uiBackground
    ) {
        Box(modifier = Modifier
            .fillMaxWidth()
            .heightIn(56.dp)
            .padding(vertical = 16.dp), contentAlignment = Alignment.TopEnd){


        }
        BottomBar( onTabSelected = { screen->bottomNavEvent(screen)},currentScreen = Chats)
    }
}
