package com.example.socialk.map

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.DisplayMetrics
import android.util.Log
import android.util.TypedValue
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.animation.*
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.*
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import coil.ImageLoader
import coil.request.ErrorResult
import coil.request.ImageRequest
import coil.request.SuccessResult
import coil.transform.CircleCropTransformation
import com.bumptech.glide.Glide
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.CustomTarget
import com.example.socialk.ButtonLink
import com.example.socialk.Destinations
import com.example.socialk.R
import com.example.socialk.components.*
import com.example.socialk.create.CreateActivityButton
import com.example.socialk.di.ActiveUsersViewModel
import com.example.socialk.di.ActivityViewModel
import com.example.socialk.di.ChatViewModel
import com.example.socialk.di.UserViewModel
import com.example.socialk.home.*
import com.example.socialk.model.Activity
import com.example.socialk.model.Response
import com.example.socialk.model.User
import com.example.socialk.model.UserData
import com.example.socialk.signinsignup.AuthViewModel
import com.example.socialk.ui.theme.Inter
import com.example.socialk.ui.theme.SocialTheme
import com.google.accompanist.systemuicontroller.SystemUiController
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.dynamiclinks.ktx.androidParameters
import com.google.firebase.dynamiclinks.ktx.dynamicLink
import com.google.firebase.dynamiclinks.ktx.dynamicLinks
import com.google.firebase.ktx.Firebase
import com.google.maps.android.compose.*
import kotlinx.coroutines.*
import okhttp3.internal.wait
import coil.compose.rememberImagePainter as rememberImagePainter1

sealed class MapEvent {
    object GoToProfile : MapEvent()
    object GoToEditProfile : MapEvent()
    object GoToChats : MapEvent()
    object AddPeople : MapEvent()
    object GoToGroup : MapEvent()
    object LogOut : MapEvent()
    object GoToHome : MapEvent()
    object GoToSettings : MapEvent()
    class GoToUserProfile(val user: User) : MapEvent()
    class ReportActivity(val activity_id: String) : MapEvent()
    class LeaveActivity(val activity_id: String,val user_id: String) : MapEvent()
    class HideActivity(val activity_id: String,val user_id: String) : MapEvent()
    class SendRequest(val activity: Activity) : MapEvent()

    object AskForPermission : MapEvent()
    object BackPressed : MapEvent()
    object GoToTrending : MapEvent()
    object GoToInfo : MapEvent()
    object GoToHelp : MapEvent()
    object GoToCalendar : MapEvent()
    object GoToCreated : MapEvent()
    object GoToBookmarked : MapEvent()
    class GoToCreateActivity(val latLng: LatLng) : MapEvent()

    class DestroyLiveActivity(val id: String) : MapEvent()
    class LeaveLiveActivity(val activity_id: String, val user_id: String) : MapEvent()
    class DisplayPicture(val photo_url: String, val activity_id: String) : MapEvent()
    class GoToFriendsPicker(val activity: Activity) : MapEvent()
    object RemovePhotoFromGallery : MapEvent()
    class GoToMap(latlng: String) : MapEvent() {
        val latlng = latlng
    }

    class GoToChat(activity: Activity) : MapEvent() {
        val activity = activity
    }

    class ActivityLiked(activity: Activity) : MapEvent() {
        val activity = activity
    }

    class ActivityUnLiked(activity: Activity) : MapEvent() {
        val activity = activity
    }

    class GoToProfileWithID(user_id: String) : MapEvent() {
        val user_id = user_id
    }
}/*
fun customShape() =  object : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        return Outline.Rectangle(Rect(0f,0f,500f /* width */, 500f /* height */))
    }
}
*/

