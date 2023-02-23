package com.example.socialk.chat.ChatComponents

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.socialk.R
import com.example.socialk.chat.ChatEvent
import com.example.socialk.model.Activity
import com.example.socialk.model.Chat
import com.example.socialk.model.User
import com.example.socialk.model.UserData
import com.example.socialk.ui.theme.Inter
import com.example.socialk.ui.theme.SocialTheme

@Composable
fun ChatScreenTopBar(chat: Chat, onEvent: (ChatEvent) -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp), contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Spacer(modifier = Modifier.width(24.dp))
            IconButton(onClick = { onEvent(ChatEvent.GoBack) }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_back),
                    tint = SocialTheme.colors.textPrimary,
                    contentDescription = null
                )
            }

            Spacer(modifier = Modifier.width(12.dp))
            if(chat.type.equals("duo")){
                val image = if (chat.chat_picture != null) {
                    chat.chat_picture
                } else if (chat.user_one_username.equals(UserData.user!!.username)) {
                    if (chat.user_two_username != null) {
                        if (chat.user_two_profile_pic != null) {
                            chat.user_two_profile_pic
                        } else {
                            ""
                        }
                    } else {
                        ""
                    }
                } else if (chat.user_two_username.equals(UserData.user!!.username)) {
                    if (chat.user_one_username != null) {
                        if (chat.user_one_profile_pic != null) {
                            chat.user_one_profile_pic
                        } else {
                            ""
                        }
                    } else {
                        ""
                    }
                } else {
                    ""
                }
                Image(
                    painter = rememberAsyncImagePainter(image),
                    contentDescription = "profile image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = if (chat.chat_name != null) {
                    if (chat.chat_name!!.length > 20) {
                        chat.chat_name!!.substring(0, 20) + "..."
                    } else {
                        chat.chat_name!!
                    }

                } else if (chat.user_one_username.equals(UserData.user!!.username)) {
                    if (chat.user_two_username != null) {
                        chat.user_two_username!!
                    } else {
                        ""
                    }
                } else if (chat.user_two_username.equals(UserData.user!!.username)) {
                    if (chat.user_one_username != null) {
                        chat.user_one_username!!
                    } else {
                        ""
                    }
                } else {
                    ""
                },
                style = TextStyle(
                    fontFamily = Inter,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            )
            Spacer(modifier = Modifier.weight(1f))
            IconButton(onClick = { onEvent(ChatEvent.GoToChatUserSettings) }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_more),
                    tint = SocialTheme.colors.textPrimary,
                    contentDescription = null
                )
            }
            Spacer(modifier = Modifier.width(24.dp))
        }
    }
}

@Composable
fun ChatScreenTopBar(user: User, onEvent: (ChatEvent) -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp), contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Spacer(modifier = Modifier.width(24.dp))
            IconButton(onClick = { onEvent(ChatEvent.GoBack) }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_back),
                    tint = SocialTheme.colors.textPrimary,
                    contentDescription = null
                )
            }

            Spacer(modifier = Modifier.width(24.dp))

            Image(
                painter = rememberAsyncImagePainter(user.pictureUrl),
                contentDescription = "profile image", modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = user.username!!,
                style = TextStyle(
                    fontFamily = Inter,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
            )
            Spacer(modifier = Modifier.weight(1f))
            IconButton(onClick = { onEvent(ChatEvent.GoToChatUserSettings) }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_more),
                    tint = SocialTheme.colors.textPrimary,
                    contentDescription = null
                )
            }
            Spacer(modifier = Modifier.width(24.dp))
        }
    }
}


@Composable
fun ChatScreenTopBar(activity: Activity, onEvent: (ChatEvent) -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp), contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Spacer(modifier = Modifier.width(24.dp))
            IconButton(onClick = { onEvent(ChatEvent.GoBack) }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_back),
                    tint = SocialTheme.colors.textPrimary,
                    contentDescription = null
                )
            }

            Spacer(modifier = Modifier.width(24.dp))

            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = activity.title,
                style = TextStyle(
                    fontFamily = Inter,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
            )
            Spacer(modifier = Modifier.weight(1f))
            IconButton(onClick = { onEvent(ChatEvent.GoToChatUserSettings) }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_more),
                    tint = SocialTheme.colors.textPrimary,
                    contentDescription = null
                )
            }
            Spacer(modifier = Modifier.width(24.dp))
        }
    }
}
