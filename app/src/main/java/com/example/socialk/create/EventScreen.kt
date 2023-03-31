package com.example.socialk.create

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Surface
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import com.example.socialk.Create
import com.example.socialk.Destinations
import com.example.socialk.R
import com.example.socialk.components.BottomBar
import com.example.socialk.home.cardHighlited
import com.example.socialk.home.cardnotHighlited
import com.example.socialk.ui.theme.SocialTheme

sealed class EventEvent{
    object GoToProfile : EventEvent()
    object LogOut : EventEvent()
    object GoToSettings : EventEvent()
    object GoToHome : EventEvent()
    object GoToLive : EventEvent()
    object GoToEvent : EventEvent()
    object GoToActivity: EventEvent()
}
@Composable
fun EventScreen (onEvent: (EventEvent) -> Unit, bottomNavEvent:(Destinations)->Unit){
    val activityTextState by rememberSaveable(stateSaver = ActivityTextStateSaver) {
        mutableStateOf(ActivityTextFieldState())
    }
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    var value by rememberSaveable { mutableStateOf("initial value") }
    Surface(modifier = Modifier
        .fillMaxSize()
        .background(SocialTheme.colors.uiBackground),color= SocialTheme.colors.uiBackground
    ) {

        Column(
            modifier = Modifier.verticalScroll(rememberScrollState())
                .fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(12.dp))
            // TOP SECTION PICKER
            activityPickerEvent(isSystemInDarkTheme(), onEvent = { event -> onEvent(event) })

            Spacer(modifier = Modifier.height(12.dp))
            //TEXT FIELD

            EditTextField(textState=activityTextState,
                modifier = Modifier, title = "Text",
                icon = R.drawable.ic_edit, focusManager = focusManager, onClick = {},
            onSaveValueCall = {

            })

/*
            CreateClickableTextField(
                modifier = Modifier,
                onClick = {},
                title = "Date",
                icon = R.drawable.ic_calendar
            )

            CreateClickableTextField(
                modifier = Modifier,
                onClick = {},
                title = "Start time",
                icon = R.drawable.ic_schedule
            )

            CreateClickableTextField(
                onClick = { },
                modifier = Modifier,
                title = "Time length",
                icon = R.drawable.ic_hourglass
            )


            Spacer(modifier = Modifier.height(48.dp))
            CreateActivityButton(onClick = {},"Create activity")
            Spacer(modifier = Modifier.height(64.dp))
            */
        }


        Box(modifier = Modifier
            .fillMaxWidth()
            .heightIn(56.dp)
            .padding(vertical = 16.dp), contentAlignment = Alignment.TopEnd){


        }

        BottomBar( onTabSelected = { screen->bottomNavEvent(screen)},currentScreen = Create)

    }
}
@Composable
fun activityPickerEvent(isDark:Boolean,modifier: Modifier = Modifier, onEvent: (EventEvent) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .padding(horizontal = 8.dp), horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {


        cardnotHighlited(text = "Live", onEvent = { onEvent(EventEvent.GoToLive) })
        Spacer(Modifier.width(6.dp))
        cardnotHighlited(text = "Activity", onEvent = { onEvent(EventEvent.GoToActivity) })
        Spacer(Modifier.width(6.dp))
        cardHighlited(text = "Event", isDark = isDark)
    }
}

