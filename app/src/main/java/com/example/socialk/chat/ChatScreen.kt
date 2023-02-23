package com.example.socialk.chat

import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Alignment.Companion.End
import androidx.compose.ui.Alignment.Companion.TopCenter
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.socialk.R
import com.example.socialk.components.SocialDialog
import com.example.socialk.create.ActivityTextFieldState
import com.example.socialk.di.ChatViewModel
import com.example.socialk.map.MapEvent
import com.example.socialk.model.*
import com.example.socialk.signinsignup.TextFieldState
import com.example.socialk.ui.theme.Inter
import com.example.socialk.ui.theme.SocialTheme
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.distinctUntilChanged

sealed class ChatEvent {
    object GoToProfile : ChatEvent()
    object LogOut : ChatEvent()
    object OpenLocationDialog : ChatEvent()
    object OpenGallery : ChatEvent()
    object GoToSettings : ChatEvent()
    object GoToHome : ChatEvent()
    object GoBack : ChatEvent()
    object GoToChatUserSettings : ChatEvent()
    object Highlight : ChatEvent()
    object AskForPermission : ChatEvent()
    class SendMessage(message: String) : ChatEvent() {
        val message = message
    }
    class SendImage(message: Uri) : ChatEvent() {
        val message = message
    }
}

@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterialApi::class)
@Composable
fun ChatScreen(viewModel:ChatCollectionViewModel,
    chat: Chat,
    chatViewModel: ChatViewModel,
    onEvent: (ChatEvent) -> Unit,
    textState: TextFieldState = remember { ActivityTextFieldState() }
) {




    val permission_flow = viewModel.granted_permission.collectAsState()
    val location_flow = viewModel.location.collectAsState()




    val isImageAddedToStorage by  chatViewModel.isImageAddedToStorageFlow.collectAsState()
    val openDialog = remember { mutableStateOf(false) }
    val openLocationDialog = remember { mutableStateOf(false) }

    val hasExecutedMoreMessages = remember { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current
    val data = remember {
        mutableStateOf(ArrayList<ChatMessage>())
    }
    val data_new = remember {
        mutableStateOf(ArrayList<ChatMessage>())
    }
    val frist_data = remember {


        mutableStateOf(ArrayList<ChatMessage>())
    }
    val valueExist = remember { mutableStateOf(false) }
    val added_data_state = remember { mutableStateOf(false) }
    var messageOptionsVisibility by remember { mutableStateOf(false) }
    var highlite_message by remember { mutableStateOf(false) }
    var highlited_message_text by remember { mutableStateOf("") }
    var highlight_dialog by remember { mutableStateOf(false) }

    var uri by remember { mutableStateOf<Uri?>(null) }
    val uriReceived by chatViewModel.uriReceived
    chatViewModel.uri.observe(LocalLifecycleOwner.current) { newUri ->
        Log.d("ImageFromGallery", "image passed" + uri.toString())
        uri = newUri
    }
    Log.d("ImageFromGallery", "image passed2" + uri.toString() + uriReceived.toString())
    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = CenterHorizontally) {
        ChatScreenTopBar(chat, onEvent = onEvent)
        Divider()
        Box(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 12.dp)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        keyboardController?.hide()
                    },
                reverseLayout = true
            ) {
                items(data.value!!) {
                    if (it.sender_id == UserData.user!!.id) {
                        if(it.message_type.equals("uri")){}
                        Spacer(modifier = Modifier.height(4.dp))
                        ChatItemRight(text_type=it.message_type,
                            textMessage = it.text,
                            date = it.sent_time,
                            onLongPress = { messageOptionsVisibility = true }, onClick = {
                                if (highlite_message) {
                                    openDialog.value = true
                                    highlited_message_text = it.text
                                }
                            })
                        Spacer(modifier = Modifier.height(4.dp))
                    } else {
                        Spacer(modifier = Modifier.height(8.dp))
                        ChatItemLeft(text_type=it.message_type,
                            textMessage = it.text,
                            date = it.sent_time,
                            onLongPress = { messageOptionsVisibility = true },
                            picture_url = it.sender_picture_url, onClick = {
                                if (highlite_message) {
                                    openDialog.value = true
                                }
                            })
                        Spacer(modifier = Modifier.height(8.dp))

                    }

                }
                items(frist_data.value!!) {
                    if (it.sender_id == UserData.user!!.id) {
                        if(it.message_type.equals("uri")){}
                        Spacer(modifier = Modifier.height(4.dp))
                        ChatItemRight(text_type=it.message_type,
                            textMessage = it.text,
                            date = it.sent_time,
                            onLongPress = { messageOptionsVisibility = true }, onClick = {
                                if (highlite_message) {
                                    openDialog.value = true
                                    highlited_message_text = it.text
                                }
                            })
                        Spacer(modifier = Modifier.height(4.dp))
                    } else {
                        Spacer(modifier = Modifier.height(8.dp))
                        ChatItemLeft(text_type=it.message_type,
                            textMessage = it.text,
                            date = it.sent_time,
                            onLongPress = { messageOptionsVisibility = true },
                            picture_url = it.sender_picture_url, onClick = {
                                if (highlite_message) {
                                    openDialog.value = true
                                }
                            })
                        Spacer(modifier = Modifier.height(8.dp))

                    }

                }
                items(data_new.value!!) {
                    if (it.sender_id == UserData.user!!.id) {
                        if(it.message_type.equals("uri")){}
                        Spacer(modifier = Modifier.height(4.dp))
                        ChatItemRight(text_type=it.message_type,
                            textMessage = it.text,
                            date = it.sent_time,
                            onLongPress = { messageOptionsVisibility = true }, onClick = {
                                if (highlite_message) {
                                    openDialog.value = true
                                    highlited_message_text = it.text
                                }
                            })
                        Spacer(modifier = Modifier.height(4.dp))
                    } else {
                        Spacer(modifier = Modifier.height(8.dp))
                        ChatItemLeft(text_type=it.message_type,
                            textMessage = it.text,
                            date = it.sent_time,
                            onLongPress = { messageOptionsVisibility = true },
                            picture_url = it.sender_picture_url, onClick = {
                                if (highlite_message) {
                                    openDialog.value = true
                                }
                            })
                        Spacer(modifier = Modifier.height(8.dp))

                    }

                }
                item{
                    if(valueExist.value){
                        LaunchedEffect(true ){
                            chatViewModel.getMoreMessages(chat.id!!)
                        }

                    }

                }
            }

            if (chat.highlited_message != null) {
                if (chat.highlited_message!!.isNotEmpty()) {
                    highlight_dialog = true
                }
            }
            if (highlight_dialog) {
                Box(
                    modifier = Modifier
                        .padding(horizontal = 24.dp)
                        .padding(top = 24.dp)
                        .align(TopCenter)
                ) {
                    Card(shape = RoundedCornerShape(12.dp), elevation = 4.dp, onClick = {
                        chat.highlited_message = null
                        highlight_dialog = false
                    }) {
                        Box(
                            modifier = Modifier
                                .background(color = SocialTheme.colors.uiBackground)
                                .padding(12.dp)
                        ) {
                            Text(
                                text = chat.highlited_message.toString(),
                                color = SocialTheme.colors.textPrimary,
                                style = TextStyle(
                                    fontFamily = Inter,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Normal
                                )
                            )
                        }
                    }
                }
            }
            if (uriReceived && uri != null) {
                Dialog(onDismissRequest = { chatViewModel.onUriProcessed() }) {
                    androidx.compose.material3.Card(shape = RoundedCornerShape(16.dp)) {
                        Box(modifier = Modifier.padding(24.dp)) {
                            Column(horizontalAlignment = CenterHorizontally) {
                                Image(
                                    painter = rememberAsyncImagePainter(uri),
                                    contentDescription = "image from gallery",
                                    modifier = Modifier
                                        .size(300.dp)
                                )
                                Spacer(modifier = Modifier.height(24.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End,
                                    verticalAlignment = CenterVertically
                                ) {
                                    ClickableText(
                                        text = AnnotatedString("Dismiss"), style = TextStyle(
                                            color = SocialTheme.colors.textPrimary,
                                            fontFamily = Inter,
                                            fontWeight = FontWeight.Medium,
                                            fontSize = 14.sp
                                        ), onClick = {chatViewModel.onUriProcessed() }
                                    )
                                    Spacer(modifier = Modifier.width(24.dp))
                                    Card(shape=RoundedCornerShape(16.dp)){

                                        Box(modifier = Modifier
                                            .background(color = Color(0xff0F0F30))
                                            .padding(12.dp))
                                        {  Row() {
                                            Icon(painter = painterResource(id = R.drawable.ic_send), contentDescription =null, tint = Color.White )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            ClickableText(
                                                text = AnnotatedString("Send"), style = TextStyle(
                                                    color = SocialTheme.colors.textSecondary,
                                                    fontFamily = Inter,
                                                    fontWeight = FontWeight.Medium,
                                                    fontSize = 14.sp
                                                ), onClick = { onEvent(ChatEvent.SendImage(uri!!))
                                                    chatViewModel.onUriProcessed()}
                                            )
                                        }

                                        }
                                    }

                                }
                            }


                        }
                    }
                }


            }
        }



        Divider()
        if (openDialog.value) {
            SocialDialog(
                onDismiss = {
                    openDialog.value = false
                    highlited_message_text = ""
                },
                onConfirm = {
                    chatViewModel.addHighLight(chat.id!!, highlited_message_text)
                    chat.highlited_message = highlited_message_text
                    openDialog.value = false
                },
                onCancel = {
                    openDialog.value = false
                    highlited_message_text = ""
                },
                title = "Highlight message?",
                info = highlited_message_text,
                icon = R.drawable.ic_highlight,
                actionButtonText = "Confirm"
            )
        }


        if (openLocationDialog.value) {
            permission_flow.value.let {
                if (it) {
                    SocialDialog(
                        onDismiss = {
                            openLocationDialog.value = false
                        },
                        onConfirm = {
                            openLocationDialog.value = false
                        },
                        onCancel = {
                            openLocationDialog.value = false
                        },
                        title = "Share current location?",
                        info = "Chat users will be able to see your location on map",
                        icon = R.drawable.ic_location_24,
                        actionButtonText = "Share"
                    )
                }else{
                    SocialDialog(
                        onDismiss = {  openLocationDialog.value = false },
                        onConfirm = { openLocationDialog.value = false
                            onEvent(ChatEvent.AskForPermission) },
                        onCancel = {  openLocationDialog.value = false },
                        title = "Location access",
                        info = "To access the map please share your location",
                        icon = R.drawable.ic_location_24,
                        actionButtonText = "Share",
                        actionButtonTextColor = SocialTheme.colors.textInteractive
                    )
                }
                }
            }



        ChatScreenBottomInputs(modifier = Modifier, keyboardController, onEvent = {
            when (it) {
                is ChatEvent.SendMessage -> {
                    if (textState.text.length > 0) {
                        onEvent(ChatEvent.SendMessage(message = textState.text.trim()))
                        textState.text = ""
                    }
                }
                is ChatEvent.Highlight -> {
                    highlite_message = !highlite_message
                }
                is ChatEvent.OpenLocationDialog -> {
                    openLocationDialog.value = true
                }
                is ChatEvent.OpenGallery -> {
                    onEvent(ChatEvent.OpenGallery)
                }
            }


        }, textState, highlite_message)


    }
    isImageAddedToStorage.let {
            response ->
        Log.d("ImagePicker", response.toString())
        when(response){
            is Response.Success->{}
            is Response.Loading->{}
            is Response.Failure->{
                Log.d("ImagePicker", "failure")
                Toast.makeText(LocalContext.current,"Failed to send the image",Toast.LENGTH_SHORT).show()
            }
        }
    }
    chatViewModel.firstMessagesState.value.let {
        when (it) {
            is Response.Success -> {
                frist_data.value=it.data
                valueExist.value=true
            }
            is Response.Loading -> {}
            is Response.Failure -> {}
        }
    }
    chatViewModel.messagesState.value.let {
        when (it) {
            is Response.Success -> {
                data.value=it.data
            }
            is Response.Loading -> {}
            is Response.Failure -> {}
        }
    }
    chatViewModel.moreMessagesState.value.let {
        when (it) {
            is Response.Success -> {
                Log.d("ACTTTTTTT","State"+it.data.size)
                data_new.value=it.data
            }
            is Response.Loading -> {}
            is Response.Failure -> {}
        }
    }
}


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ChatScreenBottomInputs(
    modifier: Modifier? = Modifier,
    keyboardController: SoftwareKeyboardController?,
    onEvent: (ChatEvent) -> Unit, textState: TextFieldState, highlight: Boolean
) {
    var chatTextFieldFocused by remember { mutableStateOf(false) }
    var textSendAvailable by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp), verticalAlignment = Alignment.CenterVertically
        ) {

            if (!chatTextFieldFocused) {

                Spacer(modifier = Modifier.width(12.dp))
                ChatButton(onEvent = {onEvent(ChatEvent.OpenLocationDialog)}, R.drawable.ic_pin_drop)
                Spacer(modifier = Modifier.width(6.dp))
                ChatButton(
                    onEvent = { onEvent(ChatEvent.OpenGallery) },
                    R.drawable.ic_photo_library
                )
                Spacer(modifier = Modifier.width(6.dp))
                ChatButton(onEvent = {
                    onEvent(ChatEvent.Highlight)

                }, R.drawable.ic_highlight, selected = highlight)
            } else {
                Spacer(modifier = Modifier.width(6.dp))
                ChatButton(onEvent = {
                    chatTextFieldFocused = false
                    keyboardController?.hide()
                }, R.drawable.ic_right_close)
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
                    SendButton(
                        onEvent = { onEvent(ChatEvent.SendMessage(textState.text)) },
                        icon = R.drawable.ic_send,
                        available = textSendAvailable
                    )

                }
            } else {
                Spacer(modifier = Modifier.width(6.dp))
                SendButton(
                    onEvent = { onEvent(ChatEvent.SendMessage(textState.text)) },
                    icon = R.drawable.ic_send,
                    available = textSendAvailable
                )
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

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ChatItemRight(text_type:String, date: String, textMessage: String, onLongPress: () -> Unit, onClick: () -> Unit) {
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
                onClick = {
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
                    }else{
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
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ChatItemLeft(text_type:String,
    date: String,
    textMessage: String,
    onLongPress: () -> Unit,
    picture_url: String,
    onClick: () -> Unit
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
                Image(
                    painter = rememberAsyncImagePainter(picture_url),
                    contentScale = ContentScale.Crop,
                    contentDescription = "profile image",
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                )
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
                    }else{
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
                    }
                }
                Spacer(modifier = Modifier.width(24.dp))
            }
        }

    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ChatButton(
    onEvent: () -> Unit,
    icon: Int,
    iconTint: Color = SocialTheme.colors.iconPrimary,
    selected: Boolean = false
) {

    Card(
        modifier = Modifier.size(48.dp),
        shape = RoundedCornerShape(12.dp),
        backgroundColor = if (selected) {
            SocialTheme.colors.textInteractive
        } else {
            SocialTheme.colors.uiBackground
        },
        onClick = onEvent,
        elevation = 0.dp,
        border = BorderStroke(1.dp, color = SocialTheme.colors.uiFloated)
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = null,
                tint = if (selected) {
                    Color.White
                } else {
                    iconTint
                }
            )
        }
    }
}
fun LazyListState.isScrolledToEnd() = layoutInfo.visibleItemsInfo.lastOrNull()?.index == layoutInfo.totalItemsCount - 1


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ChatScreen(viewModel:ChatCollectionViewModel,chat_id:String,
    user: User,
    chatViewModel: ChatViewModel,
    onEvent: (ChatEvent) -> Unit,
    textState: TextFieldState = remember { ActivityTextFieldState() }
) {
    val permission_flow = viewModel.granted_permission.collectAsState()
    val location_flow = viewModel.location.collectAsState()

    val scrollState = rememberLazyListState()
    val openDialog = remember { mutableStateOf(false) }
    var highlite_message by remember { mutableStateOf(false) }
    var highlited_message_text by remember { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current
    val data = remember {
        mutableStateOf(ArrayList<ChatMessage>())
    }
    val data_new = remember {
        mutableStateOf(ArrayList<ChatMessage>())
    }
    val frist_data = remember {


        mutableStateOf(ArrayList<ChatMessage>())
    }
    val valueExist = remember { mutableStateOf(false)}
        val more_data = remember { mutableStateOf(ArrayList<ChatMessage>()) }
    val added_data_state = remember { mutableStateOf(false) }
    var messageOptionsVisibility by remember { mutableStateOf(false) }
    Column(modifier = Modifier.fillMaxSize()) {
        ChatScreenTopBar(user, onEvent = onEvent)
        Divider()

        LazyColumn(  state = scrollState,
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

            items(data.value!!) {
                if (it.sender_id == UserData.user!!.id) {
                    if(it.message_type.equals("uri")){}
                    Spacer(modifier = Modifier.height(4.dp))
                    ChatItemRight(text_type=it.message_type,
                        textMessage = it.text,
                        date = it.sent_time,
                        onLongPress = { messageOptionsVisibility = true }, onClick = {
                            if (highlite_message) {
                                openDialog.value = true
                                highlited_message_text = it.text
                            }
                        })
                    Spacer(modifier = Modifier.height(4.dp))
                } else {
                    Spacer(modifier = Modifier.height(8.dp))
                    ChatItemLeft(text_type=it.message_type,
                        textMessage = it.text,
                        date = it.sent_time,
                        onLongPress = { messageOptionsVisibility = true },
                        picture_url = it.sender_picture_url, onClick = {
                            if (highlite_message) {
                                openDialog.value = true
                            }
                        })
                    Spacer(modifier = Modifier.height(8.dp))

                }

            }
            items(frist_data.value!!) {
                if (it.sender_id == UserData.user!!.id) {
                    if(it.message_type.equals("uri")){}
                    Spacer(modifier = Modifier.height(4.dp))
                    ChatItemRight(text_type=it.message_type,
                        textMessage = it.text,
                        date = it.sent_time,
                        onLongPress = { messageOptionsVisibility = true }, onClick = {
                            if (highlite_message) {
                                openDialog.value = true
                                highlited_message_text = it.text
                            }
                        })
                    Spacer(modifier = Modifier.height(4.dp))
                } else {
                    Spacer(modifier = Modifier.height(8.dp))
                    ChatItemLeft(text_type=it.message_type,
                        textMessage = it.text,
                        date = it.sent_time,
                        onLongPress = { messageOptionsVisibility = true },
                        picture_url = it.sender_picture_url, onClick = {
                            if (highlite_message) {
                                openDialog.value = true
                            }
                        })
                    Spacer(modifier = Modifier.height(8.dp))

                }

            }
            items(data_new.value!!) {
                if (it.sender_id == UserData.user!!.id) {
                    if(it.message_type.equals("uri")){}
                    Spacer(modifier = Modifier.height(4.dp))
                    ChatItemRight(text_type=it.message_type,
                        textMessage = it.text,
                        date = it.sent_time,
                        onLongPress = { messageOptionsVisibility = true }, onClick = {
                            if (highlite_message) {
                                openDialog.value = true
                                highlited_message_text = it.text
                            }
                        })
                    Spacer(modifier = Modifier.height(4.dp))
                } else {
                    Spacer(modifier = Modifier.height(8.dp))
                    ChatItemLeft(text_type=it.message_type,
                        textMessage = it.text,
                        date = it.sent_time,
                        onLongPress = { messageOptionsVisibility = true },
                        picture_url = it.sender_picture_url, onClick = {
                            if (highlite_message) {
                                openDialog.value = true
                            }
                        })
                    Spacer(modifier = Modifier.height(8.dp))

                }

            }
            item{
                if(valueExist.value){
                    LaunchedEffect(true ){
                        chatViewModel.getMoreMessages(chat_id)
                    }

                }

            }
        }
        // observer when reached end of list
        val endOfListReached by remember {
            derivedStateOf {
                scrollState.isScrolledToEnd()
            }
        }


        Divider()

        if (openDialog.value) {
            SocialDialog(
                onDismiss = {
                    openDialog.value = false
                    highlited_message_text = ""
                },
                onConfirm = {
                },
                onCancel = {
                    openDialog.value = false
                    highlited_message_text = ""
                },
                title = "Highlight message?",
                info = highlited_message_text,
                icon = R.drawable.ic_highlight,
                actionButtonText = "Confirm"
            )
        }

        ChatScreenBottomInputs(modifier = Modifier, keyboardController, onEvent = {
            if (textState.text.length > 0) {
                onEvent(ChatEvent.SendMessage(message = textState.text.trim()))

            }
            textState.text = ""
        }, textState, highlite_message)


    }

        chatViewModel.firstMessagesState.value.let {
            when (it) {
                is Response.Success -> {
                    frist_data.value=it.data
                    valueExist.value=true
                }
                is Response.Loading -> {}
                is Response.Failure -> {}
            }
        }
        chatViewModel.messagesState.value.let {
            when (it) {
                is Response.Success -> {
                    data.value=it.data
                }
                is Response.Loading -> {}
                is Response.Failure -> {}
            }
        }
        chatViewModel.moreMessagesState.value.let {
            when (it) {
                is Response.Success -> {
                    Log.d("ACTTTTTTT","State"+it.data.size)
                    data_new.value=it.data
                }
                is Response.Loading -> {}
                is Response.Failure -> {}
            }
        }
}


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ChatScreen(viewModel:ChatCollectionViewModel,chat_id: String,
    activity: Activity,
    chatViewModel: ChatViewModel,
    onEvent: (ChatEvent) -> Unit,
    textState: TextFieldState = remember { ActivityTextFieldState() }
) {   val isImageAddedToStorage by  chatViewModel.isImageAddedToStorageFlow.collectAsState()
    val openDialog = remember { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current
    val data = remember {
        mutableStateOf(ArrayList<ChatMessage>())
    }
    val data_new = remember {
        mutableStateOf(ArrayList<ChatMessage>())
    }
    val frist_data = remember {


        mutableStateOf(ArrayList<ChatMessage>())
    }
    val valueExist = remember { mutableStateOf(false)}
        val added_data_state = remember { mutableStateOf(false) }
    var messageOptionsVisibility by remember { mutableStateOf(false) }
    var highlite_message by remember { mutableStateOf(false) }
    var highlited_message_text by remember { mutableStateOf("") }
    Column(modifier = Modifier.fillMaxSize()) {
        ChatScreenTopBar(activity, onEvent = onEvent)
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

            items(data.value!!) {
                if (it.sender_id == UserData.user!!.id) {
                    if(it.message_type.equals("uri")){}
                    Spacer(modifier = Modifier.height(4.dp))
                    ChatItemRight(text_type=it.message_type,
                        textMessage = it.text,
                        date = it.sent_time,
                        onLongPress = { messageOptionsVisibility = true }, onClick = {
                            if (highlite_message) {
                                openDialog.value = true
                                highlited_message_text = it.text
                            }
                        })
                    Spacer(modifier = Modifier.height(4.dp))
                } else {
                    Spacer(modifier = Modifier.height(8.dp))
                    ChatItemLeft(text_type=it.message_type,
                        textMessage = it.text,
                        date = it.sent_time,
                        onLongPress = { messageOptionsVisibility = true },
                        picture_url = it.sender_picture_url, onClick = {
                            if (highlite_message) {
                                openDialog.value = true
                            }
                        })
                    Spacer(modifier = Modifier.height(8.dp))

                }

            }
            items(frist_data.value!!) {
                if (it.sender_id == UserData.user!!.id) {
                    if(it.message_type.equals("uri")){}
                    Spacer(modifier = Modifier.height(4.dp))
                    ChatItemRight(text_type=it.message_type,
                        textMessage = it.text,
                        date = it.sent_time,
                        onLongPress = { messageOptionsVisibility = true }, onClick = {
                            if (highlite_message) {
                                openDialog.value = true
                                highlited_message_text = it.text
                            }
                        })
                    Spacer(modifier = Modifier.height(4.dp))
                } else {
                    Spacer(modifier = Modifier.height(8.dp))
                    ChatItemLeft(text_type=it.message_type,
                        textMessage = it.text,
                        date = it.sent_time,
                        onLongPress = { messageOptionsVisibility = true },
                        picture_url = it.sender_picture_url, onClick = {
                            if (highlite_message) {
                                openDialog.value = true
                            }
                        })
                    Spacer(modifier = Modifier.height(8.dp))

                }

            }
            items(data_new.value!!) {
                if (it.sender_id == UserData.user!!.id) {
                    if(it.message_type.equals("uri")){}
                    Spacer(modifier = Modifier.height(4.dp))
                    ChatItemRight(text_type=it.message_type,
                        textMessage = it.text,
                        date = it.sent_time,
                        onLongPress = { messageOptionsVisibility = true }, onClick = {
                            if (highlite_message) {
                                openDialog.value = true
                                highlited_message_text = it.text
                            }
                        })
                    Spacer(modifier = Modifier.height(4.dp))
                } else {
                    Spacer(modifier = Modifier.height(8.dp))
                    ChatItemLeft(text_type=it.message_type,
                        textMessage = it.text,
                        date = it.sent_time,
                        onLongPress = { messageOptionsVisibility = true },
                        picture_url = it.sender_picture_url, onClick = {
                            if (highlite_message) {
                                openDialog.value = true
                            }
                        })
                    Spacer(modifier = Modifier.height(8.dp))

                }

            }
            item{
                if(valueExist.value){
                    LaunchedEffect(true ){
                        chatViewModel.getMoreMessages(chat_id)
                    }

                }

            }
        }
        Divider()

        if (openDialog.value) {
            SocialDialog(
                onDismiss = {
                    openDialog.value = false
                    highlited_message_text = ""
                },
                onConfirm = {
                },
                onCancel = {
                    openDialog.value = false
                    highlited_message_text = ""
                },
                title = "Highlight message?",
                info = highlited_message_text,
                icon = R.drawable.ic_highlight,
                actionButtonText = "Confirm"
            )
        }

        ChatScreenBottomInputs(modifier = Modifier, keyboardController, onEvent = {
            if (textState.text.length > 0) {
                onEvent(ChatEvent.SendMessage(message = textState.text.trim()))

            }
            textState.text = ""
        }, textState, highlite_message)


    }

    isImageAddedToStorage.let {
        response ->
        Log.d("ImagePicker", response.toString())
        when(response){
            is Response.Success->{}
            is Response.Loading->{}
            is Response.Failure->{
                Log.d("ImagePicker", "failure")
                Toast.makeText(LocalContext.current,"Failed to send the image",Toast.LENGTH_SHORT).show()
            }
        }
    }
    chatViewModel.firstMessagesState.value.let {
        when (it) {
            is Response.Success -> {
                frist_data.value=it.data
                valueExist.value=true
            }
            is Response.Loading -> {}
            is Response.Failure -> {}
        }
    }
    chatViewModel.messagesState.value.let {
        when (it) {
            is Response.Success -> {
                data.value=it.data
            }
            is Response.Loading -> {}
            is Response.Failure -> {}
        }
    }
    chatViewModel.moreMessagesState.value.let {
        when (it) {
            is Response.Success -> {
                Log.d("ACTTTTTTT","State"+it.data.size)
                data_new.value=it.data
            }
            is Response.Loading -> {}
            is Response.Failure -> {}
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SendButton(onEvent: () -> Unit, icon: Int, available: Boolean) {
    Card(
        modifier = Modifier.size(48.dp),
        shape = RoundedCornerShape(12.dp),
        backgroundColor = if (available) {
            Color(0xff0F0F30)
        } else {
            SocialTheme.colors.iconPrimary
        },
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



