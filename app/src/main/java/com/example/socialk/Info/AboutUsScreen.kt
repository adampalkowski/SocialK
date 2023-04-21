package com.example.socialk.Info

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.socialk.DrawerFragments.InfoEvent
import com.example.socialk.chat.ChatComponents.Divider
import com.example.socialk.components.HomeScreenHeading
import com.example.socialk.ui.theme.SocialTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutUsScreen(navController: NavController, onEvent:(InfoEvent)->Unit) {
    // Render the Screen 2 content
    Box(
        Modifier
            .fillMaxSize()
            .background(color = SocialTheme.colors.uiBackground)){
        Column {
            HomeScreenHeading(onEvent = { onEvent(InfoEvent.GoBack)}, title = "About us")
            Divider()


        }

    }

}