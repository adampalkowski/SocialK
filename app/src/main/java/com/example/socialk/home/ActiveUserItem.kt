package com.example.socialk.home

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
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
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ActiveUserItem(profileUrls: HashMap<String,String>, usernames: HashMap<String,String>, onClick: () -> Unit) {
    val rainbowColorsBrush = remember {
        Brush.sweepGradient(
            listOf(
                Color(0xFF9575CD),
                Color(0xFFBA68C8),
                Color(0xFFE57373),
                Color(0xFFFFB74D),
                Color(0xFFFFF176),
                Color(0xFFAED581),
                Color(0xFF4DD0E1),
                Color(0xFF9575CD)
            )
        )
    }
    Box(modifier = Modifier
        .padding(6.dp).clickable(onClick = onClick)) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Row(     horizontalArrangement = Arrangement.spacedBy((-20).dp)) {
              profileUrls.values.toList().take(3).reversed().forEachIndexed { index, it ->
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(it)
                                .crossfade(true)
                                .build(),
                            placeholder = painterResource(R.drawable.ic_person),
                            contentDescription = "image sent",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)  .border(
                                    BorderStroke(2.dp, rainbowColorsBrush),
                                    CircleShape
                                )
                                .zIndex(  profileUrls.values.toList().size - index.toFloat())
                        )
              }

                if (profileUrls.values.toList().size > 3) {
                    Card(modifier = Modifier.size(48.dp), shape = CircleShape) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(profileUrls.values.toList().get(3))
                                .crossfade(true)
                                .build(),
                            placeholder = painterResource(R.drawable.ic_person),
                            contentDescription = "image sent",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black.copy(alpha = 0.5f))
                        ) {
                            // Content
                            androidx.compose.material.Text(
                                modifier = Modifier.align(Alignment.Center),
                                textAlign = TextAlign.Center,
                                text = "+" + (usernames.values.toList().size - 3).toString(),
                                style = TextStyle(
                                    fontFamily = Inter,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Normal
                                ),
                                color = SocialTheme.colors.textSecondary,

                                )
                        }
                    }


                }

            }

            Spacer(modifier = Modifier.height(4.dp))
            val combinedUsernames = usernames.values.toList().joinToString(", ").take(30)
            Text(
                modifier= Modifier.widthIn(0.dp, max=if(combinedUsernames.length<8){60.dp}else if (combinedUsernames.length<20){80.dp}else{120.dp}),
                text = combinedUsernames,
                textAlign = TextAlign.Center,
                color=SocialTheme.colors.textPrimary,
                style = TextStyle(
                    fontFamily = Inter,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.ExtraLight
                ),maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }

}