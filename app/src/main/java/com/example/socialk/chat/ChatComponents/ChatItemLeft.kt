package com.example.socialk.chat.ChatComponents

import android.media.MediaDrm.OnEventListener
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.socialk.R
import com.example.socialk.ui.theme.Inter
import com.example.socialk.ui.theme.SocialTheme

sealed class ChatItemEvent(){
    class OpenLocation(val latLng:String):ChatItemEvent()
    class JoinLive(val live_activity_id:String):ChatItemEvent()
}
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ChatItemLeft(text_type:String,
                 date: String,
                 textMessage: String,
                 onLongPress: () -> Unit,
                 picture_url: String,
                 onClick: () -> Unit,onEvent:(ChatItemEvent)->Unit,displayPicture:Boolean=true
) {
    var itemClickedState by remember {
        mutableStateOf(false)
    }
    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterStart) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            if (itemClickedState) {
                Text(
                    text = date, color = SocialTheme.colors.iconPrimary,
                    style = TextStyle(
                        fontSize = 10.sp,
                        fontFamily = Inter,
                        fontWeight = FontWeight.ExtraLight,
                    )
                )
            } else {

            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                if(displayPicture)
                {
                    Image(
                        painter = rememberAsyncImagePainter(picture_url),
                        contentScale = ContentScale.Crop,
                        contentDescription = "profile image",
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                    )
                }else{
                    Spacer(modifier = Modifier.width(32.dp))
                }

                Spacer(modifier = Modifier.width(6.dp))

                Card(
                    shape = RoundedCornerShape(8.dp),
                    backgroundColor = SocialTheme.colors.uiBackground,
                    border = BorderStroke(1.dp, color = SocialTheme.colors.uiFloated),
                    elevation = 0.dp, onClick = {
                        onClick()
                        (!itemClickedState).also { itemClickedState = it }
                    }
                ) {
                    if (text_type.equals("uri")){
                        Log.d("ImagePicker","display uri"+textMessage.toString())
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(textMessage)
                                .crossfade(true)
                                .build(),
                            placeholder = painterResource(R.drawable.ic_photo_library),
                            contentDescription = "image sent",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                        )
                    }else if(text_type.equals("text")){
                        Box(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                            Text(
                                text = textMessage,
                                style = TextStyle(
                                    fontFamily = Inter,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 14.sp
                                ),
                                color = SocialTheme.colors.textPrimary
                            )
                        }
                    }else if(text_type.equals("latLng")){
                        Box(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(painter = painterResource(id = R.drawable.ic_location_24), tint = SocialTheme.colors.iconPrimary, contentDescription =null )
                                Spacer(modifier = Modifier.width(12.dp))

                                ClickableText(
                                    text = AnnotatedString("Shared location") ,
                                    style = TextStyle(
                                        fontFamily = Inter,
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 14.sp,
                                        color=SocialTheme.colors.textPrimary,
                                        textDecoration = TextDecoration.Underline,
                                    ),
                                    onClick = {
                                        onEvent(ChatItemEvent.OpenLocation(textMessage))
                                    }
                                )
                            }

                        }
                    }else if(text_type.equals("live")){
                        Box(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(painter = painterResource(id = R.drawable.ic_input), tint = SocialTheme.colors.iconPrimary, contentDescription =null )
                                Spacer(modifier = Modifier.width(12.dp))
                                ClickableText(
                                    text = AnnotatedString("Live activity shared") ,
                                    style = TextStyle(
                                        fontFamily = Inter,
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 14.sp,
                                        color=SocialTheme.colors.textPrimary,
                                        textDecoration = TextDecoration.Underline,
                                    ),
                                    onClick = {
                                        onEvent(ChatItemEvent.JoinLive(textMessage))
                                    }
                                )
                            }

                        }
                    }
                }
                Spacer(modifier = Modifier.width(24.dp))
            }
        }
    }
}
