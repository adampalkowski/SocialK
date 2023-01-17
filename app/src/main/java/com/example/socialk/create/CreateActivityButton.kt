package com.example.socialk.create

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.socialk.ui.theme.Inter
import com.example.socialk.ui.theme.SocialTheme

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CreateActivityButton(onClick: () -> Unit, text: String) {
    Card(
        modifier = Modifier
            .height(56.dp)
            .width(300.dp),
        shape = RoundedCornerShape(16.dp),
        backgroundColor = Color(0xFF494949),
        onClick =onClick
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                text = text,
                style = TextStyle(
                    color = SocialTheme.colors.textSecondary, fontSize = 18.sp,
                    fontFamily = Inter, fontWeight = FontWeight.Bold
                )
            )
        }
    }
}