package com.example.socialk.create

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.socialk.ui.theme.Inter
import com.example.socialk.R
import com.example.socialk.ui.theme.SocialTheme

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CreateActivityButton(modifier:Modifier=Modifier, onClick: () -> Unit, text: String, color:Color=SocialTheme.colors.iconInteractive,
                         textColor:Color=Color.White,icon:Int=R.drawable.ic_done,content:@Composable ()->Unit) {
    Card(
        modifier = modifier
            .height(48.dp)
            .padding(horizontal = 24.dp) .animateContentSize(),
        shape = RoundedCornerShape(100.dp),
        elevation=0.dp,
        backgroundColor = color,
        onClick =onClick
    ) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(painter = painterResource(id = R.drawable.ic_done), contentDescription =null,tint=Color.White )
             Spacer(modifier = Modifier.width(8.dp))
                content()

            }

        }
    }
}
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun GoToMapButton(onClick: () -> Unit,modifier:Modifier,text:String="Visit map",icon:Int=R.drawable.ic_map,color:Color=SocialTheme.colors.iconInteractive, textColor:Color=Color.White ,backgroundColor:Color=Color.Transparent,borderColor:Color=SocialTheme.colors.iconInteractive) {
    Card(
        modifier = modifier
            .height(48.dp)
            .padding(horizontal = 24.dp),
        shape = RoundedCornerShape(100.dp),
        elevation=0.dp,
        backgroundColor =backgroundColor,
        onClick =onClick,
        border = BorderStroke(1.dp,borderColor)
    ) {
        Box(modifier = modifier.fillMaxSize().background(color= backgroundColor), contentAlignment = Alignment.Center) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(painter = painterResource(id = icon), contentDescription =null,tint=Color.White )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = text,
                    style = TextStyle(
                        color = textColor, fontSize = 16.sp,
                        fontFamily = Inter, fontWeight = FontWeight.Medium
                    )
                )
            }

        }
    }
}
