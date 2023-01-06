package com.example.socialk

import androidx.compose.animation.*
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.socialk.home.HomeEvent
import com.example.socialk.home.cardHighlited
import com.example.socialk.home.cardnotHighlited
import com.example.socialk.home.topBar
import com.example.socialk.ui.theme.Ocean1
import com.example.socialk.ui.theme.SocialTheme


sealed class MemoriesEvent {
    object GoToProfile : MemoriesEvent()
    object LogOut : MemoriesEvent()
    object GoToMemories : MemoriesEvent()
    object GoToSettings : MemoriesEvent()
    object GoToHome : MemoriesEvent()
}

enum class BoxState {
    Collapsed,
    Expanded
}

@Composable
fun MemoriesScreen(
    onEvent: (MemoriesEvent) -> Unit,
    modifier: Modifier = Modifier.background(color = Color.Gray)
) {
    val isDark= isSystemInDarkTheme()
    var visible by remember { mutableStateOf(true) }
    val density = LocalDensity.current
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(Ocean1), color = Ocean1
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(56.dp)
                .padding(vertical = 12.dp), contentAlignment = Alignment.TopCenter
        ) {

            topBar(isDark,onEvent = { onEvent(MemoriesEvent.GoToHome) }, picked_screen = "Memories")
        }
    }
}

@Composable
fun topBar(isDark:Boolean,modifier: Modifier = Modifier, onEvent: () -> Unit, picked_screen: String) {
    Card(
        modifier = Modifier
            .width(200.dp)
            .height(52.dp),
        shape = RoundedCornerShape(100.dp),
        colors = CardDefaults.cardColors(containerColor = SocialTheme.colors.uiBackground)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp), horizontalArrangement = Arrangement.SpaceBetween,
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
}