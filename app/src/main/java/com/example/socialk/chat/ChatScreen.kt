package com.example.socialk.chat

import android.content.res.Configuration
import android.graphics.drawable.GradientDrawable.Orientation
import android.net.Uri
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.*
import androidx.compose.material.Divider
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.BottomCenter
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Alignment.Companion.TopCenter
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
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
import androidx.fragment.app.viewModels
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.socialk.R
import com.example.socialk.chat.ChatComponents.*
import com.example.socialk.components.BottomDialog
import com.example.socialk.components.BottomDialogEvent
import com.example.socialk.components.CustomSocialDialog
import com.example.socialk.components.SocialDialog
import com.example.socialk.create.ActivityTextFieldState
import com.example.socialk.create.LiveEvent
import com.example.socialk.create.LiveScreen
import com.example.socialk.create.LiveScreenContent
import com.example.socialk.create.components.BottomDialogLiveActivity
import com.example.socialk.di.ActiveUsersViewModel
import com.example.socialk.di.ChatViewModel
import com.example.socialk.map.loadIcon
import com.example.socialk.model.*
import com.example.socialk.signinsignup.TextFieldState
import com.example.socialk.ui.theme.Inter
import com.example.socialk.ui.theme.SocialTheme
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import kotlinx.coroutines.launch

