package com.example.socialk.chat

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.socialk.R
import com.example.socialk.create.ActivityTextFieldState
import com.example.socialk.signinsignup.TextFieldState
import com.example.socialk.ui.theme.Inter
import com.example.socialk.ui.theme.SocialTheme

sealed class ChatEvent {
    object GoToProfile : ChatEvent()
    object LogOut : ChatEvent()
    object GoToSettings : ChatEvent()
    object GoToHome : ChatEvent()
    object GoBack : ChatEvent()
    object GoToChatUserSettings : ChatEvent()
}

@Composable
fun ChatScreen(onEvent: (ChatEvent) -> Unit) {
    Column(modifier = Modifier.fillMaxSize()) {
        ChatScreenTopBar(onEvent = onEvent)
        Divider()
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 12.dp),
            reverseLayout = true
        ) {
            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
            item {
                Spacer(modifier = Modifier.height(12.dp))
                ChatItemRight()
                Spacer(modifier = Modifier.height(12.dp))
            }
            item {
                Spacer(modifier = Modifier.height(6.dp))
                ChatItemLeft()
                Spacer(modifier = Modifier.height(6.dp))
            }
            item {
                Spacer(modifier = Modifier.height(6.dp))
                ChatItemLeft()
                Spacer(modifier = Modifier.height(6.dp))
            }
            item {
                Spacer(modifier = Modifier.height(6.dp))
                ChatItemLeft()
                Spacer(modifier = Modifier.height(6.dp))
            }
            item {
                Spacer(modifier = Modifier.height(6.dp))
                ChatItemLeft()
                Spacer(modifier = Modifier.height(6.dp))
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
        Divider()
        ChatScreenBottomInputs()
    }
}


@Composable
fun ChatItemRight() {
    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
        Card(
            shape = RoundedCornerShape(8.dp),
            backgroundColor = Color(0xff0F0F30),
            elevation = 0.dp
        ) {
            Box(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                Text(
                    text = "WHERar eyuw hoidn",
                    style = TextStyle(
                        fontFamily = Inter,
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp
                    ),
                    color = SocialTheme.colors.textSecondary
                )
            }
        }
    }
}

@Composable
fun ChatItemLeft() {
    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterStart) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = rememberAsyncImagePainter("https://firebasestorage.googleapis.com/v0/b/socialv2-340711.appspot.com/o/uploads%2F1662065348037.null?alt=media&token=40cebce4-0c53-470c-867f-d9d34cba63ab"),
                contentDescription = "profile image", modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Card(
                Modifier.weight(1f),
                shape = RoundedCornerShape(8.dp),
                backgroundColor = SocialTheme.colors.uiBackground,
                border = BorderStroke(1.dp, color = SocialTheme.colors.uiFloated),
                elevation = 0.dp
            ) {
                Box(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                    Text(
                        text = "WHERareyuwhohfasdjkfhsdlfkjasdhfskdjfhlsdkfjhsdl",
                        style = TextStyle(
                            fontFamily = Inter,
                            fontWeight = FontWeight.Normal,
                            fontSize = 14.sp
                        ),
                        color = SocialTheme.colors.textPrimary
                    )
                }
            }
            Spacer(modifier = Modifier.width(24.dp))
        }

    }
}


@Composable
fun Divider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(color = SocialTheme.colors.uiFloated)
    )
}

