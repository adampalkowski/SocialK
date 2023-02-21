package com.example.socialk.map

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.transition.Transition
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.example.socialk.Destinations
import com.example.socialk.R
import com.example.socialk.components.BottomBar
import com.example.socialk.components.SocialDialog
import com.example.socialk.di.ActivityViewModel
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
import kotlinx.coroutines.coroutineScope

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
        return BitmapDescriptorFactory.fromBitmap(bitmap!!)
    } catch (e: Exception) {
        e.printStackTrace()
        return null
    }

}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MapScreen(activityViewModel:ActivityViewModel,
    onEvent: (MapEvent) -> Unit,
    bottomNavEvent: (Destinations) -> Unit,
    viewModel: MapViewModel,
    locationCallback: LocationCallback
) {
    var currentLocation: LatLng? by remember { mutableStateOf(null) }
    var location_picked_flow = viewModel.locations_picked.collectAsState()
    var isMapLoaded by remember { mutableStateOf(false) }
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
                            currentLocation = LatLng(location?.latitude!!, location?.longitude!!)
                            cameraPositionState.position =
                                CameraPosition.fromLatLngZoom(currentLocation!!, 13f)
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
                                        it.data.forEach {
                                            if(it.location.isNotEmpty()){
                                                val values=it.location.split("/")
                                                val latLng= LatLng(values.get(0).toDouble(),values.get(1).toDouble())
                                                MarkerInfoWindow(
                                                    state = MarkerState(
                                                        position = latLng
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
                                                                    text = "Activity location",
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
                            if (it != null) {
                                Log.d("MapScreen", "forehac " + it.toString())
                                cameraPositionState.position =
                                    CameraPosition.fromLatLngZoom(it!!, 13f)
                                Box(
                                    modifier = Modifier
                                        .align(
                                            Alignment.BottomEnd
                                        )
                                        .padding(bottom = 48.dp, end = 24.dp)
                                ) {
                                    Card(
                                        onClick = { onEvent(MapEvent.GoToCreateActivity(it))
                                                  Log.d("mapscreen","card clicked")},
                                        elevation = 2.dp,
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Row(     modifier = Modifier
                                            .background(color = SocialTheme.colors.uiBackground)
                                            .padding(8.dp)) {

                                                Text(
                                                    text = "Create activity",
                                                    style = TextStyle(
                                                        fontFamily = Inter,
                                                        fontWeight = FontWeight.Normal,
                                                        fontSize = 16.sp
                                                    ),
                                                    color = SocialTheme.colors.textPrimary
                                                )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Icon(
                                                painter = painterResource(id = R.drawable.ic_right),
                                                contentDescription = null, tint = SocialTheme.colors.iconPrimary
                                            )
                                        }


                                    }
                                    Spacer(modifier = Modifier.height(64.dp))
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
