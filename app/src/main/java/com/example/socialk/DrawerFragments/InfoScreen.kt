package com.example.socialk.DrawerFragments

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import com.example.socialk.components.HomeScreenHeading
import com.example.socialk.ui.theme.SocialTheme
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role.Companion.Button
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.NavController
import com.example.socialk.Info.*
import com.example.socialk.R
import com.example.socialk.ui.theme.Inter
import kotlinx.coroutines.launch
enum class InfoScreen { MainScreen,FAQ,HOW_TO,GUIDELINES,LAW,ABOUT_US,}

sealed class InfoEvent{
    object GoBack:InfoEvent()
    object GoToFAQ:InfoEvent()
    object GoToGuidelines:InfoEvent()
    object GoToTerms:InfoEvent()
    object GoToAboutUs:InfoEvent()
    object GoToAppAndUsage:InfoEvent()
    object GoToMain:InfoEvent()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(navController: NavController,onEvent:(InfoEvent)->Unit) {
    // Render the Screen 2 content
        Box(
            Modifier
                .fillMaxSize()
                .background(color = SocialTheme.colors.uiBackground)){
            Column(Modifier.verticalScroll(rememberScrollState())) {
                HomeScreenHeading(onEvent = { onEvent(InfoEvent.GoBack)}, title = "Info")
                com.example.socialk.chat.ChatComponents.Divider()
                InfoItem(title = "FAQ", icon = R.drawable.ic_faq, onClick = {onEvent(InfoEvent.GoToFAQ)} )
                com.example.socialk.create.Divider()
                InfoItem(title = "App and usage", icon = R.drawable.ic_light_bulb, onClick = {onEvent(InfoEvent.GoToAppAndUsage)} )
                com.example.socialk.create.Divider()
                InfoItem(title = "Community guidelines", icon = R.drawable.ic_handshake, onClick = {onEvent(InfoEvent.GoToGuidelines)} )
                com.example.socialk.create.Divider()
                InfoItem(title = "Terms of service and privacy policy", icon = R.drawable.ic_terms_and_services, onClick = {onEvent(InfoEvent.GoToTerms)} )
                com.example.socialk.create.Divider()
                InfoItem(title = "About us", icon = R.drawable.ic_contact, onClick = {onEvent(InfoEvent.GoToAboutUs)} )

            }

        }

}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InfoItem(title:String,icon:Int,onClick:()->Unit){
    Card(modifier= Modifier
        .fillMaxWidth()
       ,onClick = {onClick()}) {
        Box(modifier = Modifier
            .fillMaxWidth()
            .background(SocialTheme.colors.uiBackground) .padding(horizontal = 24.dp, vertical = 8.dp) ){
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(painter = painterResource(id = icon) , contentDescription =null,tint=SocialTheme.colors.iconPrimary)
                Spacer(modifier = Modifier.width(12.dp))
                Text(text = title, style = TextStyle(fontFamily = Inter, fontSize = 16.sp, fontWeight = FontWeight.Medium,color=SocialTheme.colors.textPrimary))
            }
        }

    }

}

