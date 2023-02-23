package com.example.socialk.chat

import android.net.Uri
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Alignment.Companion.TopCenter
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.socialk.R
import com.example.socialk.chat.ChatComponents.ChatItemLeft
import com.example.socialk.chat.ChatComponents.ChatItemRight
import com.example.socialk.chat.ChatComponents.ChatScreenBottomInputs
import com.example.socialk.chat.ChatComponents.ChatScreenTopBar
import com.example.socialk.components.CustomSocialDialog
import com.example.socialk.components.SocialDialog
import com.example.socialk.create.ActivityTextFieldState
import com.example.socialk.di.ChatViewModel
import com.example.socialk.model.*
import com.example.socialk.signinsignup.TextFieldState
import com.example.socialk.ui.theme.Inter
import com.example.socialk.ui.theme.SocialTheme

sealed class ChatEvent {
    object GoToProfile : ChatEvent()
    object CloseDialog : ChatEvent()
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
fun ChatScreen(
    viewModel: ChatCollectionViewModel,
    chat: Chat,
    chatViewModel: ChatViewModel,
    onEvent: (ChatEvent) -> Unit,
    textState: TextFieldState = remember { ActivityTextFieldState() }
) {
    val permission_flow = viewModel.granted_permission.collectAsState()
    val location_flow = viewModel.location.collectAsState()
    val isImageAddedToStorage by chatViewModel.isImageAddedToStorageFlow.collectAsState()
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
                        if (it.message_type.equals("uri")) {
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        ChatItemRight(text_type = it.message_type,
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
                        ChatItemLeft(text_type = it.message_type,
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
                        if (it.message_type.equals("uri")) {
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        ChatItemRight(text_type = it.message_type,
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
                        ChatItemLeft(text_type = it.message_type,
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
                        if (it.message_type.equals("uri")) {
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        ChatItemRight(text_type = it.message_type,
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
                        ChatItemLeft(text_type = it.message_type,
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
                item {
                    if (valueExist.value) {
                        LaunchedEffect(true) {
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
                HighLightDialog(modifier = Modifier.align(TopCenter), onEvent = { it ->
                    when (it) {
                        is ChatEvent.CloseDialog -> {
                            chat.highlited_message = null
                            highlight_dialog = false
                        }
                    }
                }, highlitedMessage = chat.highlited_message!!)

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
                                        ), onClick = { chatViewModel.onUriProcessed() }
                                    )
                                    Spacer(modifier = Modifier.width(24.dp))
                                    Card(shape = RoundedCornerShape(16.dp)) {

                                        Box(
                                            modifier = Modifier
                                                .background(color = Color(0xff0F0F30))
                                                .padding(12.dp)
                                        )
                                        {
                                            Row() {
                                                Icon(
                                                    painter = painterResource(id = R.drawable.ic_send),
                                                    contentDescription = null,
                                                    tint = Color.White
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                                ClickableText(
                                                    text = AnnotatedString("Send"),
                                                    style = TextStyle(
                                                        color = SocialTheme.colors.textSecondary,
                                                        fontFamily = Inter,
                                                        fontWeight = FontWeight.Medium,
                                                        fontSize = 14.sp
                                                    ),
                                                    onClick = {
                                                        onEvent(ChatEvent.SendImage(uri!!))
                                                        chatViewModel.onUriProcessed()
                                                    }
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
            CustomSocialDialog(
                onDismiss = {
                    openDialog.value = false
                    highlited_message_text = ""
                },
                onConfirm = {
                    chatViewModel.addHighLight(chat.id!!, highlited_message_text)
                    chat.highlited_message = highlited_message_text
                    openDialog.value = false
                },
                title = "Highlight message?",
                info = null,
                icon = R.drawable.ic_highlight, onCancel = {
                    openDialog.value = false
                    highlited_message_text = ""
                }
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (highlited_message_text.isValidUrl()) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(highlited_message_text)
                                .crossfade(true)
                                .build(),
                            placeholder = painterResource(R.drawable.ic_photo_library),
                            contentDescription = "image sent",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                        )

                    } else {
                        Text(
                            text = highlited_message_text.toString(),
                            color = SocialTheme.colors.textPrimary,
                            style = TextStyle(
                                fontFamily = Inter,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Normal
                            )
                        )
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        ClickableText(text = AnnotatedString("Cancel"), style = TextStyle(
                            color = SocialTheme.colors.textPrimary,
                            fontFamily = Inter, fontWeight = FontWeight.Medium, fontSize = 14.sp
                        ), onClick = {
                            openDialog.value = false
                            highlited_message_text = ""
                        })
                        Spacer(modifier = Modifier.width(24.dp))
                        ClickableText(text = AnnotatedString("Confirm"), style = TextStyle(
                            color = SocialTheme.colors.textInteractive,
                            fontFamily = Inter, fontWeight = FontWeight.Medium, fontSize = 14.sp
                        ), onClick = {
                            chatViewModel.addHighLight(chat.id!!, highlited_message_text)
                            chat.highlited_message = highlited_message_text
                            openDialog.value = false
                        })
                    }
                }

            }
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
                } else {
                    SocialDialog(
                        onDismiss = { openLocationDialog.value = false },
                        onConfirm = {
                            openLocationDialog.value = false
                            onEvent(ChatEvent.AskForPermission)
                        },
                        onCancel = { openLocationDialog.value = false },
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
    isImageAddedToStorage.let { response ->
        Log.d("ImagePicker", response.toString())
        when (response) {
            is Response.Success -> {}
            is Response.Loading -> {}
            is Response.Failure -> {
                Log.d("ImagePicker", "failure")
                Toast.makeText(LocalContext.current, "Failed to send the image", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }
    chatViewModel.firstMessagesState.value.let {
        when (it) {
            is Response.Success -> {
                frist_data.value = it.data
                valueExist.value = true
            }
            is Response.Loading -> {}
            is Response.Failure -> {}
        }
    }
    chatViewModel.messagesState.value.let {
        when (it) {
            is Response.Success -> {
                data.value = it.data
            }
            is Response.Loading -> {}
            is Response.Failure -> {}
        }
    }
    chatViewModel.moreMessagesState.value.let {
        when (it) {
            is Response.Success -> {
                Log.d("ACTTTTTTT", "State" + it.data.size)
                data_new.value = it.data
            }
            is Response.Loading -> {}
            is Response.Failure -> {}
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun HighLightDialog(modifier: Modifier, onEvent: (ChatEvent) -> Unit, highlitedMessage: String) {
    Box(
        modifier = modifier
            .padding(horizontal = 24.dp)
            .padding(top = 24.dp)
    ) {
        Card(shape = RoundedCornerShape(12.dp), elevation = 4.dp, onClick = {

            onEvent(ChatEvent.CloseDialog)
        }) {

            Box(
                modifier = Modifier
                    .background(color = SocialTheme.colors.uiBackground)
                    .padding(12.dp)
            ) {
                if (highlitedMessage.isValidUrl()) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(highlitedMessage)
                            .crossfade(true)
                            .build(),
                        placeholder = painterResource(R.drawable.ic_photo_library),
                        contentDescription = "image sent",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                    )

                } else {
                    Text(
                        text = highlitedMessage.toString(),
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
}

fun String.isValidUrl(): Boolean =
    Patterns.WEB_URL.matcher(this).matches() && this.contains("firebasestorage.googleapis")

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ChatScreen(
    viewModel: ChatCollectionViewModel, chat_id: String,
    user: User,
    chatViewModel: ChatViewModel,
    onEvent: (ChatEvent) -> Unit,
    textState: TextFieldState = remember { ActivityTextFieldState() }
) {
    val permission_flow = viewModel.granted_permission.collectAsState()
    val location_flow = viewModel.location.collectAsState()
    val openLocationDialog = remember { mutableStateOf(false) }
    val scrollState = rememberLazyListState()
    val openDialog = remember { mutableStateOf(false) }
    val chat = remember { mutableStateOf(Chat()) }
    var highlite_message by remember { mutableStateOf(false) }
    var highlited_message_text by remember { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current
    val data = remember {
        mutableStateOf(ArrayList<ChatMessage>())
    }

    var uri by remember { mutableStateOf<Uri?>(null) }
    val uriReceived by chatViewModel.uriReceived
    chatViewModel.uri.observe(LocalLifecycleOwner.current) { newUri ->
        Log.d("ImageFromGallery", "image passed" + uri.toString())
        uri = newUri
    }
    val data_new = remember {
        mutableStateOf(ArrayList<ChatMessage>())
    }
    val frist_data = remember {


        mutableStateOf(ArrayList<ChatMessage>())
    }
    val valueExist = remember { mutableStateOf(false) }
    val more_data = remember { mutableStateOf(ArrayList<ChatMessage>()) }
    val added_data_state = remember { mutableStateOf(false) }
    var messageOptionsVisibility by remember { mutableStateOf(false) }
    var highlight_dialog by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {
        ChatScreenTopBar(user, onEvent = onEvent)
        Divider()
        Box(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 12.dp)
        ) {
            LazyColumn(
                state = scrollState,
                modifier = Modifier
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
                        if (it.message_type.equals("uri")) {
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        ChatItemRight(text_type = it.message_type,
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
                        ChatItemLeft(text_type = it.message_type,
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
                        if (it.message_type.equals("uri")) {
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        ChatItemRight(text_type = it.message_type,
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
                        ChatItemLeft(text_type = it.message_type,
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
                        if (it.message_type.equals("uri")) {
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        ChatItemRight(text_type = it.message_type,
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
                        ChatItemLeft(text_type = it.message_type,
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
                item {
                    if (valueExist.value) {
                        LaunchedEffect(true) {
                            chatViewModel.getMoreMessages(chat_id)
                        }

                    }

                }
            }
            if (chat.value.highlited_message != null) {
                if (chat.value.highlited_message!!.isNotEmpty()) {
                    highlight_dialog = true
                }
            }
            if (highlight_dialog) {
                HighLightDialog(modifier = Modifier.align(Alignment.TopCenter), onEvent = { it ->
                    when (it) {
                        is ChatEvent.CloseDialog -> {
                            chat.value.highlited_message = null
                            highlight_dialog = false
                        }
                    }
                }, highlitedMessage = chat.value.highlited_message!!)

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
                                        ), onClick = { chatViewModel.onUriProcessed() }
                                    )
                                    Spacer(modifier = Modifier.width(24.dp))
                                    Card(shape = RoundedCornerShape(16.dp)) {

                                        Box(
                                            modifier = Modifier
                                                .background(color = Color(0xff0F0F30))
                                                .padding(12.dp)
                                        )
                                        {
                                            Row() {
                                                Icon(
                                                    painter = painterResource(id = R.drawable.ic_send),
                                                    contentDescription = null,
                                                    tint = Color.White
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                                ClickableText(
                                                    text = AnnotatedString("Send"),
                                                    style = TextStyle(
                                                        color = SocialTheme.colors.textSecondary,
                                                        fontFamily = Inter,
                                                        fontWeight = FontWeight.Medium,
                                                        fontSize = 14.sp
                                                    ),
                                                    onClick = {
                                                        onEvent(ChatEvent.SendImage(uri!!))
                                                        chatViewModel.onUriProcessed()
                                                    }
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
                } else {
                    SocialDialog(
                        onDismiss = { openLocationDialog.value = false },
                        onConfirm = {
                            openLocationDialog.value = false
                            onEvent(ChatEvent.AskForPermission)
                        },
                        onCancel = { openLocationDialog.value = false },
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

    chatViewModel.firstMessagesState.value.let {
        when (it) {
            is Response.Success -> {
                frist_data.value = it.data
                valueExist.value = true
            }
            is Response.Loading -> {}
            is Response.Failure -> {}
        }
    }
    chatViewModel.messagesState.value.let {
        when (it) {
            is Response.Success -> {
                data.value = it.data
            }
            is Response.Loading -> {}
            is Response.Failure -> {}
        }
    }
    chatViewModel.moreMessagesState.value.let {
        when (it) {
            is Response.Success -> {
                Log.d("ACTTTTTTT", "State" + it.data.size)
                data_new.value = it.data
            }
            is Response.Loading -> {}
            is Response.Failure -> {}
        }
    }
    chatViewModel.chatCollectionState.value.let {
        when (it) {
            is Response.Success -> {
                chat.value = it.data
            }
            is Response.Loading -> {
                CircularProgressIndicator()
            }
            is Response.Failure -> {

            }

        }
    }
}


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ChatScreen(
    viewModel: ChatCollectionViewModel, chat_id: String,
    activity: Activity,
    chatViewModel: ChatViewModel,
    onEvent: (ChatEvent) -> Unit,
    textState: TextFieldState = remember { ActivityTextFieldState() }
) {
    val isImageAddedToStorage by chatViewModel.isImageAddedToStorageFlow.collectAsState()
    val openDialog = remember { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current
    val data = remember {
        mutableStateOf(ArrayList<ChatMessage>())
    }
    val data_new = remember {
        mutableStateOf(ArrayList<ChatMessage>())
    }
    val openLocationDialog = remember { mutableStateOf(false) }
    val permission_flow = viewModel.granted_permission.collectAsState()
    val location_flow = viewModel.location.collectAsState()
    val frist_data = remember {


        mutableStateOf(ArrayList<ChatMessage>())
    }
    val valueExist = remember { mutableStateOf(false) }
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
                    if (it.message_type.equals("uri")) {
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    ChatItemRight(text_type = it.message_type,
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
                    ChatItemLeft(text_type = it.message_type,
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
                    if (it.message_type.equals("uri")) {
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    ChatItemRight(text_type = it.message_type,
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
                    ChatItemLeft(text_type = it.message_type,
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
                    if (it.message_type.equals("uri")) {
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    ChatItemRight(text_type = it.message_type,
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
                    ChatItemLeft(text_type = it.message_type,
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
            item {
                if (valueExist.value) {
                    LaunchedEffect(true) {
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
                } else {
                    SocialDialog(
                        onDismiss = { openLocationDialog.value = false },
                        onConfirm = {
                            openLocationDialog.value = false
                            onEvent(ChatEvent.AskForPermission)
                        },
                        onCancel = { openLocationDialog.value = false },
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

    isImageAddedToStorage.let { response ->
        Log.d("ImagePicker", response.toString())
        when (response) {
            is Response.Success -> {}
            is Response.Loading -> {}
            is Response.Failure -> {
                Log.d("ImagePicker", "failure")
                Toast.makeText(LocalContext.current, "Failed to send the image", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }
    chatViewModel.firstMessagesState.value.let {
        when (it) {
            is Response.Success -> {
                frist_data.value = it.data
                valueExist.value = true
            }
            is Response.Loading -> {}
            is Response.Failure -> {}
        }
    }
    chatViewModel.messagesState.value.let {
        when (it) {
            is Response.Success -> {
                data.value = it.data
            }
            is Response.Loading -> {}
            is Response.Failure -> {}
        }
    }
    chatViewModel.moreMessagesState.value.let {
        when (it) {
            is Response.Success -> {
                Log.d("ACTTTTTTT", "State" + it.data.size)
                data_new.value = it.data
            }
            is Response.Loading -> {}
            is Response.Failure -> {}
        }
    }
}


