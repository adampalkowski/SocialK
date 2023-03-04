package com.example.socialk.chat.ChatComponents

import androidx.compose.foundation.BorderStroke
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
import com.example.socialk.ui.theme.Shapes
import com.example.socialk.ui.theme.SocialTheme

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ChatButton(
    onEvent: () -> Unit,
    icon: Int,
    iconTint: Color = SocialTheme.colors.iconPrimary,
    selected: Boolean = false
) {

    Card(
        modifier = Modifier.size(48.dp),
        shape = Shapes.medium,
        backgroundColor = if (selected) {
            SocialTheme.colors.textInteractive
        } else {
            SocialTheme.colors.uiBackground
        },
        onClick = onEvent,
        elevation = 0.dp,
        border = BorderStroke(1.dp, color = SocialTheme.colors.uiFloated)
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = null,
                tint = if (selected) {
                    Color.White
                } else {
                    iconTint
                }
            )
        }
    }
}