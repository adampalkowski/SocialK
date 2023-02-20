package com.example.socialk.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.socialk.R
import com.example.socialk.ui.theme.Inter
import com.example.socialk.ui.theme.SocialTheme

//todo hardcoded text style
/*
HOME TOP ROW displays active user profile picture and username, provides on click to action
*/
@Composable
fun ActiveUserItem(profileUrl: String, username: String, onClick: () -> Unit) {
    Box(modifier = Modifier.padding(6.dp).clickable { onClick }) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(profileUrl)
                    .crossfade(true)
                    .build(),
                placeholder = painterResource(R.drawable.ic_person),
                contentDescription = "image sent",
                contentScale = ContentScale.Crop,
                modifier = Modifier     .size(48.dp)
                    .clip(CircleShape)
            )

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                modifier= Modifier.widthIn(0.dp, max=if(username.length<8){60.dp}else if (username.length<20){80.dp}else{100.dp}),
                text = username,
                textAlign = TextAlign.Center,
                color=SocialTheme.colors.textPrimary,
                style = TextStyle(
                    fontFamily = Inter,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.ExtraLight
                )
            )
        }
    }

}