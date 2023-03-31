package com.example.socialk.create

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.socialk.Create
import com.example.socialk.Destinations
import com.example.socialk.R
import com.example.socialk.bottomTabRowScreens
import com.example.socialk.components.BottomBar
import com.example.socialk.components.BottomBarRow
import com.example.socialk.di.ActiveUsersViewModel
import com.example.socialk.home.cardHighlited
import com.example.socialk.home.cardnotHighlited
import com.example.socialk.model.Response
import com.example.socialk.ui.theme.Inter
import com.example.socialk.ui.theme.SocialTheme
import com.google.android.gms.maps.model.LatLng
import com.marosseleng.compose.material3.datetimepickers.time.domain.noSeconds
import com.marosseleng.compose.material3.datetimepickers.time.ui.dialog.TimePickerDialog
import java.time.LocalTime


sealed class LiveEvent{
    object GoToProfile : LiveEvent()
    object AskForPermission : LiveEvent()
    object SendLiveMessage : LiveEvent()
    class SetCurrentLocation (val latLng:LatLng): LiveEvent()
    object LogOut : LiveEvent()
    object CloseDialog : LiveEvent()
    object GoToSettings : LiveEvent()
    object GoToHome : LiveEvent()
    object GoToLive : LiveEvent()
    object GoToEvent : LiveEvent()
    object GoToActivity: LiveEvent()
    object ClearState: LiveEvent()
    data class CreateActiveUser(
        val start_time: String,
        val latLng: String,
        val time_length: String
    ): LiveEvent()

}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LiveScreen (activeUsersViewModel:ActiveUsersViewModel,onEvent: (LiveEvent) -> Unit, bottomNavEvent:(Destinations)->Unit){



    Surface(modifier = Modifier
        .fillMaxSize()
        .background(SocialTheme.colors.uiBackground),color= SocialTheme.colors.uiBackground
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            activityPickerLive(isSystemInDarkTheme(), onEvent = { event -> onEvent(event) })
            Spacer(modifier = Modifier.height(12.dp))
            LiveScreenContent(activeUsersViewModel,onEvent)


            Spacer(modifier = Modifier.height(64.dp))
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(56.dp), contentAlignment = Alignment.BottomCenter
        ) {
            BottomBarRow(
                allScreens = bottomTabRowScreens,
                onTabSelected = { screen -> bottomNavEvent(screen) },
                currentScreen = Create, transparent = false
            )
        }
    }

        Box(modifier = Modifier
            .fillMaxWidth()
            .heightIn(56.dp)
            .padding(vertical = 16.dp), contentAlignment = Alignment.TopEnd){

        }
        BottomBar(onTabSelected = { screen->bottomNavEvent(screen)},currentScreen = Create)

    activeUsersViewModel.isActiveUsersAddedState.value.let {
        when(it){
            is Response.Loading-> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center){
                CircularProgressIndicator()
            }
            is Response.Success->  {onEvent(LiveEvent.GoToHome)
                onEvent(LiveEvent.ClearState)}
            is Response.Failure-> Box(modifier = Modifier.fillMaxSize()){
                androidx.compose.material3.Text(text = "FAILURE", fontSize = 50.sp)
            }
            else->{}
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LiveScreenContent(activeUsersViewModel:ActiveUsersViewModel,onEvent: (LiveEvent) -> Unit) {
    var currentLocation = rememberSaveable {
        mutableStateOf<LatLng?>(null)
    }
    var isDateDialogShown: Boolean by rememberSaveable {
        mutableStateOf(false)
    }
    var isTimeDialogShown: Boolean by rememberSaveable {
        mutableStateOf(false)
    }
    var isTimeLengthDialogShown: Boolean by rememberSaveable {
        mutableStateOf(false)
    }
    var timeLengthState by rememberSaveable {
        mutableStateOf("01:00")
    }
    var timeState by rememberSaveable {
        mutableStateOf(LocalTime.now().noSeconds().toString())
    }
    // Initializing a Calendar
    val (selectedTimeinit, setSelectedTime) = rememberSaveable {
        mutableStateOf(LocalTime.now().noSeconds())
    }
    val selectedTime = remember { mutableStateOf<LocalTime?>(null) }

    if (isTimeLengthDialogShown) {
        TimePickerDialog(
            onDismissRequest = { isTimeLengthDialogShown = false },
            initialTime =LocalTime.now(),
            onTimeChange = {

                setSelectedTime(it)
                isTimeLengthDialogShown = false
                timeLengthState = it.toString()
            },
            title = { Text(text = "Select time") }
        )
    }


    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    Column(modifier = Modifier.verticalScroll(rememberScrollState()).background(color=SocialTheme.colors.uiBackground), horizontalAlignment = Alignment.CenterHorizontally){
        CreateClickableTextField(
            onClick = {  focusManager.clearFocus()
                isTimeLengthDialogShown = true },
            modifier = Modifier,
            title = "Duration",
            text=      if (!timeLengthState.split(":")[0].equals("00")) {
                if (timeLengthState.split(":")[0].equals("01")) {
                    timeLengthState.split(":")[0].toInt()
                        .toString() + " hour " + " " + timeLengthState.split(":")[1].toInt()
                        .toString() + " minutes"

                } else {
                    timeLengthState.split(":")[0].toInt()
                        .toString() + " hours " + " " + timeLengthState.split(":")[1].toInt()
                        .toString() + " minutes"
                }
            } else {

                timeLengthState.split(":")[1].toInt().toString() + " " + " minutes"
            },

            icon = R.drawable.ic_hourglass,
            description ="Select the time for your activity"

        )
        CurrentLocationTextField( onClick = {  focusManager.clearFocus()},
            modifier = Modifier,
            title = "Set current location",
            value=  "" ,
            icon = R.drawable.ic_my_location,onEvent={
                when(it){
                    is LiveEvent.SetCurrentLocation->{
                        currentLocation.value=it.latLng}      else->{}
                }

            },activeUsersViewModel)

        Spacer(modifier = Modifier.height(48.dp))
        CreateActivityButton(onClick = {
            onEvent(
                LiveEvent.CreateActiveUser(
                    start_time=timeState.toString(),
                    time_length=timeLengthState.toString(),
                    latLng=if(currentLocation.value==null){""}else{
                        currentLocation.value!!.latitude.toString()+"/"+currentLocation.value!!.longitude.toString()}
                )
            )
        }, text = "Share live activity", modifier = Modifier)
    }

}

@Composable
fun CurrentLocationTextField(onClick: (Int) -> Unit,
                             modifier: Modifier,
                             title: String,
                             value:String="value",
                             icon: Int, onEvent: (LiveEvent) -> Unit, viewModel: ActiveUsersViewModel) {
    val permission_flow = viewModel.granted_permission.collectAsState()
    val mContext = LocalContext.current
    val location_flow = viewModel.location.collectAsState()
    val checkedState = remember { mutableStateOf(false) }
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Row(
                modifier = Modifier.padding(24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = icon),
                    contentDescription = null,
                    tint = SocialTheme.colors.iconSecondary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    fontFamily = Inter,
                    fontWeight = FontWeight.Normal,
                    fontSize = 16.sp,
                    color = SocialTheme.colors.iconSecondary
                )
                Spacer(modifier = Modifier.weight(1f))

                Switch(
                    checked = checkedState.value, colors = SwitchDefaults.colors(
                        checkedThumbColor = SocialTheme.colors.textPrimary,
                        checkedTrackColor = SocialTheme.colors.textPrimary,
                        disabledCheckedTrackColor = SocialTheme.colors.uiFloated,
                        disabledCheckedThumbColor = SocialTheme.colors.uiFloated,
                        disabledUncheckedThumbColor = SocialTheme.colors.uiFloated,
                        disabledUncheckedTrackColor = SocialTheme.colors.uiFloated,
                        uncheckedThumbColor = SocialTheme.colors.uiFloated,
                        uncheckedTrackColor = SocialTheme.colors.uiFloated
                    ),

                    onCheckedChange = {slideValue->
                        permission_flow.value.let {permission_granted->
                            if (permission_granted){
                                location_flow.value.let {latLng ->
                                    if (latLng != null) {
                                        if(slideValue){
                                            onEvent(LiveEvent.SetCurrentLocation(latLng))
                                            checkedState.value = slideValue
                                        }else{

                                            checkedState.value = slideValue
                                        }
                                    }else{
                                        Toast.makeText(
                                            mContext,
                                            "Error - Location might be turned off",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }

                                }
                            }else{
                                onEvent(LiveEvent.AskForPermission)
                                Toast.makeText(
                                    mContext,
                                    "App location permission missing",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }

                      }
                )
            }

            Divider()
        }

}

@Composable
fun activityPickerLive(isDark:Boolean,modifier: Modifier = Modifier, onEvent: (LiveEvent) -> Unit) {
    Row(modifier = Modifier
        .fillMaxWidth()
        .height(48.dp)
        .padding(horizontal = 8.dp), horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically) {

        cardHighlited(text="Live", isDark = isDark )
        Spacer(Modifier.width(6.dp))
        cardnotHighlited(text="Activity",onEvent=  {onEvent(LiveEvent.GoToActivity)} )
        Spacer(Modifier.width(6.dp))
        cardnotHighlited(text="Event",onEvent=  {onEvent(LiveEvent.GoToEvent)} )
    }

}

