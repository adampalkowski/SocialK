package com.example.socialk.components

import android.view.animation.AlphaAnimation
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.socialk.R
import com.example.socialk.ui.theme.Inter

@Composable
fun UploadBar(icon_anim:Boolean,text:String,icon:Int){
    val infiniteTransition = rememberInfiniteTransition()
    val progress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(36.dp)
            .background(color = Color.LightGray.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.width(24.dp))
            Icon(
                modifier = Modifier.size(24.dp),
                painter = painterResource(id = icon),
                tint = Color.White,
                contentDescription = null
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = text,
                color = Color.White,
                style = TextStyle(
                    fontFamily = Inter,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                ),
            )
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                modifier = Modifier
                    .size(24.dp)
                    .alpha(alpha = progress),
                painter = painterResource(id = R.drawable.ic_publish_with_changes_700),
                tint = Color.White,
                contentDescription = null
            )
            Spacer(modifier = Modifier.width(24.dp))


        }
    }
}

@Composable
fun ErrorBar(icon_anim:Boolean,text:String,icon:Int){
    val infiniteTransition = rememberInfiniteTransition()
    val progress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(36.dp)
            .background(color = Color.Red.copy(alpha = 0.6f))
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.width(24.dp))
            Icon(
                modifier = Modifier.size(24.dp),
                painter = painterResource(id = icon),
                tint = Color.White,
                contentDescription = null
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = text,
                color = Color.White,
                style = TextStyle(
                    fontFamily = Inter,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                ),
            )
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                modifier = Modifier
                    .size(24.dp)
                    .alpha(alpha = progress),
                painter = painterResource(id = R.drawable.ic_publish_with_changes_700),
                tint = Color.White,
                contentDescription = null
            )
            Spacer(modifier = Modifier.width(24.dp))


        }
    }
}