@OptIn(ExperimentalMaterialApi::class, ExperimentalAnimationApi::class)
@Composable
fun MapScreen(
    homeViewModel: HomeViewModel,
    systemUiController: SystemUiController,
    latLngInitial: LatLng?,
    activityViewModel: ActivityViewModel,
    onEvent: (MapEvent) -> Unit,
    bottomNavEvent: (Destinations) -> Unit,
    viewModel: MapViewModel,
    locationCallback: LocationCallback,
    activeUsersViewModel: ActiveUsersViewModel?,
    chatViewModel: ChatViewModel,
    authViewModel: AuthViewModel?,
    userViewModel: UserViewModel
) {

    //set status bar TRANSPARENT
    SideEffect {
        systemUiController.setStatusBarColor(color = androidx.compose.ui.graphics.Color.Transparent)
        systemUiController.setNavigationBarColor(color = androidx.compose.ui.graphics.Color.Transparent)
    }
    val backPressedDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
    val scaffoldState = rememberScaffoldState()
    val couroutineScope = rememberCoroutineScope()
    val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            couroutineScope.launch {
                scaffoldState.drawerState.close()

            }
        }
    }

    DisposableEffect(backPressedDispatcher) {
        backPressedDispatcher?.addCallback(onBackPressedCallback)

        onDispose {
            onBackPressedCallback.remove()
        }
    }
    val contextF= LocalContext.current

    //dialog for activity linked display
    val showDialogState: Boolean by homeViewModel?.showDialog!!.collectAsState()
    var currentLocation: LatLng? by remember { mutableStateOf(null) }
    var location_picked_flow = viewModel.locations_picked.collectAsState()
    var isMapLoaded by remember { mutableStateOf(false) }
    var displayCreateButton by remember { mutableStateOf(false) }
    /*if (ActivityCompat.checkSelfPermission(
            LocalContext.current,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
            LocalContext.current,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        // TODO: Consider calling
        //    ActivityCompat#requestPermissions
        // here to request the missing permissions, and then overriding
        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
        //                                          int[] grantResults)
        // to handle the case where the user grants the permission. See the documentation
        // for ActivityCompat#requestPermissions for more details.
        return
    }else{
    }*/

    val permission_flow = viewModel.granted_permission.collectAsState()
    val location_flow = viewModel.location.collectAsState()
    var bottomSheetState = rememberModalBottomSheetState(ModalBottomSheetValue.HalfExpanded)
    var bottomSheetType by remember {
        mutableStateOf("settings")
    }
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


    var properties by remember {
        mutableStateOf(MapProperties(mapType = MapType.NORMAL))
    }
    var selectedFitness by remember { mutableStateOf(false) }
    var selectedCreative by remember { mutableStateOf(false) }
    var selectedMusic by remember { mutableStateOf(false) }
    var selectedGames by remember { mutableStateOf(false) }
    var selectedSocial by remember { mutableStateOf(false) }
    var selectedEducation by remember { mutableStateOf(false) }
    var selectedVolunteer by remember { mutableStateOf(false) }
    var selectedTravel by remember { mutableStateOf(false) }
    var selectedFood by remember { mutableStateOf(false) }
    var selectedWellness by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    var reportActivityDialog by rememberSaveable { mutableStateOf(false) }
    var participantsActivityDialog by rememberSaveable { mutableStateOf(false) }
    var hideActivityDialog by rememberSaveable { mutableStateOf(false) }
    var leaveActivityDialog by rememberSaveable { mutableStateOf(false) }
    var openFilterDialog by rememberSaveable { mutableStateOf(false) }

    Surface(
        Modifier.background(color = SocialTheme.colors.uiBackground),
        color = SocialTheme.colors.uiBackground
    ) {
        Scaffold(
            scaffoldState = scaffoldState,
            drawerBackgroundColor = SocialTheme.colors.uiBackground,
            drawerScrimColor = Color.Black.copy(alpha = 0.3f),
            drawerContent = {
                DrawerContent(onEvent = onEvent, activityViewModel = activityViewModel)
            },
            drawerGesturesEnabled = false,
        ) { paddingValues ->

            Box(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
            ) {
                MapBottomDialog(
                    modifier = Modifier,
                    openFilter={      openFilterDialog = true},
                    state = bottomSheetState,
                    type = bottomSheetType,
                    onEvent = onEvent,
                    activeUsersViewModel = activeUsersViewModel,
                    activityViewModel = activityViewModel,
                    chatViewModel = chatViewModel,
                    viewModel = authViewModel,
                    activityEvent = {
                        when (it) {
                            is ActivityEvent.OpenActivityChat -> {
                                Log.d("HomeScreen", "chat")
                                onEvent(MapEvent.GoToChat(it.activity))
                            }
                            is ActivityEvent.ActivityLiked -> {
                                Log.d("HomeScreen", "like")
                                onEvent(MapEvent.ActivityLiked(it.activity))
                            }
                            is ActivityEvent.ActivityUnLiked -> {
                                Log.d("HomeScreen", "dislike")
                                onEvent(MapEvent.ActivityUnLiked(it.activity))

                            }
                            is ActivityEvent.SendRequest -> {
                                onEvent(MapEvent.SendRequest(it.activity))
                            }
                            is ActivityEvent.GoToMap -> {
                                onEvent(MapEvent.GoToMap(latlng = it.latlng))

                            }
                            is ActivityEvent.GoToProfile -> {
                                onEvent(MapEvent.GoToProfileWithID(user_id = it.user_id))

                            }
                            is ActivityEvent.DisplayPicture -> {
                                onEvent(MapEvent.DisplayPicture(it.photo_url, it.activity_id))
                            }
                            is ActivityEvent.ReportActivity -> {
                                viewModel.setActivityID(it.activity_id)
                                reportActivityDialog = true
                            }
                            is ActivityEvent.DisplayParticipants -> {
                                viewModel.setActivityID(it.activity.id)
                                userViewModel.getActivityUsers(it.activity.id)
                                participantsActivityDialog = true

                            }
                            is ActivityEvent.GoToFriendsPicker -> {
                                onEvent(MapEvent.GoToFriendsPicker(it.activity))

                            }
                            is ActivityEvent.HideActivity -> {
                                viewModel.setActivityID(it.activity_id)
                                hideActivityDialog = true

                            }
                            is ActivityEvent.LeaveActivity -> {
                                viewModel.setActivityID(it.activity.id)
                                leaveActivityDialog = true
                            }
                            else -> {}
                        }

                    }
                ) { isExpanded ->
                    Log.d("MapScreen", "re compose ")
                    Box(
                        Modifier
                            .fillMaxSize()
                            .background(color = SocialTheme.colors.uiBackground)
                    ) {
                        if (!isMapLoaded) {
                            SocialTheme {
                                AnimatedVisibility(
                                    modifier = Modifier
                                        .matchParentSize(),
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

                        permission_flow.value.let {
                            if (it) {
                                location_flow.value.let { location ->
                                    if (location != null) {
                                        viewModel.setLocation(
                                            LatLng(
                                                location?.latitude!!,
                                                location?.longitude!!
                                            )
                                        )

                                        if (currentLocation == null) {
                                            currentLocation =
                                                LatLng(location?.latitude!!, location?.longitude!!)
                                            Log.d("Mapfragment", " dats")
                                            if (latLngInitial == null) {
                                                cameraPositionState.position =
                                                    CameraPosition.fromLatLngZoom(
                                                        currentLocation!!,
                                                        13f
                                                    )
                                            } else {
                                                cameraPositionState.position =
                                                    CameraPosition.fromLatLngZoom(
                                                        latLngInitial!!,
                                                        13f
                                                    )
                                            }

                                        } else {
                                            currentLocation =
                                                LatLng(location?.latitude!!, location?.longitude!!)
                                        }

                                        Log.d("MapScreen", "got location")
                                    }
                                }
                                if (currentLocation == null) {
                                    SocialDialog(
                                        onDismiss = { onEvent(MapEvent.GoToHome) },
                                        onConfirm = { },
                                        onCancel = { onEvent(MapEvent.GoToHome) },
                                        title = "Turn on location",
                                        info = "To access the map please turn on you location",
                                        icon = R.drawable.ic_location_24,
                                        actionButtonText = "",
                                        actionButtonTextColor = SocialTheme.colors.textInteractive
                                    )
                                } else {
                                    GoogleMap(
                                        Modifier.fillMaxSize(), cameraPositionState,
                                        properties = properties, onMapLoaded = {
                                            isMapLoaded = true
                                        }, onMapLongClick = { latlng ->
                                            viewModel.setLocationPicked(latlng)
                                            //add marker to the map by adding it to the list of markers that should be displayed'

                                        }, onMapClick = {
                                            viewModel.setLocationPicked(null)
                                        },
                                        uiSettings = uiSettings
                                    ) {

                                        location_flow.value.let {
                                            MarkerInfoWindow(
                                                state = MarkerState(
                                                    position = it!!
                                                ), icon = loadIcon(contextF ,   UserData.user?.pictureUrl!!, R.drawable.ic_person)
                                            ) {
                                                Column() {
                                                    Card(shape = RoundedCornerShape(6.dp)) {
                                                        Box(
                                                            modifier = Modifier
                                                                .background(color = SocialTheme.colors.uiBackground)
                                                                .padding(6.dp)
                                                        ) {
                                                            Text(
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
                                        activityViewModel.activitiesListState.value.let {
                                            when (it) {
                                                is Response.Success -> {
                                                    it.data.forEach { activity ->
                                                        if (activity.location.isNotEmpty()) {
                                                            val values =
                                                                activity.location.split("/")
                                                            val latLng = LatLng(
                                                                values.get(0).toDouble(),
                                                                values.get(1).toDouble()
                                                            )
                                                            var activityIcon: BitmapDescriptor? by remember { mutableStateOf(null) }

                                                            // load the icon asynchronously
                                                            LaunchedEffect( activity.creator_profile_picture) {
                                                                withContext(Dispatchers.IO) {
                                                                    activityIcon = loadIcon(context ,  activity.creator_profile_picture, R.drawable.ic_person)
                                                                }
                                                            }
                                                            MarkerInfoWindow(
                                                                zIndex = 0.5f,
                                                                state = MarkerState(
                                                                    position = latLng
                                                                ),
                                                                icon =activityIcon
                                                            ) {
                                                                MapActivityPreview(
                                                                    bottomSheetActivity = activity,
                                                                    onEvent = {}
                                                                )

                                                            }
                                                        }

                                                    }

                                                }
                                                is Response.Loading -> {}
                                                is Response.Failure -> {}
                                            }
                                        }
                                        activityViewModel.moreActivitiesListState.value.let {
                                            when (it) {
                                                is Response.Success -> {
                                                    it.data.forEach { activity ->
                                                        if (activity.location.isNotEmpty()) {
                                                            val values =
                                                                activity.location.split("/")
                                                            val latLng = LatLng(
                                                                values.get(0).toDouble(),
                                                                values.get(1).toDouble()
                                                            )
                                                            var moreActivityIcon: BitmapDescriptor? by remember { mutableStateOf(null) }

                                                            // load the icon asynchronously
                                                            LaunchedEffect( activity.creator_profile_picture) {
                                                                withContext(Dispatchers.IO) {
                                                                    moreActivityIcon = loadIcon(context ,  activity.creator_profile_picture, R.drawable.ic_person)
                                                                }
                                                            }
                                                            MarkerInfoWindow(
                                                                state = MarkerState(
                                                                    position = latLng
                                                                ),
                                                                icon =moreActivityIcon
                                                            ) {
                                                                MapActivityPreview(
                                                                    bottomSheetActivity = activity,
                                                                    onEvent = {}
                                                                )
                                                            }
                                                        }

                                                    }

                                                }
                                                is Response.Loading -> {}
                                                is Response.Failure -> {}
                                            }
                                        }

                                        activityViewModel.closestActivitiesListState.value.let {
                                            when (it) {
                                                is Response.Success -> {
                                                    it.data.forEach { activity ->
                                                        if (activity.location.isNotEmpty()) {
                                                            val values =
                                                                activity.location.split("/")
                                                            val latLng = LatLng(
                                                                values.get(0).toDouble(),
                                                                values.get(1).toDouble()
                                                            )
                                                            var activityIcon: BitmapDescriptor? by remember { mutableStateOf(null) }

                                                            // load the icon asynchronously
                                                            LaunchedEffect( activity.creator_profile_picture) {
                                                                withContext(Dispatchers.IO) {
                                                                    activityIcon = loadIcon(context ,  activity.creator_profile_picture, R.drawable.ic_person)
                                                                }
                                                            }
                                                            MarkerInfoWindow(
                                                                zIndex = 0.5f,
                                                                state = MarkerState(
                                                                    position = latLng
                                                                ),
                                                                icon =activityIcon
                                                            ) {
                                                                MapActivityPreview(
                                                                    bottomSheetActivity = activity,
                                                                    onEvent = {}
                                                                )

                                                            }
                                                        }

                                                    }

                                                }
                                                is Response.Loading -> {}
                                                is Response.Failure -> {}
                                            }
                                        }
                                        activityViewModel.moreclosestActivitiesListState.value.let {
                                            when (it) {
                                                is Response.Success -> {
                                                    it.data.forEach { activity ->
                                                        if (activity.location.isNotEmpty()) {
                                                            val values =
                                                                activity.location.split("/")
                                                            val latLng = LatLng(
                                                                values.get(0).toDouble(),
                                                                values.get(1).toDouble()
                                                            )
                                                            var moreActivityIcon: BitmapDescriptor? by remember { mutableStateOf(null) }

                                                            // load the icon asynchronously
                                                            LaunchedEffect( activity.creator_profile_picture) {
                                                                withContext(Dispatchers.IO) {
                                                                    moreActivityIcon = loadIcon(context ,  activity.creator_profile_picture, R.drawable.ic_person)
                                                                }
                                                            }
                                                            MarkerInfoWindow(
                                                                state = MarkerState(
                                                                    position = latLng
                                                                ),
                                                                icon =moreActivityIcon
                                                            ) {
                                                                MapActivityPreview(
                                                                    bottomSheetActivity = activity,
                                                                    onEvent = {}
                                                                )
                                                            }
                                                        }

                                                    }

                                                }
                                                is Response.Loading -> {}
                                                is Response.Failure -> {}
                                            }
                                        }


                                        location_picked_flow.value.let {
                                            if (it == null) {
                                            } else {

                                                MarkerInfoWindow(
                                                    state = MarkerState(
                                                        position = it!!
                                                    ), icon = loadIcon(contextF ,   UserData.user?.pictureUrl!!, R.drawable.ic_person)
                                                ) {
                                                    Column() {
                                                        Card(shape = RoundedCornerShape(6.dp)) {
                                                            Box(
                                                                modifier = Modifier
                                                                    .background(color = SocialTheme.colors.uiBackground)
                                                                    .padding(6.dp)
                                                            ) {
                                                                Text(
                                                                    text = "Picked location",
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

                                    //PROFILE PICTURE
                                    Box(
                                        modifier = Modifier
                                            .align(Alignment.TopEnd)
                                            .padding(end = 24.dp, top = 48.dp)
                                    ) {
                                        Row() {
                                            Spacer(Modifier.width(24.dp))
                                            androidx.compose.material.Card(
                                                modifier = Modifier
                                                    .width(48.dp)
                                                    .height(48.dp),
                                                onClick = {
                                                    couroutineScope.launch {
                                                        scaffoldState.drawerState.open()
                                                    }
                                                },
                                                shape = RoundedCornerShape(12.dp),
                                                backgroundColor =  SocialTheme.colors.uiBackground,
                                                elevation = 4.dp
                                            ) {
                                                Box(
                                                    modifier = Modifier
                                                        .fillMaxSize()
                                                        .background(color = Color.Transparent),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Icon(
                                                        modifier = Modifier.background(Color.Transparent),
                                                        painter = painterResource(id = R.drawable.ic_menu),
                                                        contentDescription = null,
                                                        tint =  SocialTheme.colors.textPrimary
                                                    )
                                                }
                                            }
                                            Spacer(Modifier.width(24.dp))
                                            androidx.compose.material.Card(
                                                modifier = Modifier
                                                    .width(48.dp)
                                                    .height(48.dp),
                                                onClick = {
                                                    if (viewModel.location.value != null) {
                                                        Log.d("getClosestActivities", "call")
                                                        activityViewModel.getMoreClosestActivities(
                                                            viewModel.location.value!!.latitude,
                                                            viewModel.location.value!!.longitude,
                                                            50.0*1000.0
                                                        )
                                                    }
                                                },
                                                shape = RoundedCornerShape(12.dp),
                                                backgroundColor = SocialTheme.colors.uiBackground,
                                                elevation = 4.dp
                                            ) {
                                                Box(
                                                    modifier = Modifier
                                                        .fillMaxSize()
                                                        .background(color = Color.Transparent),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Icon(
                                                        modifier = Modifier.background(Color.Transparent),
                                                        painter = painterResource(id = R.drawable.ic_cloud_sync),
                                                        contentDescription = null,
                                                        tint =  SocialTheme.colors.textPrimary
                                                    )
                                                }
                                            }
                                            Spacer(modifier = Modifier.weight(1f))
                                            androidx.compose.material.Card(
                                                modifier =
                                                Modifier
                                                    .width(48.dp)
                                                    .height(48.dp),
                                                shape = RoundedCornerShape(12.dp),
                                                onClick = {
                                                    if (currentLocation != null) {
                                                        pointToCurrentLocation(
                                                            cameraPositionState,
                                                            currentLocation!!
                                                        )
                                                    } else {
                                                        Toast.makeText(
                                                            context,
                                                            "Current location unavailable",
                                                            Toast.LENGTH_SHORT
                                                        ).show()
                                                    }
                                                },
                                                backgroundColor =  SocialTheme.colors.uiBackground,
                                                elevation = 4.dp
                                            ) {
                                                Box(
                                                    modifier = Modifier.fillMaxSize(),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Icon(
                                                        painter = painterResource(id = R.drawable.ic_current_location),
                                                        contentDescription = null,
                                                        tint = SocialTheme.colors.textPrimary
                                                    )

                                                }
                                            }
                                            Spacer(modifier = Modifier.width(12.dp))
                                            androidx.compose.material.Card(
                                                modifier =
                                                Modifier
                                                    .width(48.dp)
                                                    .height(48.dp),
                                                shape = RoundedCornerShape(12.dp),
                                                onClick = { onEvent(MapEvent.GoToChats) },
                                                backgroundColor =  SocialTheme.colors.uiBackground,
                                                elevation = 4.dp
                                            ) {
                                                Box(
                                                    modifier = Modifier.fillMaxSize(),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Icon(
                                                        painter = painterResource(id = R.drawable.instagram_chat_icon_isolated_on_transparent_background_png),
                                                        contentDescription = null,
                                                        tint = SocialTheme.colors.textPrimary
                                                    )
                                                }
                                            }
                                            /*Spacer(modifier = Modifier.width(12.dp))
                                            androidx.compose.material.Card(
                                                shape = RoundedCornerShape(
                                                    100.dp
                                                ),
                                                onClick = { onEvent(MapEvent.GoToProfile) },
                                                elevation = 4.dp
                                            ) {
                                                AsyncImage(
                                                    model = ImageRequest.Builder(LocalContext.current)
                                                        .data(UserData.user!!.pictureUrl)
                                                        .crossfade(true)
                                                        .build(),
                                                    placeholder = painterResource(R.drawable.ic_person),
                                                    contentDescription = "image sent",
                                                    contentScale = ContentScale.Crop,
                                                    modifier = Modifier
                                                        .size(48.dp)
                                                        .background(color = SocialTheme.colors.uiBackground)
                                                        .clip(CircleShape)
                                                )
                                            }*/
                                        }


                                    }



                                    location_picked_flow.value.let {
                                        if (it != null) {
                                            displayCreateButton = true
                                        } else {
                                            displayCreateButton = false
                                        }
                                    }
                                    if (isExpanded) {
                                        displayCreateButton = false
                                    }
                                    Box(
                                        modifier = Modifier
                                            .align(
                                                Alignment.BottomEnd
                                            )
                                            .padding(bottom = 160.dp, end = 0.dp)
                                    ) {

                                        AnimatedVisibility(
                                            visible = displayCreateButton,
                                        ) {

                                        CreateActivityButton(
                                            modifier = Modifier.width(200.dp),
                                            text = "",
                                            onClick = {
                                                onEvent(
                                                    MapEvent.GoToCreateActivity(
                                                        location_picked_flow.value!!
                                                    )
                                                )
                                            },
                                            icon = R.drawable.ic_right,
                                        ){

                                                androidx.compose.material3.Text(
                                                    text = "Add location",
                                                    style = TextStyle(
                                                        color = Color.White,
                                                        fontSize = 16.sp,
                                                        fontFamily = Inter,
                                                        fontWeight = FontWeight.ExtraBold
                                                    )
                                                )


                                        }
                                        }

                                    }
                                }

                            } else {
                                SocialDialog(
                                    onDismiss = { onEvent(MapEvent.GoToHome) },
                                    onConfirm = { onEvent(MapEvent.AskForPermission) },
                                    onCancel = { onEvent(MapEvent.GoToHome) },
                                    title = "Location access",
                                    info = "To access the map please share your location",
                                    icon = R.drawable.ic_location_24,
                                    actionButtonText = "Share",
                                    actionButtonTextColor = SocialTheme.colors.textInteractive
                                )
                            }
                        }

                    }

                }

            }

        }
    }

    activityViewModel?.isActivityAddedState?.value.let {
        when (it) {
            is Response.Loading -> Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
            is Response.Success -> {
                Toast.makeText(LocalContext.current, "Activity created", Toast.LENGTH_SHORT).show()
                activityViewModel.activityAdded()
            }
            is Response.Failure -> Box(modifier = Modifier.fillMaxSize()) {
                androidx.compose.material3.Text(text = "FAILURE", fontSize = 50.sp)
            }
            else -> {}
        }
    }
    userViewModel?.userState?.value.let { event ->
        when (event) {
            is Response.Success -> {
                if (event.data != null) {
                    onEvent(MapEvent.GoToUserProfile(event.data))
                }
            }
            is Response.Loading -> {

            }
            is Response.Failure -> {

            }
            else -> {}
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
        })

    AnimatedVisibility(visible = reportActivityDialog, enter = scaleIn(), exit = scaleOut()) {
        var selectedOffensiveLangue by remember{ mutableStateOf(false) }
        var selectedInappropraiteImages by remember{ mutableStateOf(false) }
        var selectedIllegalActivites by remember{ mutableStateOf(false) }
        var selectedPersonalAttacks by remember{ mutableStateOf(false) }
        var selectedSpam by remember{ mutableStateOf(false) }
        CustomSocialDialog(title = "Report activity",
            info = "We take inappropriate activity seriously and appreciate your help in keeping our community safe and respectful. If you have come across any activity that you feel violates our community guidelines or is otherwise inappropriate," +
                    " please let us know.",
            icon = R.drawable.ic_report,
            onDismiss = { reportActivityDialog = false },
            onConfirm = {},
            onCancel = { reportActivityDialog = false }, actionButtonText = "Send", actionButtonTextColor = SocialTheme.colors.iconInteractive){
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)) {

                ReportItem(title="Offensive language"
                    ,description="Any activity containing hate speech, discriminatory language, or other language that is derogatory, abusive, or offensive to any group of people should be reported.",
                    selected=selectedOffensiveLangue, checkedChange = {
                        selectedOffensiveLangue = !selectedOffensiveLangue
                    })
                Spacer(Modifier.height(8.dp))
                ReportItem(title="Inappropriate Images"
                    ,description="Any images that are sexually explicit, violent, or graphic in nature should be reported.",
                    selected=selectedInappropraiteImages, checkedChange = {
                        selectedInappropraiteImages = !selectedInappropraiteImages
                    })
                Spacer(Modifier.height(8.dp))
                ReportItem(title="Illegal Activities"
                    ,description="Any activities that promote or depict illegal activities, such as drug use or violence, should be reported.",
                    selected=selectedIllegalActivites, checkedChange = {
                        selectedIllegalActivites = !selectedIllegalActivites
                    })
                Spacer(Modifier.height(8.dp))
                ReportItem(title="Personal Attacks"
                    ,description="Any activities that contain personal attacks, harassment, or bullying should be reported.",
                    selected=selectedPersonalAttacks, checkedChange = {
                        selectedPersonalAttacks = !selectedPersonalAttacks
                    })
                Spacer(Modifier.height(8.dp))
                ReportItem(title="Spam"
                    ,description="Any activities that are repetitive, irrelevant, or unsolicited should be reported.",
                    selected=selectedSpam, checkedChange = {
                        selectedSpam = !selectedSpam
                    })
                Spacer(modifier = Modifier.height(24.dp))
                Row(modifier = Modifier.fillMaxWidth(),horizontalArrangement = Arrangement.End) {
                    ClickableText(text = AnnotatedString("Cancel")
                        , style = TextStyle(color= SocialTheme.colors.textPrimary,
                            fontFamily = Inter , fontWeight = FontWeight.Medium , fontSize = 14.sp
                        ), onClick = { reportActivityDialog=false })
                    Spacer(modifier = Modifier.width(24.dp))
                    ClickableText(text = AnnotatedString("Send report"), style = TextStyle(color=SocialTheme.colors.iconInteractive,
                        fontFamily = Inter , fontWeight = FontWeight.Medium , fontSize = 14.sp
                    ), onClick = {
                        if(selectedIllegalActivites ||selectedSpam||selectedInappropraiteImages||selectedOffensiveLangue||selectedPersonalAttacks){

                            onEvent(MapEvent.ReportActivity(viewModel.activityID.value!!))
                            viewModel.resetActivityID()
                            reportActivityDialog = false
                        }
                    })
                }
            }
        }
    }
    AnimatedVisibility(visible = hideActivityDialog, enter = scaleIn(), exit = scaleOut()) {
        SocialDialog(title = "Hide activity",
            info = "Confirm hiding the activity from your feed. Please note that hiding an activity is irreversible and once hidden, you will have to be invited again to see it.",
            icon = R.drawable.ic_visibility_off,
            onDismiss = { hideActivityDialog = false },
            onConfirm = {onEvent(MapEvent.HideActivity(viewModel.activityID.value!!,UserData.user!!.id))
                viewModel.resetActivityID()
                hideActivityDialog = false},
            onCancel = { hideActivityDialog = false }, actionButtonText = "Hide")
    }
    AnimatedVisibility(visible = leaveActivityDialog, enter = scaleIn(), exit = scaleOut()) {
        SocialDialog(title = "Leave activity",
            info = " If you no longer wish to participate in this activity, confirm leaving it. You will be removed from participants list.",
            icon = R.drawable.ic_log_out,
            onDismiss = { leaveActivityDialog = false },
            onConfirm = {onEvent(MapEvent.LeaveActivity(viewModel.activityID.value!!,UserData.user!!.id))
                        viewModel.resetActivityID()
                leaveActivityDialog = false
                        },
            onCancel = { leaveActivityDialog = false }, actionButtonText = "Leave")
    }
    AnimatedVisibility(visible = openFilterDialog, enter = scaleIn(), exit = scaleOut()) {
        var tags = ArrayList<String>()
        Dialog(onDismissRequest ={openFilterDialog=false}) {
            Card(shape= RoundedCornerShape(16.dp)) {
                Box(modifier = Modifier
                    .background(color = SocialTheme.colors.uiBackground)
                    .padding(24.dp)) {
                    if (selectedFitness && !tags.contains(Category.SPORTS.label)) tags.add(Category.SPORTS.label) else tags.remove(Category.SPORTS.label)
                    if (selectedFood && !tags.contains(Category.FOOD.label)) tags.add(Category.FOOD.label) else tags.remove(Category.FOOD.label)
                    if (selectedMusic && !tags.contains(Category.MUSIC.label)) tags.add(Category.MUSIC.label) else tags.remove(Category.MUSIC.label)
                    if (selectedWellness && !tags.contains(Category.WELLNESS.label)) tags.add(Category.WELLNESS.label) else tags.remove(Category.WELLNESS.label)
                    if (selectedTravel && !tags.contains(Category.TRAVEL.label)) tags.add(Category.TRAVEL.label) else tags.remove(Category.TRAVEL.label)

                    if (selectedCreative && !tags.contains(Category.CREATIVE.label)) tags.add(Category.CREATIVE.label) else tags.remove(Category.CREATIVE.label)

                    if (selectedGames && !tags.contains(Category.GAMES.label)) tags.add(Category.GAMES.label) else tags.remove(Category.GAMES.label)

                    if (selectedSocial && !tags.contains(Category.SOCIAL.label)) tags.add(Category.SOCIAL.label) else tags.remove(Category.SOCIAL.label)

                    if (selectedEducation && !tags.contains(Category.EDUCATION.label)) tags.add(Category.EDUCATION.label) else tags.remove(Category.EDUCATION.label)

                    if (selectedVolunteer && !tags.contains(Category.VOLUNTEER.label)) tags.add(Category.VOLUNTEER.label) else tags.remove(Category.VOLUNTEER.label)

                    Column() {
                        Column(
                            Modifier
                                .padding(horizontal = 0.dp)
                                .verticalScroll(rememberScrollState())) {
                            Row(verticalAlignment = Alignment.CenterVertically){
                                Icon(painter= painterResource(id = R.drawable.ic_tag_36), contentDescription = null,tint=SocialTheme.colors.textPrimary)
                                Spacer(Modifier.width(3.dp))
                                Text(text = "Filter by tags",style= TextStyle(fontFamily = Inter, fontSize = 16.sp, fontWeight = FontWeight.SemiBold),color=SocialTheme.colors.textPrimary)
                                Spacer(Modifier.weight(1f))
                                ClickableText(text = AnnotatedString("Clear filters"),style= TextStyle(fontFamily = Inter, fontSize = 14.sp, fontWeight = FontWeight.SemiBold,color=Color( 0xFF3773FF)), onClick = {activityViewModel.setTags(null)
                                openFilterDialog=false})

                            }
                            Spacer(Modifier.height(12.dp))

                            TagLabelItem(
                                title =  Category.SPORTS.label,
                                icon = R.drawable.ic_fitness,
                                selected = selectedFitness,
                                checkedChange = { selectedFitness = !selectedFitness })
                            Spacer(Modifier.height(6.dp))
                            TagLabelItem(
                                title =  Category.CREATIVE.label,
                                icon = R.drawable.ic_creative,
                                selected = selectedCreative,
                                checkedChange = { selectedCreative = !selectedCreative })
                            Spacer(Modifier.height(6.dp))

                            TagLabelItem(
                                title = Category.MUSIC.label,
                                icon = R.drawable.ic_piano,
                                selected = selectedMusic,
                                checkedChange = { selectedMusic = !selectedMusic })
                            Spacer(Modifier.height(6.dp))

                            TagLabelItem(
                                title = Category.GAMES.label,
                                icon = R.drawable.ic_games,
                                selected = selectedGames,
                                checkedChange = { selectedGames = !selectedGames })
                            Spacer(Modifier.height(6.dp))

                            TagLabelItem(
                                title = Category.SOCIAL.label,
                                icon = R.drawable.ic_celebration,
                                selected = selectedSocial,
                                checkedChange = { selectedSocial = !selectedSocial })
                            Spacer(Modifier.height(6.dp))

                            TagLabelItem(
                                title = Category.EDUCATION.label,
                                icon = R.drawable.ic_book,
                                selected = selectedEducation,
                                checkedChange = { selectedEducation = !selectedEducation })
                            Spacer(Modifier.height(6.dp))

                            TagLabelItem(
                                title = Category.VOLUNTEER.label,
                                icon = R.drawable.ic_volounteer,
                                selected = selectedVolunteer,
                                checkedChange = { selectedVolunteer = !selectedVolunteer })
                            Spacer(Modifier.height(6.dp))

                            TagLabelItem(
                                title = Category.TRAVEL.label,
                                icon = R.drawable.ic_travel,
                                selected = selectedTravel,
                                checkedChange = { selectedTravel = !selectedTravel })
                            Spacer(Modifier.height(6.dp))

                            TagLabelItem(
                                title = Category.FOOD.label,
                                icon = R.drawable.ic_food,
                                selected = selectedFood,
                                checkedChange = { selectedFood = !selectedFood })
                            Spacer(Modifier.height(6.dp))

                            TagLabelItem(
                                title = Category.WELLNESS.label,
                                icon = R.drawable.ic_wellness,
                                selected = selectedWellness,
                                checkedChange = { selectedWellness = !selectedWellness })

                        }
                        Spacer(Modifier.height(16.dp))
                        Row(modifier = Modifier.fillMaxWidth(),horizontalArrangement = Arrangement.End) {
                            ClickableText(text = AnnotatedString("Cancel")
                                , style = TextStyle(color= SocialTheme.colors.textPrimary,
                                    fontFamily = Inter , fontWeight = FontWeight.Medium , fontSize = 14.sp
                                ), onClick = {openFilterDialog=false  })
                            Spacer(modifier = Modifier.width(24.dp))
                            ClickableText(text = AnnotatedString("Confirm"), style = TextStyle(color=if(tags.isEmpty()) SocialTheme.colors.textInteractive else Color( 0xFF3773FF),
                                fontFamily = Inter , fontWeight = FontWeight.Medium , fontSize = 14.sp
                            ), onClick = {
                                if(tags.isNotEmpty()){
                                    activityViewModel.getClosestFilteredActivities(activityViewModel.location.value!!.latitude,activityViewModel.location.value!!.longitude,tags,50.0*1000.0)
                                    activityViewModel.setTags(tags)
                                }


                            openFilterDialog=false})
                        }
                    }


                }
            }
        }
    }
    AnimatedVisibility(visible = participantsActivityDialog, enter = scaleIn(), exit = scaleOut()) {
        Dialog(onDismissRequest ={participantsActivityDialog=false}) {
            Card(shape= RoundedCornerShape(16.dp)) {
                Box(modifier = Modifier
                    .background(color = SocialTheme.colors.uiBackground)
                    .padding(24.dp)) {
                    LazyColumn {
                        userViewModel.activityUsersState.value.let {
                            when (it) {
                                is Response.Success -> {
                                    items(it.data) { user ->
                                        UserDisplay(image = user.pictureUrl!!, name = user.name!!)

                                    }
                                }
                                is Response.Failure -> {}
                                is Response.Loading -> {}
                            }
                        }

                        userViewModel.oreActivityUsersState.value.let {
                            when (it) {
                                is Response.Success -> {
                                    items(it.data) { user ->
                                        UserDisplay(image = user.pictureUrl!!, name = user.name!!)

                                    }
                                }
                                is Response.Failure -> {}
                                is Response.Loading -> {}
                            }
                        }

                    }
                }
            }
        }
    }
}

@Composable
fun ReportItem(title:String,description: String,selected:Boolean,checkedChange:(Boolean) ->Unit){
    Card(modifier=Modifier,elevation=0.dp,shape= RoundedCornerShape(8.dp), border = BorderStroke(1.dp,color=SocialTheme.colors.uiFloated), backgroundColor = SocialTheme.colors.uiBackground) {
        Row(modifier=Modifier.padding(horizontal = 8.dp,vertical=6.dp),verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceAround){
            Column(Modifier.weight(0.8f)) {
                Text(text=title,style= TextStyle(fontFamily = Inter, fontWeight = FontWeight.SemiBold, fontSize = 14.sp),color=SocialTheme.colors.textPrimary)
                Text(text=description,
                    style= TextStyle(fontFamily = Inter, fontWeight = FontWeight.Light, fontSize = 10.sp),color=SocialTheme.colors.textPrimary)
            }

            androidx.compose.material3.Checkbox(
                checked = selected,
                colors = CheckboxDefaults.colors(
                    checkedColor = SocialTheme.colors.iconInteractive,
                    uncheckedColor = Color.Black.copy(alpha = 0.8f)
                ),
                onCheckedChange =checkedChange)
        }
    }
}
@Composable
fun DrawerContent(onEvent: (MapEvent) -> Unit, activityViewModel: ActivityViewModel) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .padding(vertical = 48.dp)
            .padding(start = 8.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        //DRAWER Content

        //User profile
        DrawerProfileField(username = UserData.user!!.username!!,
            fullName = UserData.user!!.name!!,
            picture_url = UserData.user!!.pictureUrl!!,
            icon = R.drawable.ic_person,
            onClick = { onEvent(MapEvent.GoToEditProfile) })

        com.example.socialk.chat.ChatComponents.Divider()

        //friends list and search
        DrawerField(
            title = "Search",
            icon = R.drawable.ic_search,
            onClick = { onEvent(MapEvent.AddPeople) })
        //trending
        DrawerField(
            title = "Trending",
            icon = R.drawable.ic_trending,
            onClick = {onEvent(MapEvent.GoToTrending)}){
            Text(
                text = "Upcoming soon",
                style = TextStyle(
                    fontFamily = Inter,
                    fontWeight = FontWeight.Light,
                    fontSize = 10.sp,color=SocialTheme.colors.textPrimary
                ))
        }
        // calendar
        DrawerField(
            title = "Upcoming",
            icon = R.drawable.ic_calendar,
            onClick = { onEvent(MapEvent.GoToCalendar) }) {
            activityViewModel.activitiesListState.value.let { it ->
                when (it) {
                    is Response.Success -> {
                        if (it.data.size > 0) {
                            val first_activity = it.data.get(0)
                            Row() {
                                Spacer(Modifier.width(64.dp))
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_down_right),
                                    tint = SocialTheme.colors.iconPrimary,
                                    contentDescription = null
                                )
                                Column() {
                                    Text(
                                        text = first_activity.time_left,
                                        style = TextStyle(
                                            fontFamily = Inter,
                                            fontWeight = FontWeight.Light,
                                            fontSize = 10.sp, color = SocialTheme.colors.textPrimary
                                        )
                                    )
                                    val text = if (first_activity.title.length > 25) {
                                        first_activity.title.take(25) + "..."
                                    } else {
                                        first_activity.title
                                    }
                                    Text(
                                        text = text, style = TextStyle(
                                            fontFamily = Inter,
                                            fontWeight = FontWeight.Normal,
                                            fontSize = 12.sp, color = SocialTheme.colors.textPrimary
                                        )
                                    )
                                }


                            }
                        }

                    }
                    is Response.Failure -> {}
                    is Response.Loading -> {}
                }
            }
        }

        //user activities
        DrawerField(
            title = "Created",
            icon = R.drawable.ic_event_available,
            onClick = { onEvent(MapEvent.GoToCreated) })
        //bookmarked activities
        DrawerField(
            title = "Groups",
            icon = R.drawable.ic_groups,
            onClick = { onEvent(MapEvent.GoToGroup) })

        //bookmarked activities
        /* DrawerField(title = "Bookmarks", icon = R.drawable.ic_bookmark, onClick = {onEvent(MapEvent.GoToBookmarked)})*/

        //setttings
        DrawerField(
            title = "Settings",
            icon = R.drawable.ic_settings,
            onClick = { onEvent(MapEvent.GoToSettings) })

        //help
        DrawerField(
            title = "Help",
            icon = R.drawable.ic_help,
            onClick = { onEvent(MapEvent.GoToHelp) })

        //info
        DrawerField(
            title = "Info",
            icon = R.drawable.ic_info,
            onClick = { onEvent(MapEvent.GoToInfo) })
        Spacer(Modifier.weight(1f))
        //TODO HARDCODED NAME
        ButtonLink(onClick = {
            val dynamicLink = Firebase.dynamicLinks.dynamicLink {
                link = Uri.parse("https://link.friendup.app/" + "User" + "/" + UserData.user!!.id)
                domainUriPrefix = "https://link.friendup.app/"
                // Open links with this app on Android
                androidParameters { }
            }
            val dynamicLinkUri = dynamicLink.uri
            val localClipboardManager = clipboardManager
            localClipboardManager.setText(AnnotatedString(dynamicLinkUri.toString()))
            Toast.makeText(context, "Copied user link to clipboard", Toast.LENGTH_LONG).show()
        }, username = UserData.user!!.username!!)
    }
}


fun pointToCurrentLocation(cameraPositionState: CameraPositionState, currentLocation: LatLng) {
    cameraPositionState.position =
        CameraPosition.fromLatLngZoom(currentLocation, 13f)
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun MapActivityPreview(
    modifier: Modifier = Modifier,
    bottomSheetActivity: Activity,
    onEvent: (ActivityPreviewEvent) -> Unit
) {
    Box(modifier = modifier) {
        Column(Modifier.padding(horizontal = 24.dp)) {

            androidx.compose.material3.Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent)
            ) {
                Box(
                    Modifier
                        .background(color = Color.Black.copy(0.3f))
                        .padding(12.dp)
                ) {
                    Column() {
                        Row(
                            modifier = Modifier.pointerInput(Unit) { },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            GlideImage(
                                model = bottomSheetActivity.creator_profile_picture,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop,
                            )

                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier) {
                                Text(
                                    text = bottomSheetActivity.creator_username,
                                    style = com.example.socialk.ui.theme.Typography.h5,
                                    fontWeight = FontWeight.Light,
                                    color = Color.White
                                )
                                Text(
                                    text = "Starts in " + bottomSheetActivity.time_left,
                                    style = com.example.socialk.ui.theme.Typography.subtitle1,
                                    textAlign = TextAlign.Center,
                                    color = Color.White
                                )
                            }
                            Spacer(modifier = Modifier.weight(1f))
                            IconButton(onClick = { }) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_more),
                                    contentDescription = null,
                                    tint = SocialTheme.colors.iconPrimary
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Column(
                            modifier = modifier
                                .padding(end = 8.dp),
                            verticalArrangement = Arrangement.Top,
                            horizontalAlignment = Alignment.Start
                        ) {
                            Text(
                                text = bottomSheetActivity.title,
                                style = com.example.socialk.ui.theme.Typography.h3,
                                fontWeight = FontWeight.Normal,
                                color = Color.White,
                                textAlign = TextAlign.Left
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = bottomSheetActivity.description,
                                style = com.example.socialk.ui.theme.Typography.h5,
                                fontWeight = FontWeight.Light,
                                color = Color.White,
                                textAlign = TextAlign.Left
                            )
                        }
                    }

                }
            }

            Spacer(modifier = Modifier.height(4.dp))
            DataBox(icon = R.drawable.ic_date_24, bottomSheetActivity.date, title = "Date")
            Spacer(modifier = Modifier.height(4.dp))
            DataBox(
                icon = R.drawable.ic_timer_24,
                bottomSheetActivity.start_time + " - " + bottomSheetActivity.end_time,
                title = "Time"
            )
            //ChatMessageBox()
            Spacer(modifier = Modifier.height(4.dp))
           /* ParticipantsBox(
                bottomSheetActivity.participants_usernames,
                bottomSheetActivity.participants_profile_pictures
            )*/

        }


    }
}

@Composable
fun MapActivityItem(
    activity: Activity,
    username: String,
    profilePictureUrl: String,
    timeLeft: String,
    title: String,
    description: String,
    date: String,
    timePeriod: String,
    custom_location: String,
    liked: Boolean,
    onEvent: (ActivityEvent) -> Unit
) {


    var liked = rememberSaveable { mutableStateOf(liked) }
    Box(
        modifier = Modifier
            .padding(start = 12.dp)
            .padding(end = 12.dp)
            .padding(vertical = 12.dp)
    ) {
        Column() {
            //ACtivity top content
            Row(modifier = Modifier, verticalAlignment = Alignment.CenterVertically) {

                /*  AsyncImage(
                      model = ImageRequest.Builder(LocalContext.current)
                          .data(profilePictureUrl)
                          .crossfade(true)
                          .build(),
                      placeholder = painterResource(R.drawable.ic_person),
                      contentDescription = "image sent",
                      contentScale = ContentScale.Crop,
                      modifier = Modifier .size(36.dp)
                          .clip(CircleShape)
                  )*/
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier) {
                    Text(
                        text = username,
                        style = com.example.socialk.ui.theme.Typography.h5,
                        fontWeight = FontWeight.Light,
                        color = SocialTheme.colors.textPrimary
                    )
                    Text(
                        text = timeLeft,
                        style = com.example.socialk.ui.theme.Typography.subtitle1,
                        textAlign = TextAlign.Center,
                        color = SocialTheme.colors.textPrimary
                    )
                }
                Spacer(modifier = Modifier.weight(1f))

                Spacer(modifier = Modifier.width(12.dp))
            }
            Spacer(modifier = Modifier.height(12.dp))
            //TEXT AND CONTROLS ROW
            Row(modifier = Modifier, verticalAlignment = Alignment.CenterVertically) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp), verticalArrangement = Arrangement.Top
                ) {
                    Text(
                        text = title,
                        style = com.example.socialk.ui.theme.Typography.h3,
                        fontWeight = FontWeight.Normal,
                        color = SocialTheme.colors.textPrimary,
                        textAlign = TextAlign.Left
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = description,
                        style = com.example.socialk.ui.theme.Typography.h5,
                        fontWeight = FontWeight.Light,
                        color = SocialTheme.colors.iconPrimary,
                        textAlign = TextAlign.Left
                    )
                }

            }

            //DETAILS
            Spacer(modifier = Modifier.height(12.dp))
            //todo either custom location or latlng
            //ActivityDetailsBar(custom_location = null,location = null, date = date, timePeriod = timePeriod, onEvent = {}, participants_pictures = activity.participants_profile_pictures,min=activity.minUserCount,max=activity.maxUserCount)
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp)
                    .height(1.dp)
                    .background(color = SocialTheme.colors.uiFloated)
            )


        }
    }
}

enum class ExpandedType {
    HALF, FULL, COLLAPSED
}

@Composable
@OptIn(ExperimentalMaterialApi::class, ExperimentalComposeUiApi::class)
fun MapBottomDialog(
    modifier: Modifier,
    state: ModalBottomSheetState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden),
    type: String, onEvent: (MapEvent) -> Unit, activeUsersViewModel: ActiveUsersViewModel?,
    activityViewModel: ActivityViewModel?,
    chatViewModel: ChatViewModel,
    viewModel: AuthViewModel?,
    activityEvent: (ActivityEvent) -> Unit,
    openFilter:() -> Unit,
    content: @Composable (asdas: Boolean) -> Unit

) {
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp
    var expandedType by remember {
        mutableStateOf(ExpandedType.HALF)
    }
    val height by animateIntAsState(
        when (expandedType) {
            ExpandedType.HALF -> screenHeight / 2
            ExpandedType.FULL -> screenHeight
            ExpandedType.COLLAPSED -> 0
        }
    )

    val couroutineScope = rememberCoroutineScope()
    val isDark = isSystemInDarkTheme()
    val bottomSheetScaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = BottomSheetState(BottomSheetValue.Collapsed)
    )

    BottomSheetScaffold(
        scaffoldState = bottomSheetScaffoldState,
        sheetElevation = 8.dp,
        sheetShape = RoundedCornerShape(
            bottomStart = 0.dp,
            bottomEnd = 0.dp,
            topStart = 24.dp,
            topEnd = 24.dp
        ),
        sheetContent = {
            sheetContent(
                activityEvent = activityEvent,
                height,
                expandedType,
                activeUsersViewModel,
                viewModel,
                activityViewModel,
                isDark,
                onEvent = onEvent,
                openFilter = openFilter
            )
        },
        // This is the height in collapsed state
        sheetPeekHeight = 140.dp
    ) {
        content(bottomSheetScaffoldState.bottomSheetState.isExpanded)
    }


}

@Composable
fun sheetContent(
    activityEvent: (ActivityEvent) -> Unit,
    height: Int,
    expandedType: ExpandedType,
    activeUsersViewModel: ActiveUsersViewModel?,
    viewModel: AuthViewModel?,
    activityViewModel: ActivityViewModel?,
    isDark: Boolean,
    onEvent: (MapEvent) -> Unit,
    openFilter:() -> Unit

) {
    var isUpdated = false
    val displayMetrics: DisplayMetrics = LocalContext.current.getResources().getDisplayMetrics()
    val dpHeight: Float = displayMetrics.heightPixels / displayMetrics.density
    val bottomSheetMaxHeight = dpHeight * (3 / 4)
    Box(
        Modifier
            .fillMaxWidth()
            .heightIn(min = 150.dp, max = 700.dp)
            .background(SocialTheme.colors.uiBackground),
    ) {
        HomeScreenContent(
            activeUsersViewModel = activeUsersViewModel,
            viewModel = viewModel,
            activityViewModel = activityViewModel,
            padding = PaddingValues(12.dp), homeViewModel = null,
            isDark = isDark,
            activityEvent = activityEvent,
            onEvent = { homeEvent ->

            },
            onLongClick = { activity ->

            }, openFilter = openFilter
        )
    }

}

fun loadIcon(
    context: Context,
    url: String?,
    placeHolder: Int,
): BitmapDescriptor? {
    try {
        var bitmap: Bitmap? = null
        Glide.with(context)
            .asBitmap()
            .load(url)
            .circleCrop()
            .error(placeHolder)
            .override(200, 200) // resize the image to 48x48 pixels
            .diskCacheStrategy(DiskCacheStrategy.ALL) //
            // to show a default icon in case of any errors
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(
                    resource: Bitmap,
                    transition: com.bumptech.glide.request.transition.Transition<in Bitmap>?
                ) {
                    bitmap = resource
                }

                override fun onLoadCleared(placeholder: Drawable?) {

                }
            })
        bitmap = bitmap?.let {
            Bitmap.createScaledBitmap(
                it,
                TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    48f,
                    context.resources.displayMetrics
                ).toInt(),
                TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    48f,
                    context.resources.displayMetrics
                ).toInt(),
                true
            )
        }
        return BitmapDescriptorFactory.fromBitmap(
            bitmap!!.copy(
                bitmap!!.config,
                bitmap!!.isMutable
            )
        )
    } catch (e: Exception) {
        e.printStackTrace()
        return null
    }

}

