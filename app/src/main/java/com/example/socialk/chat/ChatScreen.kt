package com.example.socialk.chat

import android.content.res.Configuration
import android.net.Uri
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
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
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Alignment.Companion.TopCenter
import androidx.compose.ui.Alignment.Companion.TopStart
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
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import coil.request.ImageRequest
import com.example.socialk.R
import com.example.socialk.camera.BackPressHandler
import com.example.socialk.chat.ChatComponents.*
import com.example.socialk.components.*
import com.example.socialk.create.ActivityTextFieldState
import com.example.socialk.create.LiveEvent
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

sealed class ImagePreviewEvent {
    object GoBack : ImagePreviewEvent()
    object BackPressed : ImagePreviewEvent()
}
sealed class ChatEvent {
    object GoToProfile : ChatEvent()
    object BackPressed : ChatEvent()
    object CreateNonExistingChatCollection : ChatEvent()
    object LiveInvite : ChatEvent()
    class ShareLocation(val latLng: LatLng) : ChatEvent()
    class JoinLive(val live_activity_id: String) : ChatEvent()
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
            CircularProgressIndicator(color=SocialTheme.colors.textPrimary)
        }
        is Response.Success -> {
            ChatContent(chat = result.data, onEvent = onEvent, chatViewModel = chatViewModel,activeUsersViewModel=activeUsersViewModel)
        }
        is Response.Failure -> {
            Log.d("CHATREPOSITYIMPLCHATCOLLECT","FAILURE")
            Toast.makeText(LocalContext.current, "Can't load in chat. Please try again", Toast.LENGTH_SHORT).show()
            if(result.e.message.equals("document_null")){
                Log.d("CHATREPOSITYIMPLCHATCOLLECT","document_null")
                onEvent(ChatEvent.CreateNonExistingChatCollection)
            }
            onEvent(ChatEvent.GoBack)




        }
    }

}