@Composable
fun InfoScreen(navController:NavController,onEvent:(InfoEvent)->Unit){
    val currentScreen = remember { mutableStateOf(InfoScreen.MainScreen) }

    // Conditionally render different composables based on the current screen
    when (currentScreen.value) {
        InfoScreen.FAQ -> FAQScreen(navController = navController,onEvent={event->
            when(event){
                is InfoEvent.GoBack->{    currentScreen.value = InfoScreen.MainScreen}
                else ->{}

            }


        })
        InfoScreen.MainScreen -> MainScreen(navController = navController,onEvent={event->
            when(event){
                is InfoEvent.GoToFAQ->{
                    currentScreen.value = InfoScreen.FAQ
                }
                is InfoEvent.GoToAboutUs->{
                    currentScreen.value = InfoScreen.ABOUT_US
                }
                is InfoEvent.GoToGuidelines->{
                    currentScreen.value = InfoScreen.GUIDELINES
                }
                is InfoEvent.GoToAppAndUsage->{
                    currentScreen.value = InfoScreen.HOW_TO
                }
                is InfoEvent.GoToTerms->{
                    currentScreen.value = InfoScreen.LAW
                }
                is InfoEvent.GoBack->{
                    onEvent(InfoEvent.GoBack)
                }


                else ->{}
            }
        })
        InfoScreen.ABOUT_US -> AboutUsScreen(navController = navController,onEvent={event->
            when(event){
                is InfoEvent.GoBack->{    currentScreen.value = InfoScreen.MainScreen}
                else ->{}

            }


        })

        InfoScreen.GUIDELINES -> GuidelinesScreen(navController = navController,onEvent={event->
            when(event){
                is InfoEvent.GoBack->{    currentScreen.value = InfoScreen.MainScreen}
                else ->{}

            }


        })
        InfoScreen.HOW_TO -> AppAndUsageScreen(navController = navController,onEvent={event->
            when(event){
                is InfoEvent.GoBack->{    currentScreen.value = InfoScreen.MainScreen}
                else ->{}

            }


        })

        InfoScreen.LAW -> TermsAndServicesScreen(navController = navController,onEvent={event->
            when(event){
                is InfoEvent.GoBack->{    currentScreen.value = InfoScreen.MainScreen}
                else ->{}

            }


        })
    }

}
/*

@Composable
fun Map(lat: Double, lng: Double, zoom: Double) {
    MapboxMap(lat = lat, lng = lng, zoom = zoom)
}


@Composable
fun MapboxMap(lat: Double, lng: Double, zoom: Double) {
    val map = rememberMapboxViewWithLifecycle()

    MapboxMapContainer(map = map, lat = lat, lng = lng, zoom = zoom)
}


@Composable()
fun MapboxMapContainer(map: MapView, lat: Double, lng: Double, zoom: Double) {
    val (isMapInitialized, setMapInitialized) = remember(map) { mutableStateOf(false) }

    LaunchedEffect(map, isMapInitialized) {
        if (!isMapInitialized) {
            val mbxMap = map.getMapboxMap()
            mbxMap.loadStyleUri(Style.OUTDOORS) {
                mbxMap.centerTo(lat = lat, lng = lng, zoom = zoom)

                setMapInitialized(true)
            }
        }
    }

    val coroutineScope = rememberCoroutineScope()
    AndroidView(factory = { context->map
    }) {


        coroutineScope.launch {
            val mbxMap = it.getMapboxMap()

            mbxMap.centerTo(lat = lat, lng = lng, zoom = zoom)
        }
    }
}





@Composable
private fun rememberMapboxViewWithLifecycle(): MapView {
    val context = LocalContext.current

    val opt = MapInitOptions(context, plugins = emptyList())
    val map = remember { MapView(context, opt) }

    val observer = rememberMapboxViewLifecycleObserver(map)
    val lifecycle = LocalLifecycleOwner.current.lifecycle

    DisposableEffect(lifecycle) {
        lifecycle.addObserver(observer)

        onDispose {
            lifecycle.removeObserver(observer)
        }
    }

    return map
}

@Composable
private fun rememberMapboxViewLifecycleObserver(map: com.mapbox.maps.MapView): LifecycleEventObserver {
    return remember(map) {
        LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START -> map.onStart()
                Lifecycle.Event.ON_STOP -> map.onStop()
                Lifecycle.Event.ON_DESTROY -> map.onDestroy()
                else -> Unit // nop
            }
        }
    }
}


fun MapboxMap.centerTo(lat: Double, lng: Double, zoom: Double) {
    val point = Point.fromLngLat(lng, lat)

    val camera = CameraOptions.Builder()
        .center(point)
        .zoom(zoom)
        .build()

    setCamera(camera)
}*/