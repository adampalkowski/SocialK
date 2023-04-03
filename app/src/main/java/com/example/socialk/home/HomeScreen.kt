package com.example.socialk.home

import android.net.Uri
import android.util.Log
import androidx.activity.OnBackPressedCallback
import androidx.compose.animation.*
import androidx.compose.animation.AnimatedContentScope.SlideDirection.Companion.End
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.Absolute.Center
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.layout.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.socialk.*
import com.example.socialk.R
import com.example.socialk.camera.CameraEvent
import com.example.socialk.camera.CameraView
import com.example.socialk.camera.ImageDisplay
import com.example.socialk.components.*
import com.example.socialk.di.ActiveUsersViewModel
import com.example.socialk.di.ActivityViewModel
import com.example.socialk.di.ChatViewModel
import com.example.socialk.model.*
import com.example.socialk.signinsignup.AuthViewModel
import com.example.socialk.ui.theme.Inter
import com.example.socialk.ui.theme.SocialTheme
import com.google.accompanist.systemuicontroller.SystemUiController
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.util.*
import java.util.concurrent.Executor
import com.example.socialk.home.MapBox as MapBox1

sealed class ActivityEvent() {
    class OpenActivitySettings(activity: Activity) : ActivityEvent() {
        val activity = activity
    }

    class GoToProfile(user_id: String) : ActivityEvent() {
        val user_id = user_id
    }

    class DisplayPicture(val photo_url: String, val activity_id: String) : ActivityEvent()
    class OpenActivityChat(activity: Activity) : ActivityEvent() {
        val activity = activity
    }

    class ActivityLiked(activity: Activity) : ActivityEvent() {
        val activity = activity
    }

    class ActivityUnLiked(activity: Activity) : ActivityEvent() {
        val activity = activity
    }

    class GoToMap(latlng: String) : ActivityEvent() {
        val latlng = latlng
    }

    class OpenCamera(val activity_id: String) : ActivityEvent()
}
sealed class ActivityPreviewEvent{
    class GoToProfile(val id:String) :ActivityPreviewEvent()
    class OpenActivitySettings(val activity: Activity) :ActivityPreviewEvent()
}

sealed class HomeEvent {
    object GoToProfile : HomeEvent()
    class OpenCamera(val activity_id: String) : HomeEvent()
    class DestroyLiveActivity(val id: String) : HomeEvent()
    class LeaveLiveActivity(val activity_id: String, val user_id: String) : HomeEvent()
    class DisplayPicture(val photo_url: String, val activity_id: String) : HomeEvent()
    object LogOut : HomeEvent()
    object GoToMemories : HomeEvent()
    object BackPressed : HomeEvent()
    object GoToSettings : HomeEvent()
    object RemovePhotoFromGallery : HomeEvent()
    class GoToMap(latlng: String) : HomeEvent() {
        val latlng = latlng
    }

    class GoToChat(activity: Activity) : HomeEvent() {
        val activity = activity
    }

    class ActivityLiked(activity: Activity) : HomeEvent() {
        val activity = activity
    }

    class ActivityUnLiked(activity: Activity) : HomeEvent() {
        val activity = activity
    }

    class GoToProfileWithID(user_id: String) : HomeEvent() {
        val user_id = user_id
    }
}

