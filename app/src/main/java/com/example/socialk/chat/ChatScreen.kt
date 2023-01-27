package com.example.socialk.chat

import android.content.ClipData
import android.service.autofill.OnClickAction
import android.util.Log
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.End
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.socialk.R
import com.example.socialk.components.BottomSheetLayout
import com.example.socialk.create.ActivityTextFieldState
import com.example.socialk.di.ChatViewModel
import com.example.socialk.model.*
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
    class SendMessage(message: String) : ChatEvent() {
        val message = message
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ChatScreen(
    chat: Chat,
    chatViewModel: ChatViewModel,
    onEvent: (ChatEvent) -> Unit,
    textState: TextFieldState = remember { ActivityTextFieldState() }
) {  val keyboardController = LocalSoftwareKeyboardController.current
    Column(modifier = Modifier.fillMaxSize()) {
        ChatScreenTopBar(chat, onEvent = onEvent)
        Divider()
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 12.dp),
            reverseLayout = true
        ) {

        }
        Divider()
        ChatScreenBottomInputs(keyboardController,onEvent = {}, textState)
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ChatScreenBottomInputs(keyboardController: SoftwareKeyboardController?,onEvent: () -> Unit, textState: TextFieldState) {
    var chatTextFieldFocused by remember { mutableStateOf(false) }
    var textSendAvailable by remember { mutableStateOf(false) }
    Box(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {

            if (!chatTextFieldFocused) {

                Spacer(modifier = Modifier.width(12.dp))
                ChatButton(onEvent = {}, R.drawable.ic_pin_drop)
                Spacer(modifier = Modifier.width(6.dp))
                ChatButton(onEvent = {}, R.drawable.ic_photo_library)
                Spacer(modifier = Modifier.width(6.dp))
                ChatButton(onEvent = {}, R.drawable.ic_highlight)
            } else {
                Spacer(modifier = Modifier.width(6.dp))
                ChatButton(onEvent = { chatTextFieldFocused = false
                                     keyboardController?.hide()}, R.drawable.ic_right_close)
            }
            Spacer(modifier = Modifier.width(6.dp))

            Card(
                modifier = Modifier
                    .weight(1f)
                    .heightIn(48.dp, 128.dp),
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
                        .fillMaxWidth()
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
                                    fontSize = 18.sp,
                                    fontFamily = Inter,
                                    fontWeight = FontWeight.Normal
                                )
                            )
                        }
                        innerTextField()
                    },
                        maxLines = 5, value = textState.text, onValueChange = {

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
                    SendButton(onEvent = onEvent, icon=R.drawable.ic_send,available=textSendAvailable)

                }
            } else {
                Spacer(modifier = Modifier.width(6.dp))
                SendButton(onEvent =    onEvent, icon=R.drawable.ic_send,available=textSendAvailable)
            }
            Spacer(modifier = Modifier.width(12.dp))


        }
    }
}

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

            Spacer(modifier = Modifier.width(24.dp))

            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = chat.chat_name!!,
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
fun ChatItemRight(date:String,textMessage: String,onLongPress:()->Unit) {
    var itemClickedState by remember {
        mutableStateOf(false)
    }
    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
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
            Card(modifier = Modifier.align(End),
                shape = RoundedCornerShape(8.dp),
                backgroundColor = Color(0xff0F0F30),
                elevation = 0.dp,
                onClick =  {
                    (!itemClickedState).also { itemClickedState = it }
                }
            ) {
                Box(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                    Text(
                        text = textMessage,
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
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ChatItemLeft(date:String,textMessage: String,onLongPress:()->Unit) {
    var itemClickedState by remember {
        mutableStateOf(false)
    }
    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterStart) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            if(itemClickedState){
                Text(text =date ,color=SocialTheme.colors.iconPrimary,
                    style = TextStyle(fontSize = 10.sp, fontFamily = Inter, fontWeight = FontWeight.ExtraLight,))
            }else{

            }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = rememberAsyncImagePainter("https://firebasestorage.googleapis.com/v0/b/socialv2-340711.appspot.com/o/uploads%2F1662065348037.null?alt=media&token=40cebce4-0c53-470c-867f-d9d34cba63ab"),
                contentDescription = "profile image", modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
            )
            Spacer(modifier = Modifier.width(6.dp))

                Card(
                    shape = RoundedCornerShape(8.dp),
                    backgroundColor = SocialTheme.colors.uiBackground,
                    border = BorderStroke(1.dp, color = SocialTheme.colors.uiFloated),
                    elevation = 0.dp, onClick = {
                        (!itemClickedState).also { itemClickedState = it }
                    }
                ) {
                    Box(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                        Text(
                            text = textMessage,
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


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ChatScreen(
    user: User,
    chatViewModel: ChatViewModel,
    onEvent: (ChatEvent) -> Unit,
    textState: TextFieldState = remember { ActivityTextFieldState() }
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val data = remember { mutableStateOf(ArrayList<ChatMessage>()) }
    val added_data_state = remember { mutableStateOf(false) }
    var messageOptionsVisibility by remember { mutableStateOf(false) }
    Column(modifier = Modifier.fillMaxSize()) {
        ChatScreenTopBar(user, onEvent = onEvent)
        Divider()



        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 12.dp)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    keyboardController?.hide()
                },
            reverseLayout = true
        ) {

            items(data.value) {

                if (it.sender_id==UserData.user!!.id){
                    Spacer(modifier = Modifier.height(4.dp))
                    ChatItemRight(textMessage =it.text, date=it.sent_time, onLongPress = {messageOptionsVisibility=true})
                    Spacer(modifier = Modifier.height(4.dp))
                }else{
                    Spacer(modifier = Modifier.height(8.dp))
                    ChatItemLeft(textMessage =it.text,date=it.sent_time, onLongPress = {messageOptionsVisibility=true})
                    Spacer(modifier = Modifier.height(8.dp))

                }

            }

        }
        Divider()
        if (messageOptionsVisibility){
            BottomSheetLayout()

        }else{
            ChatScreenBottomInputs(keyboardController,onEvent = {
                if (textState.text.length>0){
                    onEvent(ChatEvent.SendMessage(message = textState.text.trim()))

                }
                textState.text = ""
            }, textState)
        }


    }

    chatViewModel.messagesState.value.let {
        when (it) {
            is Response.Success -> {
                data.value =it.data
            }
            is Response.Loading -> {}
            is Response.Failure -> {}
        }
    }
}


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SendButton(onEvent: () -> Unit, icon: Int,available:Boolean) {
    Card(
        modifier = Modifier.size(48.dp),
        shape = RoundedCornerShape(12.dp),
        backgroundColor =if (available){Color(0xff0F0F30)}else{SocialTheme.colors.iconPrimary},
        onClick = onEvent
        ,
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
fun Divider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(color = SocialTheme.colors.uiFloated)
    )
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



