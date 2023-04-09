package com.example.socialk.DrawerFragments

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.socialk.components.HomeScreenHeading
import com.example.socialk.di.ActivityViewModel
import com.example.socialk.ui.theme.SocialTheme

sealed class InfoEvent{
    object GoBack:InfoEvent()
}


@Composable
fun InfoScreen(onEvent:(InfoEvent)->Unit){
    Box(
        Modifier
            .fillMaxSize()
            .background(color = SocialTheme.colors.uiBackground)){
        Column {
            HomeScreenHeading(onEvent = { onEvent(InfoEvent.GoBack)}, title = "Info")
            com.example.socialk.chat.ChatComponents.Divider()

        }
    }
}