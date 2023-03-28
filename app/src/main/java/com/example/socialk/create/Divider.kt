package com.example.socialk.create

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.socialk.ui.theme.SocialTheme

@Composable
fun Divider(){
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(color = SocialTheme.colors.uiFloated)
        )
        Box(
            modifier = Modifier
                .width(300.dp)
                .height(1.dp)
                .background(color = SocialTheme.colors.uiFloated)
        )

    }

}