@Composable
fun ChatScreenTopBar(onEvent: (ChatEvent) -> Unit) {
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
                painter = rememberAsyncImagePainter("https://firebasestorage.googleapis.com/v0/b/socialv2-340711.appspot.com/o/uploads%2F1662065348037.null?alt=media&token=40cebce4-0c53-470c-867f-d9d34cba63ab"),
                contentDescription = "profile image", modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "Adam",
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

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ChatButton(onEvent: () -> Unit, icon: Int) {
    Card(
        modifier = Modifier.size(48.dp),
        shape = RoundedCornerShape(12.dp),
        backgroundColor = SocialTheme.colors.uiBackground,
        onClick = onEvent,
        elevation = 0.dp,
        border = BorderStroke(1.dp, color = SocialTheme.colors.uiFloated)
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = null,
                tint = SocialTheme.colors.iconPrimary
            )
        }
    }
}


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SendButton(onEvent: () -> Unit, icon: Int) {
    Card(
        modifier = Modifier.size(48.dp),
        shape = RoundedCornerShape(12.dp),
        backgroundColor = Color(0xff0F0F30),
        onClick = onEvent,
        elevation = 0.dp
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = null,
                tint = SocialTheme.colors.textSecondary
            )
        }
    }
}


@Composable
fun ChatScreenBottomInputs(textState: TextFieldState = remember { ActivityTextFieldState() }) {
    var chatTextFieldFocused by remember { mutableStateOf(false) }
    var textSendAvailable by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
    ) {
        Row(modifier = Modifier.fillMaxSize(), verticalAlignment = Alignment.CenterVertically) {

            if (!chatTextFieldFocused) {

                Spacer(modifier = Modifier.width(12.dp))
                ChatButton(onEvent = {}, R.drawable.ic_pin_drop)
                Spacer(modifier = Modifier.width(6.dp))
                ChatButton(onEvent = {}, R.drawable.ic_photo_library)
                Spacer(modifier = Modifier.width(6.dp))
                ChatButton(onEvent = {}, R.drawable.ic_highlight)
            } else {
                Spacer(modifier = Modifier.width(6.dp))
                ChatButton(onEvent = { chatTextFieldFocused = false }, R.drawable.ic_right_close)
            }
            Spacer(modifier = Modifier.width(6.dp))

            Card(
                modifier = Modifier
                    .height(48.dp)
                    .weight(1f),
                border = BorderStroke(1.dp, color = SocialTheme.colors.uiFloated),
                shape = RoundedCornerShape(8.dp),
                backgroundColor = SocialTheme.colors.uiBackground,
                elevation = 0.dp
            ) {
                Box(
                    modifier = Modifier.padding(horizontal = 12.dp),
                    contentAlignment = Alignment.CenterStart
                ) {

                    BasicTextField(modifier = Modifier
                        .onFocusChanged {
                            if (it.isFocused) {
                                chatTextFieldFocused = true
                            }
                        }, decorationBox = { innerTextField ->
                        if (textState.text.isEmpty()) {
                            Text(
                                text = "Aa",
                                color = SocialTheme.colors.iconPrimary,
                                style = TextStyle(
                                    fontSize = 16.sp,
                                    fontFamily = Inter,
                                    fontWeight = FontWeight.Normal
                                )
                            )
                        }
                        innerTextField()
                    }, singleLine = true,
                        maxLines = 1, value = textState.text, onValueChange = {

                            textState.text = it
                            if (textState.text.length > 0) {
                                chatTextFieldFocused = true
                                textSendAvailable = true
                            }
                            if (textState.text.length == 0) {
                                chatTextFieldFocused = false
                                textSendAvailable = false
                            }
                        },
                        textStyle = TextStyle(
                            fontSize = 16.sp,
                            fontFamily = Inter,
                            fontWeight = FontWeight.Normal
                        )
                    )
                }

            }
            if (!chatTextFieldFocused) {
                if (!textSendAvailable) {
                    Spacer(modifier = Modifier.width(6.dp))
                    ChatButton(onEvent = {}, R.drawable.ic_person_waving)
                } else {
                    Spacer(modifier = Modifier.width(6.dp))
                    SendButton(onEvent = {}, R.drawable.ic_send)
                }
            } else {
                Spacer(modifier = Modifier.width(6.dp))
                SendButton(onEvent = {}, R.drawable.ic_send)
            }
            Spacer(modifier = Modifier.width(12.dp))

        }
    }

}