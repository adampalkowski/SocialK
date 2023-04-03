package com.example.socialk.map

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.util.Log
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.example.socialk.Destinations
import com.example.socialk.R
import com.example.socialk.components.*
import com.example.socialk.create.CreateActivityButton
import com.example.socialk.di.ActivityViewModel
import com.example.socialk.home.*
import com.example.socialk.home.ActivityPreview
import com.example.socialk.model.Activity
import com.example.socialk.model.Response
import com.example.socialk.model.UserData
import com.example.socialk.ui.theme.Inter
import com.example.socialk.ui.theme.SocialTheme
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

sealed class MapEvent {
    object GoToProfile : MapEvent()
    object LogOut : MapEvent()
    object GoToHome : MapEvent()
    object GoToSettings : MapEvent()
    object AskForPermission : MapEvent()
    class GoToCreateActivity(val latLng: LatLng) : MapEvent()


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
        return BitmapDescriptorFactory.fromBitmap(bitmap!!.copy(bitmap!!.config, bitmap!!.isMutable))
    } catch (e: Exception) {
        e.printStackTrace()
        return null
    }

}

@OptIn(ExperimentalMaterialApi::class, ExperimentalAnimationApi::class)
@Composable
fun MapScreen(latLngInitial: LatLng?,activityViewModel:ActivityViewModel,
    onEvent: (MapEvent) -> Unit,
    bottomNavEvent: (Destinations) -> Unit,
    viewModel: MapViewModel,
    locationCallback: LocationCallback
) {
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
        modifier = Modifier
            .fillMaxSize(), color = SocialTheme.colors.uiBackground
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(56.dp), contentAlignment = Alignment.TopEnd
        ) {
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

                            if (currentLocation==null){
                                currentLocation = LatLng(location?.latitude!!, location?.longitude!!)
                                Log.d("Mapfragment"," dats")
                                if (latLngInitial==null){
                                    cameraPositionState.position =
                                        CameraPosition.fromLatLngZoom(currentLocation!!, 13f)
                                }else{
                                    cameraPositionState.position =
                                        CameraPosition.fromLatLngZoom(latLngInitial!!, 13f)
                                }

                            }else{
                                currentLocation = LatLng(location?.latitude!!, location?.longitude!!)
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
                                when(it){
                                    is Response.Success ->{
                                        it.data.forEach {activity->
                                            if(activity.location.isNotEmpty()){
                                                val values=activity.location.split("/")
                                                val latLng= LatLng(values.get(0).toDouble(),values.get(1).toDouble())
                                                MarkerInfoWindow(zIndex=0.5f,
                                                    state = MarkerState(
                                                        position = latLng
                                                    ), icon = loadIcon(LocalContext.current, activity.creator_profile_picture, R.drawable.ic_person)
                                                ) {
                                                    MapActivityPreview(
                                                        bottomSheetActivity =activity ,
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
                                    is Response.Loading ->{}
                                    is Response.Failure ->{}
                                }
                            }
                            activityViewModel.moreActivitiesListState.value.let {
                                when(it){
                                    is Response.Success ->{
                                        it.data.forEach {activity->
                                            if(activity.location.isNotEmpty()){
                                                val values=activity.location.split("/")
                                                val latLng= LatLng(values.get(0).toDouble(),values.get(1).toDouble())
                                                MarkerInfoWindow(
                                                    state = MarkerState(
                                                        position = latLng
                                                    ), icon = loadIcon(LocalContext.current, activity.creator_profile_picture, R.drawable.ic_person)
                                                ) {
                                                    MapActivityPreview(
                                                        bottomSheetActivity =activity ,
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
                                    is Response.Loading ->{}
                                    is Response.Failure ->{}
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
                        location_picked_flow.value.let {
                            if (it != null){
                                displayCreateButton=true
                            }else{
                                displayCreateButton=false
                            }
                        }

                        Box(
                            modifier = Modifier
                                .align(
                                    Alignment.BottomEnd
                                )
                                .padding(bottom = 48.dp, end = 24.dp)
                        ) {
                          AnimatedVisibility(visible =displayCreateButton, enter = scaleIn(),exit= scaleOut() ) {

                                CreateActivityButton(modifier=Modifier.width(200.dp),text="Add location",onClick={onEvent(MapEvent.GoToCreateActivity(location_picked_flow.value!!))},icon= R.drawable.ic_right)

                                Spacer(modifier = Modifier.height(64.dp))
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


        }
        BottomBar(
            onTabSelected = { screen -> bottomNavEvent(screen) },
            currentScreen = com.example.socialk.Map,
            transparent = true
        )
    }
}
@Composable
fun MapActivityPreview(modifier: Modifier = Modifier, bottomSheetActivity: Activity,onEvent:(ActivityPreviewEvent)->Unit) {
    Box(modifier=modifier) {
        Column(Modifier.padding(horizontal=24.dp)) {

            androidx.compose.material3.Card(shape= RoundedCornerShape(12.dp),   colors = CardDefaults.cardColors(containerColor = Color.Transparent)) {
                 Box(
                     Modifier
                         .background(color = Color.Black.copy(0.3f))
                         .padding(12.dp)){
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
             }

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