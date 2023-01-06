package com.example.socialk.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.IconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.socialk.R
import com.example.socialk.ui.theme.Inter
import com.example.socialk.ui.theme.SocialTheme


@Composable
fun ScreenHeading(onClick: () -> Unit,title:String){
    Box(modifier = Modifier.background(color = Color.Transparent)
        .fillMaxWidth()
        .padding(horizontal = 12.dp).padding(top = 4.dp)) {
        IconButton(
            onClick = onClick,
            modifier = Modifier.align(Alignment.CenterStart)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_back),
                contentDescription = null,
                tint =  SocialTheme.colors.iconInteractive,
            )
        }
        Text(
            text = title,
            fontFamily = Inter,
            fontWeight = FontWeight.Normal,
            fontSize = 18.sp,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center),
            textAlign = TextAlign.Center,
            color = Color.Black)
    }
}

@Preview
@Composable
fun previewScreenHeading(){
    ScreenHeading(onClick = { /*TODO*/ }, title ="Screen heading" )
}