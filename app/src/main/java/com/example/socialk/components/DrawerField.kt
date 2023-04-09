package com.example.socialk.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.socialk.R
import com.example.socialk.ui.theme.Inter
import com.example.socialk.ui.theme.SocialTheme

@Composable
fun DrawerField(
    modifier: Modifier = Modifier,
    title: String,
    icon: Int,
    subtitle: String? = null,
    onClick: () -> Unit,
    content:@Composable () -> Unit ={}
) {


    Box(modifier = modifier
        .fillMaxWidth().clickable(onClick = onClick)
        .padding(vertical = 16.dp)) {
        Column() {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Start) {
                Spacer(modifier = Modifier.width(12.dp))
                Icon(
                    painter = painterResource(id = icon),
                    tint = SocialTheme.colors.iconPrimary,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(24.dp))
                Text(text =title, style = TextStyle(color=SocialTheme.colors.textPrimary,
                    fontFamily = Inter, fontWeight = FontWeight.Medium, fontSize = 16.sp))
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    painter = painterResource(id = R.drawable.ic_right),
                    tint = SocialTheme.colors.iconPrimary,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(24.dp))
            }
            content()
        }

    }
}

@Composable
fun DrawerProfileField(
    modifier: Modifier = Modifier,
    icon: Int,
    picture_url:String,
    username:String,
    fullName:String,
    subtitle: String? = null,
    onClick: () -> Unit
) {
    Box(modifier = modifier
        .fillMaxWidth()
        .padding(vertical = 16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Start) {
            Spacer(modifier = Modifier.width(12.dp))
            AsyncImage(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape),
                model = ImageRequest.Builder(LocalContext.current)
                    .data(picture_url)
                    .crossfade(true)
                    .build(),
                placeholder = painterResource(icon),
                contentDescription = "profile picture",
                contentScale = ContentScale.Crop,

                )
            Spacer(modifier = Modifier.width(24.dp))
            Column() {
                Text(text =fullName, style = TextStyle(color=SocialTheme.colors.textPrimary,
                    fontFamily = Inter, fontWeight = FontWeight.Medium, fontSize = 16.sp))
                Text(text =username, style = TextStyle(color=SocialTheme.colors.textPrimary,
                    fontFamily = Inter, fontWeight = FontWeight.Light, fontSize = 12.sp))

            }

        }
    }
}