@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterialApi::class,
    ExperimentalAnimationApi::class
)
@Composable
fun ChatContent(chat: Chat, onEvent: (ChatEvent) -> Unit, chatViewModel: ChatViewModel,activeUsersViewModel:ActiveUsersViewModel) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val textState: TextFieldState = remember { ActivityTextFieldState() }
    val permission_flow = chatViewModel.granted_permission.collectAsState()
    val location_flow = chatViewModel.location.collectAsState()
    val isImageAddedToStorage by chatViewModel.isImageAddedToStorageFlow.collectAsState()
    val openDialog = rememberSaveable { mutableStateOf(false) }
    val openLocationDialog = rememberSaveable { mutableStateOf(false) }
    val openCreateLiveDialog = remember { mutableStateOf(false) }
    val displayLocationDialog = rememberSaveable { mutableStateOf<LatLng?>(null) }

    val dialogJoinLive= rememberSaveable { mutableStateOf<String?>(null) }

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
    var display_picture by remember { mutableStateOf("") }

    var uri by remember { mutableStateOf<Uri?>(null) }
    val uriReceived by chatViewModel.uriReceived
    chatViewModel.uri.observe(LocalLifecycleOwner.current) { newUri ->
        Log.d("ImageFromGallery", "image passed" + uri.toString())
        uri = newUri
    }

    //handle image loading animatipn
    var showLoading by remember { mutableStateOf(false) }
    val flowimageaddition = chatViewModel?.isImageAddedToStorageAndFirebaseState?.collectAsState()
    flowimageaddition?.value.let {
        when (it) {
            is Response.Success -> {
                showLoading = false

            }
            is Response.Failure -> {}
            is Response.Loading -> {
                showLoading = true
            }
            else -> {}
        }
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
            else->{}
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
            else->{}
        }
    }
    chatViewModel.messagesState.value.let {
        when (it) {
            is Response.Success -> {
                data.value = it.data
            }
            is Response.Loading -> {}
            is Response.Failure -> {}
            else->{}
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
            else->{}
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
            var previousMessage:ChatMessage? =null
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
                items(data.value!!) { chat->
                    val shouldGroup = previousMessage != null && previousMessage!!.sender_id == chat.sender_id

                    ChatBox(chat, onLongPress = { messageOptionsVisibility = true },highlite_message=highlite_message, displayPicture = {display_picture=it}, highliteMessage = {highlited_message_text=it}, openDialog =  {openDialog.value=true
                    },onEvent={ event ->
                        when (event) {
                            is ChatItemEvent.OpenLocation -> {
                                val values = event.latLng.split("/")
                                displayLocationDialog.value = LatLng(values.get(0).toDouble(), values.get(1).toDouble())
                            }
                            is ChatItemEvent.JoinLive->{
                                dialogJoinLive.value=event.live_activity_id
                            }
                        }

                    },shouldGroup=shouldGroup)
                    previousMessage=chat
                }
                items(frist_data.value!!) {chat->

                    val shouldGroup = previousMessage != null && previousMessage!!.sender_id == chat.sender_id
                    Log.d("CHATSCREENLOG",shouldGroup.toString())
                    Log.d("CHATSCREENLOG",chat.sender_id)
                    ChatBox(chat, onLongPress = { messageOptionsVisibility = true },highlite_message=highlite_message, displayPicture = {display_picture=it}, highliteMessage = {highlited_message_text=it}, openDialog =  {openDialog.value=true
                    },onEvent={ event ->
                        when (event) {
                            is ChatItemEvent.OpenLocation -> {
                                val values = event.latLng.split("/")
                                displayLocationDialog.value = LatLng(values.get(0).toDouble(), values.get(1).toDouble())
                            }
                            is ChatItemEvent.JoinLive->{
                                dialogJoinLive.value=event.live_activity_id
                            }
                        }

                    },shouldGroup=shouldGroup)
                    previousMessage=chat

                }
                items(data_new.value!!) {chat->
                    val shouldGroup = previousMessage != null && previousMessage!!.sender_id == chat.sender_id
                    ChatBox(chat, onLongPress = { messageOptionsVisibility = true },highlite_message=highlite_message, displayPicture = {display_picture=it}, highliteMessage = {highlited_message_text=it}, openDialog =  {openDialog.value=true
                    },onEvent={ event ->
                        when (event) {
                            is ChatItemEvent.OpenLocation -> {
                                val values = event.latLng.split("/")
                                displayLocationDialog.value = LatLng(values.get(0).toDouble(), values.get(1).toDouble())
                            }
                            is ChatItemEvent.JoinLive->{
                                dialogJoinLive.value=event.live_activity_id
                            }
                        }

                    },shouldGroup=shouldGroup)

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
            activeUsersViewModel.isUserAddedToLiveActivityState.value.let {it->
                when(it){
                    is Response.Success->{
                        Toast.makeText(LocalContext.current,"Joined live activity",Toast.LENGTH_SHORT).show()

                    }
                    is Response.Loading->{}
                    is Response.Failure->{
                        Toast.makeText(LocalContext.current,"Failed to join live activity",Toast.LENGTH_SHORT).show()
                    }
                }
            }
            if(  dialogJoinLive.value!=null){
                SocialDialog(
                    onDismiss = {
                        dialogJoinLive.value = null
                    },
                    onConfirm = {
                        onEvent(ChatEvent.JoinLive(dialogJoinLive.value.toString()))

                        dialogJoinLive.value= null
                    },
                    onCancel = {
                        dialogJoinLive.value= null
                    },
                    title = "Join live activity",
                    info = "Your profile picture and username will be display in your friend's live activity, join in to let others know that you are ready to start activities",
                    icon = R.drawable.ic_input,
                    actionButtonText = "Join", actionButtonTextColor =SocialTheme.colors.textInteractive
                )
            }else{

            }

            if (highlight_dialog) {
                HighLightDialog(modifier = Modifier.align(TopCenter), onEvent = { it ->
                    when (it) {
                        is ChatEvent.CloseDialog -> {
                            chat.highlited_message = null
                            highlight_dialog = false
                        }
                        else->{}
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
                                                .background(color = SocialTheme.colors.iconInteractive)
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


        com.example.socialk.chat.ChatComponents.Divider()
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
                                actionButtonText = "Share", actionButtonTextColor =SocialTheme.colors.iconInteractive
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
        //display loading animation when uploading image beacause it takes time to resize
        AnimatedVisibility(
            visible = showLoading,
            enter = slideInVertically(animationSpec = tween(500, easing = LinearEasing)),
            exit = scaleOut()
        ) {

            //UPLOADING IMAGE
            UploadBar(icon_anim = true, text = "Processing image", icon = R.drawable.ic_send_and_archive)
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
                else->{}
            }


        }, textState, highlite_message)


    }
    if(display_picture.isNotEmpty()){
        DisplayPictureScreen(display_picture,onEvent= {display_picture=""})
    }
    BottomDialogLiveActivity(
            state = bottomSheetState,
    onEvent = { event ->
        when (event) {
            is LiveEvent.CreateActiveUser->{
                onEvent(ChatEvent.CreateActiveUser(start_time=event.start_time, latLng = event.latLng, time_length = event.time_length))
            }
            is LiveEvent.SendLiveMessage->{
                onEvent(ChatEvent.SendLive)
            }
            is LiveEvent.CloseDialog->{
                Log.d("CHATSCREEN","Close dialog")
                scope.launch {
                    bottomSheetState.hide()
                }
            }
            else->{}
        }
    },
    activeUsersViewModel = activeUsersViewModel
    )

}

@Composable
fun ChatBox(chat: ChatMessage,highlite_message:Boolean,onLongPress:()->Unit,onEvent: (ChatItemEvent) -> Unit,openDialog:()->Unit,displayPicture:(String)->Unit,highliteMessage:(String)->Unit,shouldGroup:Boolean=false) {
    var padding= 12.dp
    if(shouldGroup){
        padding=0.dp
    }
    if (chat.sender_id == UserData.user!!.id) {
        ChatItemRight(text_type = chat.message_type,
            textMessage = chat.text,
            date = chat.sent_time,
            onLongPress = {onLongPress()}, onClick = {

                if (highlite_message) {
                    if(chat.message_type.equals("live")||chat.message_type.equals("latLng")){

                    }else{
                        openDialog()
                        highliteMessage(chat.text)
                    }
                }else{
                    if(chat.message_type.equals("uri")){
                        displayPicture(chat.text)

                    }
                }
            }, onEvent = onEvent)
        Spacer(modifier = Modifier.height(padding))
    } else {
        ChatItemLeft(text_type = chat.message_type,
            textMessage = chat.text,
            date = chat.sent_time,
            onLongPress ={onLongPress()},
            picture_url = chat.sender_picture_url, onClick = {

                if (highlite_message) {
                    if(chat.message_type.equals("live")||chat.message_type.equals("latLng")){
                    }else{
                        openDialog()
                        highliteMessage(chat.text)
                    }
                }else{
                    if(chat.message_type.equals("uri")){
                        displayPicture(chat.text)
                    }
                }
            }, onEvent = onEvent, displayPicture = !shouldGroup)
        Spacer(modifier = Modifier.height(padding))

    }

}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun DisplayPictureScreen(photoUri: String,onEvent:(ImagePreviewEvent)->Unit) {
    BackPressHandler(onBackPressed = { onEvent(ImagePreviewEvent.BackPressed) })
    Surface(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(color = androidx.compose.ui.graphics.Color.Black)
        ) {



            Image(
                painter = rememberImagePainter(photoUri),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            Row(
                Modifier
                    .align(TopStart)
                    .padding(top = 24.dp)) {
                Spacer(modifier = Modifier.width(24.dp))
                Card(modifier = Modifier, shape = RoundedCornerShape(12.dp), backgroundColor = Color.Black.copy(alpha=0.5f), onClick = { onEvent(ImagePreviewEvent.GoBack)}) {
                    Box(modifier = Modifier
                        .padding(12.dp)
                        .background(color = Color.Transparent)){
                        Icon(painter = painterResource(id = R.drawable.ic_back),      tint = Color.White,  contentDescription = null,modifier = Modifier.background(color=Color.Transparent))

                    }

                }

            }
        }
    }

}