@OptIn(ExperimentalMaterialApi::class, ExperimentalAnimationApi::class)
@Composable
fun HomeScreen(
    systemUiController: SystemUiController,
    activeUsersViewModel: ActiveUsersViewModel?,
    activityViewModel: ActivityViewModel?,
    chatViewModel: ChatViewModel,
    viewModel: AuthViewModel?, homeViewModel: HomeViewModel,
    onEvent: (HomeEvent) -> Unit,
    bottomNavEvent: (Destinations) -> Unit,
    outputDirectory: File?,
    executor: Executor?,
    onImageCaptured: (Uri) -> Unit,
) {


    //set status bar TRANSPARENT
    SideEffect {
        systemUiController.setStatusBarColor(color = androidx.compose.ui.graphics.Color.Transparent)
        systemUiController.setNavigationBarColor(color = androidx.compose.ui.graphics.Color.Transparent)
    }
    val backCallback = remember { // remember the callback to avoid recompositions
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                onEvent(HomeEvent.BackPressed)
            }
        }
    }
    val scope = rememberCoroutineScope()

    val openDialog = remember { mutableStateOf(false) }
    var bottomSheetState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)
    var bottomSheetType by remember {
        mutableStateOf("settings")
    }
    val isDark = isSystemInDarkTheme()
    val scaffoldState = rememberScaffoldState()
    val showDialogState: Boolean by homeViewModel?.showDialog!!.collectAsState()
    var bottomSheetActivity by rememberSaveable { mutableStateOf(Activity()) }
    var displayActivity by remember { mutableStateOf(false) }


    androidx.compose.material.Scaffold(modifier = Modifier.systemBarsPadding(),
        scaffoldState = scaffoldState,
        bottomBar = {
            BottomBar(
                onTabSelected = { screen -> bottomNavEvent(screen) },
                currentScreen = Home
            )
        },
        content = { it ->
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .background(SocialTheme.colors.uiBackground),
                color = SocialTheme.colors.uiBackground
            ) {
                HomeScreenContent(activeUsersViewModel = activeUsersViewModel,
                    viewModel = viewModel,
                    activityViewModel = activityViewModel,
                    padding = it, homeViewModel = homeViewModel,
                    isDark = isDark,
                    activityEvent = {
                        when (it) {
                            is ActivityEvent.OpenActivityChat -> {
                                onEvent(HomeEvent.GoToChat(it.activity))
                            }
                            is ActivityEvent.ActivityLiked -> {
                                Log.d("HomeScreen", "like")
                                onEvent(HomeEvent.ActivityLiked(it.activity))
                            }
                            is ActivityEvent.ActivityUnLiked -> {
                                Log.d("HomeScreen", "dislike")
                                onEvent(HomeEvent.ActivityUnLiked(it.activity))

                            }
                            is ActivityEvent.GoToMap -> {
                                onEvent(HomeEvent.GoToMap(latlng = it.latlng))

                            }
                            is ActivityEvent.GoToProfile -> {
                                onEvent(HomeEvent.GoToProfileWithID(user_id = it.user_id))

                            }
                            is ActivityEvent.OpenActivitySettings -> {
                                Log.d("HomeScreen", "open settings")
                                bottomSheetType = "settings"
                                bottomSheetActivity = it.activity
                                scope.launch {
                                    bottomSheetState.show()
                                }
                            }
                            is ActivityEvent.OpenCamera -> {
                                onEvent(HomeEvent.OpenCamera(it.activity_id))
                            }
                            is ActivityEvent.DisplayPicture -> {
                                onEvent(HomeEvent.DisplayPicture(it.photo_url, it.activity_id))
                            }

                            else -> {}
                        }

                    },
                    onEvent = { homeEvent ->

                        onEvent(homeEvent)
                    }, onLongClick = { activity ->
                        bottomSheetActivity = activity
                        displayActivity = true
                    })
            }

        })

    val uri_flow = homeViewModel.photo_uri.collectAsState()
    val flowImageDelete = activityViewModel?.isImageRemoveFromActivityState?.collectAsState()
    flowImageDelete?.value.let {
        when (it) {
            is Response.Success -> {
                val photoFile =
                    File(outputDirectory, uri_flow.value!!.lastPathSegment)
                photoFile.delete()
            }
            is Response.Failure -> {

            }
            is Response.Loading -> {
            }
            else -> {}
        }
    }
    AnimatedVisibility(
        visible = homeViewModel.shouldShowCamera.value,
        enter = scaleIn(animationSpec = tween(800), transformOrigin = TransformOrigin(1f, 0.2f)),
        exit = scaleOut(animationSpec = tween(800), transformOrigin = TransformOrigin(1f, 0.2f))

    ) {
        CameraView(
            onEvent = { event ->
                when (event) {
                    is CameraEvent.BackPressed -> {
                        if (homeViewModel.shouldShowCamera.value) {
                            homeViewModel.shouldShowCamera.value = false
                        }
                        if (homeViewModel.shouldShowPhoto.value) {
                            homeViewModel.shouldShowPhoto.value = false
                        }
                    }
                    is CameraEvent.SavePhoto -> {
                        homeViewModel.shouldShowCamera.value = false
                        homeViewModel.shouldShowPhoto.value = false
                    }
                    else -> {}
                }
            },
            outputDirectory = outputDirectory!!,
            executor = executor!!,
            onImageCaptured = onImageCaptured,
            onError = { Log.e("kilo", "View error:", it) }
        )

        if (homeViewModel.shouldShowPhoto.value) {
            if (uri_flow.value == null) {
                homeViewModel.shouldShowPhoto.value = false
            } else {
                ImageDisplay(
                    modifier = Modifier.fillMaxSize(),
                    uri_flow.value!!, onEvent = { event ->
                        when (event) {
                            is CameraEvent.RemovePhoto -> {
                                val photoFile =
                                    File(outputDirectory, uri_flow.value!!.lastPathSegment)
                                photoFile.delete()
                                homeViewModel.shouldShowPhoto.value = false
                                homeViewModel.shouldShowCamera.value = true
                            }
                            is CameraEvent.BackPressed -> {
                                val photoFile =
                                    File(outputDirectory, uri_flow.value!!.lastPathSegment)
                                photoFile.delete()
                                homeViewModel.shouldShowPhoto.value = false
                                homeViewModel.shouldShowCamera.value = false


                            }
                            is CameraEvent.SetPicture -> {

                                if (homeViewModel.camera_activity_id.value.isNotEmpty()) {
                                    activityViewModel?.setParticipantImage(
                                        homeViewModel.camera_activity_id.value,
                                        viewModel?.currentUser?.uid.toString(),
                                        event.image_url
                                    )
                                }

                                homeViewModel.shouldShowPhoto.value = false
                                homeViewModel.shouldShowCamera.value = false

                            }
                            is CameraEvent.SavePhoto -> {
                                homeViewModel.shouldShowCamera.value = false
                                homeViewModel.shouldShowPhoto.value = false
                            }
                            is CameraEvent.ImageSent -> {
                                homeViewModel.shouldShowPhoto.value = false
                                homeViewModel.shouldShowCamera.value = false
                            }
                            is CameraEvent.DeletePhoto -> {
                                activityViewModel?.removeParticipantImage(
                                    homeViewModel.camera_activity_id.value,
                                    viewModel?.currentUser!!.uid
                                )

                                homeViewModel.shouldShowPhoto.value = false
                                homeViewModel.shouldShowCamera.value = false
                                homeViewModel.displayPhoto.value = false
                            }
                            else -> {}
                        }

                    }, activityViewModel, homeViewModel.displayPhoto.value
                )
            }

        }
    }

    activityViewModel?.activityState?.value.let { event ->
        when (event) {
            is Response.Success -> {
                Log.d("Homefragment2", event.toString())
                if (event.data != null) {
                    homeViewModel?.setActivity(event.data)
                    homeViewModel?.setShowDialog(true)
                }

            }
            is Response.Loading -> {

            }
            is Response.Failure -> {

            }
            else -> {}
        }
    }
    activityDialog(
        activity = homeViewModel?.activity?.value,
        activityDialogState = showDialogState,
        onEvent = {
            homeViewModel?.setShowDialog(false)
            activityViewModel?.resetActivityState()
            homeViewModel?.removeActivity()
            Log.d("homescreen", "falseeee")
        })
    AnimatedVisibility(visible=displayActivity, enter = scaleIn(animationSpec = tween(500)), exit =scaleOut( tween(500)) ){
        Dialog(onDismissRequest = { displayActivity = false }) {
            ActivityPreview(modifier=Modifier,bottomSheetActivity, onEvent = {event->})
        }
    }

    BottomDialog(
        state = bottomSheetState,
        activity = bottomSheetActivity,
        type = bottomSheetType,
        onEvent = { event ->
            when (event) {
                is BottomDialogEvent.SendMessage -> {
                    chatViewModel.addMessage(
                        bottomSheetActivity.id,
                        //todo set sender picture_url
                        ChatMessage(
                            text = event.message,
                            sender_picture_url = UserData.user?.pictureUrl!!,
                            sent_time = "",
                            sender_id = UserData.user!!.id,
                            message_type = "text",
                            id = ""
                        )
                    )
                }
                is BottomDialogEvent.removeUserFromActivity -> {
                    activityViewModel?.removeUserFromActivityInvites(
                        bottomSheetActivity,
                        UserData.user!!.id!!
                    )
                    activityViewModel?.getActivitiesForUser(viewModel?.currentUser!!.uid)

                }
                is BottomDialogEvent.LeaveActivity -> {
                    activityViewModel?.unlikeActivity(
                        event.activity.id,
                        UserData.user!!
                    )
                }
                else -> {}

            }
        },
        chatViewModel = chatViewModel
    )

}