sealed class ChatEvent {
    object GoToProfile : ChatEvent()
    object LiveInvite : ChatEvent()
    class ShareLocation(val latLng: LatLng) : ChatEvent()
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
    object SendLive: ChatEvent()
    data class CreateActiveUser(
        val start_time: String,
        val latLng: String,
        val time_length: String
    ): ChatEvent()
}


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun HighLightDialog(modifier: Modifier, onEvent: (ChatEvent) -> Unit, highlitedMessage: String) {
    Box(
        modifier = modifier
            .padding(horizontal = 24.dp)
            .padding(top = 24.dp)
    ) {
        Card(shape = RoundedCornerShape(12.dp), elevation = 8.dp, onClick = {

            onEvent(ChatEvent.CloseDialog)
        }) {

            Box(
                modifier = Modifier
                    .background(color = SocialTheme.colors.uiBackground)
                    .padding(
                        if (highlitedMessage.isValidUrl()) {
                            0.dp
                        } else {
                            12.dp
                        }
                    )
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


@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterialApi::class)
@Composable
fun ChatScreen(activeUsersViewModel:ActiveUsersViewModel,
    viewModel: ChatCollectionViewModel,
    chatViewModel: ChatViewModel,
    onEvent: (ChatEvent) -> Unit,
) {
    val chatState = chatViewModel.chatCollectionState.collectAsState()

    when (val result = chatState.value) {
        is Response.Loading -> {
            // Display a circular loading indicator
            CircularProgressIndicator()
        }
        is Response.Success -> {
            ChatContent(chat = result.data, onEvent = onEvent, chatViewModel = chatViewModel,activeUsersViewModel=activeUsersViewModel)
        }
        is Response.Failure -> {
            Toast.makeText(LocalContext.current, "Can't load in chat", Toast.LENGTH_SHORT).show()
        }
    }

}

@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterialApi::class)
@Composable
fun ChatContent(chat: Chat, onEvent: (ChatEvent) -> Unit, chatViewModel: ChatViewModel,activeUsersViewModel:ActiveUsersViewModel) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val textState: TextFieldState = remember { ActivityTextFieldState() }
    val permission_flow = chatViewModel.granted_permission.collectAsState()
    val location_flow = chatViewModel.location.collectAsState()
    val isImageAddedToStorage by chatViewModel.isImageAddedToStorageFlow.collectAsState()
    val openDialog = remember { mutableStateOf(false) }
    val openLocationDialog = remember { mutableStateOf(false) }
    val openCreateLiveDialog = remember { mutableStateOf(false) }
    val displayLocationDialog = remember { mutableStateOf<LatLng?>(null) }
    val hasExecutedMoreMessages = remember { mutableStateOf(false) }
    var bottomSheetState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)


    val scope = rememberCoroutineScope()
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
                            }, onEvent = { event ->
                                when (event) {
                                    is ChatItemEvent.OpenLocation -> {
                                        val values = event.latLng.split("/")
                                        val lat = values.get(0).toDouble()
                                        val lng = values.get(1).toDouble()
                                        displayLocationDialog.value = LatLng(lat, lng)
                                    }
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
                            }, onEvent = { event ->
                                when (event) {
                                    is ChatItemEvent.OpenLocation -> {
                                        val values = event.latLng.split("/")
                                        val lat = values.get(0).toDouble()
                                        val lng = values.get(1).toDouble()
                                        displayLocationDialog.value = LatLng(lat, lng)
                                    }
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
                            }, onEvent = { event ->
                                when (event) {
                                    is ChatItemEvent.OpenLocation -> {
                                        val values = event.latLng.split("/")
                                        val lat = values.get(0).toDouble()
                                        val lng = values.get(1).toDouble()
                                        displayLocationDialog.value = LatLng(lat, lng)
                                    }
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
                            }, onEvent = { event ->
                                when (event) {
                                    is ChatItemEvent.OpenLocation -> {
                                        val values = event.latLng.split("/")
                                        val lat = values.get(0).toDouble()
                                        val lng = values.get(1).toDouble()
                                        displayLocationDialog.value = LatLng(lat, lng)
                                    }
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
                            }, onEvent = { event ->
                                when (event) {
                                    is ChatItemEvent.OpenLocation -> {
                                        val values = event.latLng.split("/")
                                        val lat = values.get(0).toDouble()
                                        val lng = values.get(1).toDouble()
                                        displayLocationDialog.value = LatLng(lat, lng)
                                    }
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
                            }, onEvent = { event ->
                                when (event) {
                                    is ChatItemEvent.OpenLocation -> {
                                        val values = event.latLng.split("/")
                                        val lat = values.get(0).toDouble()
                                        val lng = values.get(1).toDouble()
                                        displayLocationDialog.value = LatLng(lat, lng)
                                    }
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
        if (displayLocationDialog.value != null) {
            var isMapLoaded by remember { mutableStateOf(false) }


            var uiSettings by remember {
                mutableStateOf(
                    MapUiSettings(
                        zoomControlsEnabled = false,
                        myLocationButtonEnabled = true,
                        indoorLevelPickerEnabled = true
                    )
                )
            }
            var latLng: LatLng? = null
            displayLocationDialog.value.let {
                latLng = it
            }
            var bitmap: BitmapDescriptor? =
                loadIcon(LocalContext.current, UserData.user?.pictureUrl!!, R.drawable.ic_person)
            var properties by remember {
                mutableStateOf(MapProperties(mapType = MapType.NORMAL))
            }

            val cameraPositionState: CameraPositionState =
                rememberCameraPositionState {
                    position = CameraPosition.fromLatLngZoom(latLng!!, 11f)
                }
            Dialog(onDismissRequest = { displayLocationDialog.value = null }) {
                Card(shape = RoundedCornerShape(16.dp)) {
                    Box(
                        modifier = Modifier
                            .background(color = SocialTheme.colors.uiBackground)
                            .padding(12.dp)
                    ) {
                        Column(horizontalAlignment = CenterHorizontally) {


                            Card(
                                Modifier
                                    .fillMaxWidth()
                                    .height(
                                        if (LocalContext.current
                                                .getResources()
                                                .getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE
                                        ) {
                                            200.dp
                                        } else {
                                            300.dp
                                        }
                                    ),
                                elevation = 0.dp,
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                GoogleMap(
                                    Modifier.fillMaxSize(),
                                    cameraPositionState,
                                    properties = properties, onMapLoaded = {
                                        isMapLoaded = true
                                    },
                                    uiSettings = uiSettings
                                ) {
                                    MarkerInfoWindow(
                                        state = MarkerState(
                                            position = latLng!!
                                        )
                                    ) {
                                        Column() {
                                            Card(shape = RoundedCornerShape(6.dp)) {
                                                Box(
                                                    modifier = Modifier
                                                        .background(color = SocialTheme.colors.uiBackground)
                                                        .padding(6.dp)
                                                ) {
                                                    androidx.compose.material.Text(
                                                        text = "Current location",
                                                        style = TextStyle(
                                                            fontFamily = Inter,
                                                            fontWeight = FontWeight.Normal,
                                                            fontSize = 14.sp
                                                        ),
                                                        color = SocialTheme.colors.textPrimary
                                                    )
                                                }
                                            }
                                            Spacer(modifier = Modifier.height(4.dp))
                                        }

                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                ClickableText(
                                    modifier = Modifier,
                                    text = AnnotatedString("Dismiss"),
                                    style = TextStyle(
                                        fontFamily = Inter,
                                        color = SocialTheme.colors.textPrimary,
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 14.sp
                                    ), onClick = {
                                        displayLocationDialog.value = null
                                    }
                                )
                            }
                        }

                        if (!isMapLoaded) {
                            SocialTheme {
                                androidx.compose.animation.AnimatedVisibility(
                                    modifier = Modifier.matchParentSize(),
                                    visible = !isMapLoaded,
                                    enter = EnterTransition.None,
                                    exit = fadeOut()
                                ) {
                                    CircularProgressIndicator(
                                        modifier = Modifier
                                            .wrapContentSize()
                                    )
                                }
                            }
                        }


                    }
                }
            }

        }

        if (openLocationDialog.value) {
            permission_flow.value.let {
                if (it) {
                    location_flow.value.let { latLng ->
                        if (latLng != null) {
                            SocialDialog(
                                onDismiss = {
                                    openLocationDialog.value = false
                                },
                                onConfirm = {
                                    onEvent(ChatEvent.ShareLocation(latLng))
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
                            Toast.makeText(
                                LocalContext.current,
                                latLng.toString(),
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            Toast.makeText(
                                LocalContext.current,
                                "Error - Location might be turned off",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

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
                is ChatEvent.LiveInvite -> {
                    scope.launch {
                        bottomSheetState.show()
                    }
                }
            }


        }, textState, highlite_message)


    }
    BottomDialogLiveActivity(
            state = bottomSheetState,
    onEvent = { event ->
        when (event) {
            is LiveEvent.CreateActiveUser->{
                Log.d("BOTTOMDIALOGLIVE","create USEr")

                onEvent(ChatEvent.CreateActiveUser(start_time=event.start_time, latLng = event.latLng, time_length = event.time_length))
            }
            is LiveEvent.SendLiveMessage->{
                onEvent(ChatEvent.SendLive)
            }
            is LiveEvent.CloseDialog->{
                scope.launch {
                    bottomSheetState.hide()
                }
            }
        }
    },
    activeUsersViewModel = activeUsersViewModel
    )

}