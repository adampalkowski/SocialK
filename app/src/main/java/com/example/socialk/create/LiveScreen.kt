package com.example.socialk.create

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Surface
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.socialk.Create
import com.example.socialk.Destinations
import com.example.socialk.R
import com.example.socialk.bottomTabRowScreens
import com.example.socialk.components.BottomBar
import com.example.socialk.components.BottomBarRow
import com.example.socialk.components.timepicker.TimeSelection
import com.example.socialk.components.timepicker.rememberSheetState
import com.example.socialk.di.ActiveUsersViewModel
import com.example.socialk.home.cardHighlited
import com.example.socialk.home.cardnotHighlited
import com.example.socialk.model.Response
import com.example.socialk.ui.theme.SocialTheme
import com.marosseleng.compose.material3.datetimepickers.time.domain.noSeconds
import com.marosseleng.compose.material3.datetimepickers.time.ui.dialog.TimePickerDialog
import java.time.LocalDate
import java.time.LocalTime


sealed class LiveEvent{
    object GoToProfile : LiveEvent()
    object LogOut : LiveEvent()
    object GoToSettings : LiveEvent()
    object GoToHome : LiveEvent()
    object GoToLive : LiveEvent()
    object GoToEvent : LiveEvent()
    object GoToActivity: LiveEvent()
    object ClearState: LiveEvent()
    data class CreateActiveUser(
        val start_time: String,
        val time_length: String
    ): LiveEvent()

}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LiveScreen (activeUsersViewModel:ActiveUsersViewModel?,onEvent: (LiveEvent) -> Unit, bottomNavEvent:(Destinations)->Unit){

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
    if (isTimeDialogShown) {
        TimePickerDialog(
            onDismissRequest = { isTimeDialogShown = false },
            initialTime = selectedTimeinit,
            onTimeChange = {
                setSelectedTime(it)
                isTimeDialogShown = false
                timeState = it.toString()
            },
            title = { Text(text = "Select time") }
        )
    }

    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current


    Surface(modifier = Modifier
        .fillMaxSize()
        .background(SocialTheme.colors.uiBackground),color= SocialTheme.colors.uiBackground
    ) {

        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            activityPickerLive(isSystemInDarkTheme(), onEvent = { event -> onEvent(event) })
            Spacer(modifier = Modifier.height(12.dp))


            CreateClickableTextField(
                modifier = Modifier,
                onClick = {   focusManager.clearFocus()
                    isTimeDialogShown = true },
                title = "Start time",
                icon = R.drawable.ic_schedule,
                value=timeState,

                )

            CreateClickableTextField(
                onClick = {  focusManager.clearFocus()
                    isTimeLengthDialogShown = true },
                modifier = Modifier,
                title = "Time length",
                value=      if (!timeLengthState.split(":")[0].equals("00")) {
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

                icon = R.drawable.ic_hourglass
            )

            Spacer(modifier = Modifier.height(48.dp))
            CreateActivityButton(onClick = {
                onEvent(
                    LiveEvent.CreateActiveUser(
                         start_time=timeState.toString(),
                         time_length=timeLengthState.toString()
                    )
                )
            }, text = "Create activity")
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

    activeUsersViewModel?.isActiveUsersAddedState?.value.let {
        when(it){
            is Response.Loading-> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center){
                CircularProgressIndicator()
            }
            is Response.Success->  {onEvent(LiveEvent.GoToHome)
                onEvent(LiveEvent.ClearState)}
            is Response.Failure-> Box(modifier = Modifier.fillMaxSize()){
                androidx.compose.material3.Text(text = "FAILURE", fontSize = 50.sp)
            }
        }
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