@Composable
fun ActivityPreview(modifier: Modifier = Modifier, bottomSheetActivity: Activity,onEvent:(ActivityPreviewEvent)->Unit) {
    Box(modifier=modifier) {
        Column() {
            if(bottomSheetActivity.location.isNotEmpty()){
            MapBox1(location=bottomSheetActivity.location)
            Spacer(modifier = Modifier.height(16.dp))
           }else{
                DataBox(icon = R.drawable.ic_location_24,bottomSheetActivity.custom_location,title="Location")
                Spacer(modifier = Modifier.height(4.dp))
           }

           /* androidx.compose.material3.Card(shape= RoundedCornerShape(12.dp)) {
                Box(
                    Modifier
                        .background(color = SocialTheme.colors.uiBackground)
                        .padding(8.dp)){
                    Column() {
                        ActivityItemCreatorBox(
                            onClick = { onEvent(ActivityPreviewEvent.GoToProfile(bottomSheetActivity.creator_id)) },
                            pictureUrl = bottomSheetActivity.creator_profile_picture,
                            username = bottomSheetActivity.creator_username,
                            timeLeft = bottomSheetActivity.time_left,
                            onSettingsClick = { onEvent(ActivityPreviewEvent.OpenActivitySettings(bottomSheetActivity)) })
                        Spacer(modifier = Modifier.height(12.dp))
                        ActivityTextBox(modifier = Modifier, title =bottomSheetActivity.title , description =bottomSheetActivity.description )
                    }

                }
            }*/

            Spacer(modifier = Modifier.height(4.dp))
            DataBox(icon=R.drawable.ic_date_24,bottomSheetActivity.date,title="Date")
            Spacer(modifier = Modifier.height(4.dp))
            DataBox(icon=R.drawable.ic_timer_24,bottomSheetActivity.start_time + " - " + bottomSheetActivity.end_time,title="Time")
            //ChatMessageBox()
            Spacer(modifier = Modifier.height(4.dp))
            ParticipantsBox(bottomSheetActivity.participants_usernames,bottomSheetActivity.participants_profile_pictures)

        }


    }
}
@Composable
fun DataBox(icon:Int,text:String,title:String) {

    androidx.compose.material3.Card(
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.background(color = Color.Transparent),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            Modifier
                .fillMaxWidth()
                .background(color = Color.Black.copy(alpha = 0.3f))
        ) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),horizontalArrangement = Arrangement.Start,
            ) {
                androidx.compose.material3.Icon(
                    painter = painterResource(id = icon),
                    contentDescription = null,
                    tint = Color.White
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "$title:",color=Color.White,
                    style = TextStyle(
                        fontFamily = Inter,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp
                    )
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = text,color=Color.White,
                    style = TextStyle(
                        fontFamily = Inter,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp
                    )
                )

            }
        }
    }
}
@Composable
fun ParticipantsBox(participantsUsernames: HashMap<String, String>, participantsProfilePictures: HashMap<String, String>) {
        Box(
            Modifier
                .fillMaxWidth()
               ) {
                    Column() {
                        DataBox(icon =R.drawable.ic_person_done , text = "" ,title="Participants")
                        Spacer(modifier = Modifier.height(2.dp))
                    Row(Modifier.horizontalScroll(rememberScrollState())){
                        participantsProfilePictures.forEach{
                            ParticipantBoxItem(it.value,participantsUsernames[it.key]!!)
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                    }
                    }

        }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun ParticipantBoxItem(picture_url: String, username: String) {
    androidx.compose.material3.Card(shape=RoundedCornerShape(8.dp),modifier=Modifier.background(color=Color.Transparent),     colors = CardDefaults.cardColors(containerColor = Color.Transparent)) {
        Box(
            Modifier
                .background(color = Color.Black.copy(0.3f))
                .padding(8.dp)){
            Column(horizontalAlignment = Alignment.CenterHorizontally){
                GlideImage(
                    model = picture_url,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp).clip(CircleShape),
                    contentScale = ContentScale.Crop,
                )

                Text(text = username,color=Color.White)
            }
        }
    }



}

@Composable
fun MapBox(location:String){
    val cameraPositionState: CameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(0.0, 0.0), 11f)
    }
    var uiSettings by remember {
        mutableStateOf(
            MapUiSettings(
                zoomControlsEnabled = false,
                myLocationButtonEnabled = true,
                indoorLevelPickerEnabled = true
            )
        )
    }
    val latLngVA = location.split("/")
    val latitude = latLngVA[0].toDouble()
    val longitude = latLngVA[1].toDouble()
    val latLng = LatLng(latitude, longitude)
    cameraPositionState.position = CameraPosition.fromLatLngZoom(latLng, 13f)




    var isMapLoaded by remember { mutableStateOf(false) }
    var properties by remember {
        mutableStateOf(MapProperties(mapType = MapType.NORMAL))
    }
    androidx.compose.material3.Card(
        Modifier
            .fillMaxWidth()
            .height(200.dp), shape = RoundedCornerShape(12.dp)
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
                    position = latLng
                )
            ) {
                Column() {
                    androidx.compose.material3.Card(shape = RoundedCornerShape(6.dp)) {
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

}
@Composable
fun TimeBox(time: String) {
    androidx.compose.material3.Card(shape = RoundedCornerShape(100.dp)) {
        Box(
            Modifier
                .background(color = SocialTheme.colors.uiBackground)
                .fillMaxWidth()) {
            Row(Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                androidx.compose.material3.Icon(painter = painterResource(id = R.drawable.ic_timer_24), contentDescription =null, tint = SocialTheme.colors.iconPrimary )
                Spacer(modifier = Modifier.width(24.dp))
                Text(
                    text = time,
                    style = TextStyle(
                        fontFamily = Inter,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp
                    )
                )
            }
        }
    }

}

@Composable
fun DateBox(date: String) {
    androidx.compose.material3.Card(shape = RoundedCornerShape(100.dp)) {
        Box(
            Modifier
                .background(color = SocialTheme.colors.uiBackground)
                .fillMaxWidth()) {
            Row(Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                androidx.compose.material3.Icon(painter = painterResource(id = R.drawable.ic_date_24), contentDescription =null, tint = SocialTheme.colors.iconPrimary )
                Spacer(modifier = Modifier.width(24.dp))
                Text(
                    text = date,
                    style = TextStyle(
                        fontFamily = Inter,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp
                    )
                )
            }
        }}

}
@Composable
fun LocationBox(customLocation: String) {
    androidx.compose.material3.Card(shape = RoundedCornerShape(100.dp)) {
        Box(
            Modifier
                .background(color = SocialTheme.colors.uiBackground)
                .fillMaxWidth()) {
            Row(Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                androidx.compose.material3.Icon(painter = painterResource(id = R.drawable.ic_location_24), contentDescription =null, tint = SocialTheme.colors.iconPrimary )
                Spacer(modifier = Modifier.width(24.dp))
                Text(
                    text = customLocation,
                    style = TextStyle(
                        fontFamily = Inter,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp
                    )
                )
            }
        }}

}


@Composable
fun activityDialog(activity: Activity?, activityDialogState: Boolean, onEvent: () -> Unit) {
    if (activityDialogState) {
        ActivityDialog(
            onDismiss = onEvent,
            onConfirm = { },
            onCancel = {
                onEvent()
            },
            title = "asd",
            info = "ass",
            icon = R.drawable.ic_delete,
            activity = activity!!
        )
    } else {

    }

}


@OptIn(ExperimentalMaterialApi::class, ExperimentalAnimationApi::class)
@Composable
fun HomeScreenContent(
    activityEvent: (ActivityEvent) -> Unit, activeUsersViewModel: ActiveUsersViewModel?,
    viewModel: AuthViewModel?,
    activityViewModel: ActivityViewModel?,
    padding: PaddingValues,
    isDark: Boolean,
    onEvent: (HomeEvent) -> Unit,
    homeViewModel: HomeViewModel,
    onLongClick: (Activity) -> Unit
) {

    val refreshScope = rememberCoroutineScope()
    var refreshing by remember { mutableStateOf(false) }
    var activitiesExist = remember { mutableStateOf(false) }
    fun refresh() = refreshScope.launch {
        refreshing = true
        activityViewModel?.getActivitiesForUser(viewModel?.currentUser!!.uid)

    }

    val flowImageAddition = activityViewModel?.addImageToActivityState?.collectAsState()
    val flowImageDelete = activityViewModel?.isImageRemoveFromActivityState?.collectAsState()

    var uploading by remember { mutableStateOf(false) }
    var updateActivity by remember { mutableStateOf(false) }
    var updateDeleteActivity by remember { mutableStateOf(false) }
    var liveActivityDialog by rememberSaveable { mutableStateOf<String?>(null) }
    var liveActivityParticipantsValue by rememberSaveable { mutableStateOf<Int>(0) }
    var deletingImage by remember { mutableStateOf(false) }
    var photo_url by remember { mutableStateOf("") }
    var uploadingError by remember { mutableStateOf(false) }
    flowImageDelete?.value.let {
        when (it) {
            is Response.Success -> {
                deletingImage = false

                activityViewModel?.isImageRemoveFromActivityState?.value = null
                updateDeleteActivity = true

            }
            is Response.Failure -> {
                deletingImage = false

                activityViewModel?.isImageRemoveFromActivityState?.value = null
            }
            is Response.Loading -> {
                deletingImage = true
            }
            else -> {}
        }
    }
    flowImageAddition?.value.let {
        when (it) {
            is Response.Success -> {
                uploading = false
                onEvent(HomeEvent.RemovePhotoFromGallery)
                updateActivity = true
                photo_url = it.data
            }
            is Response.Failure -> {
                uploading = false
                // on failure to add image DISPLAY ERROR BAR
                refreshScope.launch {
                    uploadingError = true
                    delay(3000) // wait for 2 seconds
                    uploadingError = false // set uploadingError to false
                    activityViewModel?.addImageToActivityState?.value =
                        null // set the state to null becasue it is Response.Failure
                }
            }
            is Response.Loading -> {
                uploading = true
            }
            else -> {}
        }
    }
    val state = rememberPullRefreshState(refreshing, ::refresh)
    if (liveActivityDialog != null) {
        Log.d("HOMESCREEN", liveActivityDialog!!)
        Log.d("HOMESCREEN", liveActivityParticipantsValue.toString()!!)

        SocialDialog(
            onDismiss = { liveActivityDialog = null },
            onConfirm = {
                //check if destroy or leave live activity
                if (liveActivityParticipantsValue > 1) {

                    Log.d("HOMESCREEN", "CLICKED")
                    onEvent(
                        HomeEvent.LeaveLiveActivity(
                            liveActivityDialog!!.toString(),
                            UserData.user!!.id
                        )
                    )

                } else {
                    //destroy acitivty passing liveACtivity id
                    onEvent(HomeEvent.DestroyLiveActivity(liveActivityDialog!!.toString()))
                }
                liveActivityDialog = null
            },
            onCancel = { liveActivityDialog = null },
            title = "Leave live activity?",
            info = "Others wont see you in this live activity, if you are the only participant the activity will be destroyed",
            icon = R.drawable.ic_log_out,
            actionButtonText = "Leave"
        )
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .pullRefresh(state)
    ) {
        LazyColumn {
            //Space for the header
            item {
                Spacer(modifier = Modifier.height(64.dp))
            }
            //display of active users
            activeUsersViewModel?.activeUsersListState?.value.let {
                when (it) {
                    is Response.Success -> {
                        item {
                            LazyRow {
                                items(it.data) { liveActivity ->
                                    Spacer(modifier = Modifier.width(24.dp))
                                    ActiveUserItem(
                                        onClick = {
                                            Log.d("HOMESCREEN", liveActivity.toString())
                                            if (liveActivity.participants_profile_pictures.keys.contains(
                                                    UserData.user!!.id
                                                )
                                            ) {
                                                Log.d("HOMESCREEN", liveActivity.id)
                                                liveActivityDialog = liveActivity.creator_id
                                                liveActivityParticipantsValue =
                                                    liveActivity.participants_profile_pictures.size
                                            }
                                        },
                                        profileUrls = liveActivity.participants_profile_pictures,
                                        usernames = liveActivity.participants_usernames
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                }
                            }
                        }
                    }
                    else -> {}
                }
            }
            //divider bettwen active users and activities
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp)
                        .height(1.dp)
                        .background(color = SocialTheme.colors.uiFloated)
                )
            }
            item {
                AnimatedVisibility(
                    visible = uploading,
                    enter = slideInVertically(animationSpec = tween(500, easing = LinearEasing)),
                    exit = scaleOut()
                ) {

                    UploadBar(
                        icon_anim = true,
                        text = "Uploading image",
                        icon = R.drawable.ic_add_photo
                    )
                }
            }
            item {
                AnimatedVisibility(
                    visible = uploadingError,
                    enter = slideInVertically(animationSpec = tween(500, easing = LinearEasing)),
                    exit = scaleOut()
                ) {


                    ErrorBar(
                        icon_anim = true,
                        text = "Error while uploading the image",
                        icon = R.drawable.ic_error
                    )
                }
            }
            item {
                AnimatedVisibility(
                    visible = deletingImage,
                    enter = slideInVertically(animationSpec = tween(500, easing = LinearEasing)),
                    exit = scaleOut()
                ) {

                    ErrorBar(icon_anim = true, text = "Removing image", icon = R.drawable.ic_remove)
                }
            }

            activityViewModel?.activitiesListState?.value.let {
                when (it) {
                    is Response.Success -> {
                        refreshing = false
                        //display activities

                        items(it.data) { item ->
                            if (updateActivity) {
                                if (item.id == homeViewModel.camera_activity_id.value) {
                                    item.pictures[UserData.user!!.id] = photo_url
                                }
                                updateActivity = false
                            }
                            if (updateDeleteActivity) {
                                if (item.id == homeViewModel.camera_activity_id.value) {
                                    item.pictures.remove(UserData.user!!.id)
                                }
                                updateDeleteActivity = false
                            }
                            ActivityItem(
                                activity = item,
                                onEvent = activityEvent,
                                username = item.creator_username,
                                profilePictureUrl = item.creator_profile_picture,
                                timeLeft = item.time_left,
                                title = item.title,
                                description = item.description,
                                date = item.date,
                                liked = item.participants_usernames.containsKey(UserData.user!!.id!!),
                                //todo add the time end
                                timePeriod = item.start_time + " - " + item.end_time,
                                custom_location = item.custom_location,
                                location = item.location, lockPhotoButton = uploading,
                                onLongClick = { onLongClick(item) }
                            )

                        }
                        activitiesExist.value = true
                    }
                    else -> {}
                }
            }
            activityViewModel?.moreActivitiesListState?.value.let {
                when (it) {
                    is Response.Success -> {
                        refreshing = false
                        //display activities
                        Log.d("homescreen", it.data.toString())
                        items(it.data) { item ->
                            if (updateActivity) {
                                if (item.id == homeViewModel.camera_activity_id.value) {
                                    item.pictures[UserData.user!!.id] = photo_url
                                }
                                updateActivity = false

                            }
                            if (updateDeleteActivity) {
                                if (item.id == homeViewModel.camera_activity_id.value) {
                                    item.pictures.remove(UserData.user!!.id)
                                }
                                updateDeleteActivity = false
                            }
                            ActivityItem(
                                activity = item,
                                onEvent = activityEvent,
                                username = item.creator_username,
                                profilePictureUrl = item.creator_profile_picture,
                                timeLeft = item.time_left,
                                title = item.title,
                                description = item.description,
                                date = item.date,
                                liked = item.participants_usernames.containsKey(UserData.user!!.id!!),
                                //todo add the time end
                                timePeriod = item.start_time + " - " + item.end_time,
                                custom_location = item.custom_location,
                                location = item.location,
                                lockPhotoButton = uploading
                            )

                        }
                    }
                    else -> {}
                }
            }
            item {
                LaunchedEffect(true) {
                    if (activitiesExist.value) {
                        activityViewModel?.getMoreActivitiesForUser(UserData.user!!.id)
                    }
                }
            }
            //space for bottom bar
            item {
                Spacer(modifier = Modifier.height(56.dp))
            }
        }

        PullRefreshIndicator(
            refreshing,
            state,
            Modifier
                .align(Alignment.TopCenter)
                .padding(top = 64.dp),
            backgroundColor = SocialTheme.colors.uiBackground,
            contentColor = SocialTheme.colors.textPrimary
        )
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(56.dp)
            .padding(vertical = 12.dp), contentAlignment = Alignment.TopCenter
    ) {
        topBar(
            isDark,
            onEvent = { onEvent(HomeEvent.GoToMemories) },
            picked_screen = "Activities"
        )
    }

}


