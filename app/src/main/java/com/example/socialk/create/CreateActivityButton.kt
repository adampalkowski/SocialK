package com.example.socialk.create

import androidx.compose.foundation.layout.*
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
fun CreateActivityButton(modifier:Modifier, onClick: () -> Unit, text: String, color:Color=Color(
    0xFF00083A
), textColor:Color=SocialTheme.colors.textSecondary) {
    Card(
        modifier = modifier
            .height(48.dp).fillMaxWidth().padding(horizontal = 24.dp),
        shape = RoundedCornerShape(100.dp),
        elevation=0.dp,
        backgroundColor = color,
        onClick =onClick
    ) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                text = text,
                style = TextStyle(
                    color = textColor, fontSize = 16.sp,
                    fontFamily = Inter, fontWeight = FontWeight.ExtraBold
                )
            )
        }
    }
}