package com.example.socialk.DrawerFragments

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.socialk.chat.ChatComponents.Divider
import com.example.socialk.components.HomeScreenHeading
import com.example.socialk.ui.theme.SocialTheme

sealed class HelpEvent{
    object GoBack:HelpEvent()
}

@Composable
fun HelpScreen(onEvent:(HelpEvent)->Unit){
    Box(
        Modifier
            .fillMaxSize()
            .background(color = SocialTheme.colors.uiBackground)){
        Column {
            HomeScreenHeading(onEvent = { onEvent(HelpEvent.GoBack)}, title = "Help")
            Divider()

        }
    }
}