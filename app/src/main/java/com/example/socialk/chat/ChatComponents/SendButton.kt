package com.example.socialk.chat.ChatComponents

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.socialk.ui.theme.SocialTheme

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SendButton(onEvent: () -> Unit, icon: Int, available: Boolean) {
    Card(
        modifier = Modifier.size(48.dp),
        shape = RoundedCornerShape(12.dp),
        backgroundColor = if (available) {
            Color(0xff0F0F30)
        } else {
            SocialTheme.colors.iconPrimary
        },
        onClick = onEvent,
        elevation = 0.dp
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = null,

                tint = SocialTheme.colors.textSecondary
            )
        }
    }
}
