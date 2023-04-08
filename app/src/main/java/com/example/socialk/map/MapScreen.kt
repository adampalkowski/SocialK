package com.example.socialk.map

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.util.DisplayMetrics
import android.util.Log
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.animation.*
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.bumptech.glide.Glide
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.request.target.CustomTarget
import com.example.socialk.Destinations
import com.example.socialk.R
import com.example.socialk.components.*
import com.example.socialk.create.CreateActivityButton
import com.example.socialk.di.ActiveUsersViewModel
import com.example.socialk.di.ActivityViewModel
import com.example.socialk.di.ChatViewModel
import com.example.socialk.home.*
import com.example.socialk.model.Activity
import com.example.socialk.model.Response
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
import com.google.maps.android.compose.*
import kotlinx.coroutines.launch

sealed class MapEvent {
    object GoToProfile : MapEvent()
    object GoToChats : MapEvent()
    object LogOut : MapEvent()
    object GoToHome : MapEvent()
    object GoToSettings : MapEvent()
    object AskForPermission : MapEvent()
    object BackPressed : MapEvent()
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
    systemUiController: SystemUiController,
    latLngInitial: LatLng?,
    activityViewModel: ActivityViewModel,
    onEvent: (MapEvent) -> Unit,
    bottomNavEvent: (Destinations) -> Unit,
    viewModel: MapViewModel,
    locationCallback: LocationCallback,
    activeUsersViewModel: ActiveUsersViewModel?,
    chatViewModel: ChatViewModel,
    authViewModel: AuthViewModel?
) {
    if(viewModel.location.value!=null){
        Log.d("getClosestActivities","call")
        activityViewModel.getClosestActivities(viewModel.location.value!!.latitude,viewModel.location.value!!.longitude)
    }
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


    val context = LocalContext.current
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
    var bitmap: BitmapDescriptor? =
        loadIcon(LocalContext.current, UserData.user?.pictureUrl!!, R.drawable.ic_person)
    var properties by remember {
        mutableStateOf(MapProperties(mapType = MapType.NORMAL))
    }



    Surface(
        Modifier.background(color = SocialTheme.colors.uiBackground),
        color = SocialTheme.colors.uiBackground
    ) {
        Scaffold(
            scaffoldState = scaffoldState,
            drawerBackgroundColor = SocialTheme.colors.uiBackground,
            drawerScrimColor = Color.Black.copy(alpha = 0.3f),
            drawerContent = {
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
                        onClick = {})

                    com.example.socialk.chat.ChatComponents.Divider()
                    // calendar
                    DrawerField(title = "Calendar", icon = R.drawable.ic_calendar, onClick = {}) {
                        activityViewModel.activitiesListState.value.let { it ->
                            when (it) {
                                is Response.Success -> {
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
                                                    fontSize = 10.sp,color=SocialTheme.colors.textPrimary
                                                )
                                            )
                                            val text = if (first_activity.title.length>25){first_activity.title.take(25) +"..."}else{first_activity.title}
                                            Text(
                                                text = text
                                                ,      style = TextStyle(
                                                    fontFamily = Inter,
                                                    fontWeight = FontWeight.Normal,
                                                    fontSize = 12.sp,color=SocialTheme.colors.textPrimary
                                                ) )
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
                        onClick = {})

                    //bookmarked activities
                    DrawerField(title = "Bookmarks", icon = R.drawable.ic_bookmark, onClick = {})

                    //setttings
                    DrawerField(title = "Settings", icon = R.drawable.ic_settings, onClick = {})

                    //help
                    DrawerField(title = "Help", icon = R.drawable.ic_help, onClick = {})

                    //info
                    DrawerField(title = "Info", icon = R.drawable.ic_info, onClick = {})

                }

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
                    state = bottomSheetState,
                    type = bottomSheetType,
                    onEvent = onEvent,
                    activeUsersViewModel = activeUsersViewModel,
                    activityViewModel = activityViewModel,
                    chatViewModel = chatViewModel,
                    viewModel = authViewModel,
                ) { isExpanded ->

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
                                                ), icon = bitmap
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
                                                            MarkerInfoWindow(
                                                                zIndex = 0.5f,
                                                                state = MarkerState(
                                                                    position = latLng
                                                                ),
                                                                icon = loadIcon(
                                                                    LocalContext.current,
                                                                    activity.creator_profile_picture,
                                                                    R.drawable.ic_person
                                                                )
                                                            ) {
                                                                MapActivityPreview(
                                                                    bottomSheetActivity = activity,
                                                                    onEvent = {}
                                                                )
                                                                /* Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                                                                     androidx.compose.material3.Card(
                                                                         shape = RoundedCornerShape(
                                                                             16.dp
                                                                         )
                                                                     ) {
                                                                         Box(modifier = Modifier.background(color = SocialTheme.colors.uiBackground),) {
                                                                             MapActivityItem(
                                                                                 activity = activity,
                                                                                 username = activity.creator_username,
                                                                                 profilePictureUrl = activity.creator_profile_picture,
                                                                                 timeLeft = activity.time_left,
                                                                                 title = activity.title,
                                                                                 description = activity.description,
                                                                                 date = activity.date,
                                                                                 timePeriod = activity.start_time + "-" + activity.end_time,
                                                                                 custom_location = activity.custom_location,
                                                                                 liked = activity.participants_usernames.containsKey(
                                                                                     UserData.user!!.id
                                                                                 ),
                                                                                 onEvent = {}
                                                                             )

                                                                         }}


                                                                             Spacer(modifier = Modifier.height(4.dp))
                                                                         }
                                                                         */
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
                                                            MarkerInfoWindow(
                                                                state = MarkerState(
                                                                    position = latLng
                                                                ),
                                                                icon = loadIcon(
                                                                    LocalContext.current,
                                                                    activity.creator_profile_picture,
                                                                    R.drawable.ic_person
                                                                )
                                                            ) {
                                                                MapActivityPreview(
                                                                    bottomSheetActivity = activity,
                                                                    onEvent = {}
                                                                )
                                                                /*  Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                                                                      androidx.compose.material3.Card(
                                                                          shape = RoundedCornerShape(
                                                                              16.dp
                                                                          )
                                                                      ) {
                                                                          Box(modifier = Modifier.background(color = SocialTheme.colors.uiBackground),) {
                                                                              MapActivityItem(
                                                                                  activity = activity,
                                                                                  username = activity.creator_username,
                                                                                  profilePictureUrl = activity.creator_profile_picture,
                                                                                  timeLeft = activity.time_left,
                                                                                  title = activity.title,
                                                                                  description = activity.description,
                                                                                  date = activity.date,
                                                                                  timePeriod = activity.start_time + "-" + activity.end_time,
                                                                                  custom_location = activity.custom_location,
                                                                                  liked = activity.participants_usernames.containsKey(
                                                                                      UserData.user!!.id
                                                                                  ),
                                                                                  onEvent = {}
                                                                              )

                                                                          }}


                                                                      Spacer(modifier = Modifier.height(4.dp))
                                                                  }*/

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
                                                    ), icon = bitmap
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
                                                backgroundColor = Color.White.copy(alpha = 0.95f),
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
                                                        tint = Color.Black
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
                                                backgroundColor = Color.White,
                                                elevation = 4.dp
                                            ) {
                                                Box(
                                                    modifier = Modifier.fillMaxSize(),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Icon(
                                                        painter = painterResource(id = R.drawable.ic_current_location),
                                                        contentDescription = null,
                                                        tint = Color.Black
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
                                                backgroundColor = Color.White,
                                                elevation = 4.dp
                                            ) {
                                                Box(
                                                    modifier = Modifier.fillMaxSize(),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Icon(
                                                        painter = painterResource(id = R.drawable.instagram_chat_icon_isolated_on_transparent_background_png),
                                                        contentDescription = null,
                                                        tint = Color.Black
                                                    )
                                                }
                                            }
                                            Spacer(modifier = Modifier.width(12.dp))
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
                                            }
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
                                            enter = scaleIn(),
                                            exit = scaleOut()
                                        ) {

                                            CreateActivityButton(
                                                modifier = Modifier.width(200.dp),
                                                text = "Add location",
                                                onClick = {
                                                    onEvent(
                                                        MapEvent.GoToCreateActivity(
                                                            location_picked_flow.value!!
                                                        )
                                                    )
                                                },
                                                icon = R.drawable.ic_right
                                            )

                                            Spacer(modifier = Modifier.height(12.dp))
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
            ParticipantsBox(
                bottomSheetActivity.participants_usernames,
                bottomSheetActivity.participants_profile_pictures
            )

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
                activityEvent = {},
                height,
                expandedType,
                activeUsersViewModel,
                viewModel,
                activityViewModel,
                isDark,
                onEvent = onEvent
            )
        },
        // This is the height in collapsed state
        sheetPeekHeight = 140.dp
    ) {
        content(bottomSheetScaffoldState.bottomSheetState.isExpanded)
    }


    /*     BottomSheetScaffold(sheetShape = RoundedCornerShape(32.dp), sheetElevation = 4.dp, scaffoldState = bottomSheetScaffoldState, sheetPeekHeight = height.dp, sheetBackgroundColor = SocialTheme.colors.uiFloated,sheetContent = {
             sheetContent(height,)
         }) {it->
             content()
         }*/

    /*  ModalBottomSheetLayout(sheetShape = RoundedCornerShape(32.dp),
          sheetState = state, sheetBackgroundColor = SocialTheme.colors.uiFloated,
          sheetContentColor = SocialTheme.colors.textPrimary, scrimColor = Color.Transparent,
          sheetContent = {
              HomeScreenContent(activeUsersViewModel = activeUsersViewModel,
                  viewModel = viewModel,
                  activityViewModel = activityViewModel,
                  padding = PaddingValues(12.dp), homeViewModel = null,
                  isDark = isDark,
                  activityEvent = {/*
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
                          }*/

                  },
                  onEvent = { homeEvent ->

                  }, onLongClick = { activity ->

                  }, closeBottomSheet = { couroutineScope.launch {  state.hide()} })
          }
      ){
      }*/


    /* Card(modifier=modifier.height(300.dp),shape = RoundedCornerShape(24.dp)) {
         Box(modifier = Modifier.height(300.dp).background(color=SocialTheme.colors.uiBackground))
         {

         }

     }*/


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
    onEvent: (MapEvent) -> Unit
) {
    var isUpdated = false
    val displayMetrics: DisplayMetrics = LocalContext.current.getResources().getDisplayMetrics()
    val dpHeight: Float = displayMetrics.heightPixels / displayMetrics.density
    val bottomSheetMaxHeight = dpHeight * (3 / 4)
    Box(
        Modifier
            .fillMaxWidth()
            .heightIn(min = 150.dp, max = 700.dp)
            .pointerInput(Unit) {
                detectVerticalDragGestures(
                    onVerticalDrag = { change, dragAmount ->
                        /*  change.consume()
                        if (!isUpdated) {
                            expandedType = when {
                                dragAmount < 0 && expandedType == ExpandedType.COLLAPSED -> {
                                    ExpandedType.HALF
                                }
                                dragAmount < 0 && expandedType == ExpandedType.HALF -> {
                                    ExpandedType.FULL
                                }
                                dragAmount > 0 && expandedType == ExpandedType.FULL -> {
                                    ExpandedType.HALF
                                }
                                dragAmount > 0 && expandedType == ExpandedType.HALF -> {
                                    ExpandedType.COLLAPSED
                                }
                                else -> {
                                    ExpandedType.FULL
                                }
                            }
                            isUpdated = true
                        }*/
                    },
                    onDragEnd = {
                        isUpdated = false
                    }
                )
            }
            .background(Color.Red),
    ) {
        HomeScreenContent(activeUsersViewModel = activeUsersViewModel,
            viewModel = viewModel,
            activityViewModel = activityViewModel,
            padding = PaddingValues(12.dp), homeViewModel = null,
            isDark = isDark,
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
                    is ActivityEvent.GoToMap -> {
                        onEvent(MapEvent.GoToMap(latlng = it.latlng))

                    }
                    is ActivityEvent.GoToProfile -> {
                        onEvent(MapEvent.GoToProfileWithID(user_id = it.user_id))

                    }
                    is ActivityEvent.OpenActivitySettings -> {
                        /* Log.d("HomeScreen", "open settings")
                         bottomSheetType = "settings"
                         bottomSheetActivity = it.activity
                         scope.launch {
                             bottomSheetState.show()
                         }*/
                    }
                    is ActivityEvent.DisplayPicture -> {
                        onEvent(MapEvent.DisplayPicture(it.photo_url, it.activity_id))
                    }

                    else -> {}
                }

            },
            onEvent = { homeEvent ->

            }, onLongClick = { activity ->

            }, closeBottomSheet = { /*couroutineScope.launch {  state.hide()}*/ })
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
            .load(url).circleCrop()
            .error(placeHolder)
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