@Composable
fun topBar(
    isDark: Boolean,
    modifier: Modifier = Modifier,
    onEvent: () -> Unit,
    picked_screen: String
) {
    Row(
        modifier = Modifier
            .width(200.dp)
            .height(48.dp)
            .padding(horizontal = 8.dp), horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (picked_screen.equals("Activities")) {
            cardHighlited(text = "Activities", isDark = isDark)
            Spacer(Modifier.width(6.dp))
            cardnotHighlited(text = "Memories", onEvent = onEvent)
        } else {
            cardnotHighlited(text = "Activities", onEvent = onEvent)
            Spacer(Modifier.width(6.dp))
            cardHighlited(text = "Memories", isDark = isDark)
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun cardnotHighlited(text: String, onEvent: () -> Unit) {
    Card(
        modifier = Modifier
            .width(90.dp)
            .height(40.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors
            (containerColor = Color.Transparent),
        onClick = onEvent
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = text,
                color = Color(0xffB0B0B0),
                style = com.example.socialk.ui.theme.Typography.body2,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun cardHighlited(isDark: Boolean, text: String) {
    Column(
        modifier = Modifier
            .height(40.dp)
            .width(90.dp)
            .padding(6.dp),
        horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = text,
            color = if (isDark) Color.White else Color(0xFF25232A),
            modifier = Modifier,
            style = com.example.socialk.ui.theme.Typography.body1,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .background(color = if (isDark) Color.White else Color(0xFF25232A))
                .height(1.dp)
                .width(40.dp)
        )
    }

}



