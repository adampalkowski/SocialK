package com.example.socialk.create

import android.content.res.Configuration
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
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
import java.time.LocalDate


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
@Composable
fun LiveScreen (activeUsersViewModel:ActiveUsersViewModel?,onEvent: (LiveEvent) -> Unit, bottomNavEvent:(Destinations)->Unit){

    var isDateDialogShown: Boolean by rememberSaveable {
        mutableStateOf(false)
    }
    var date: LocalDate? by remember {
        mutableStateOf(null)
    }
    var dateState by rememberSaveable {
        mutableStateOf(LocalDate.now().toString())
    }
    if (isDateDialogShown) {
        DatePicker(onDismissRequest = { isDateDialogShown = false },
            onDateChange = {
                date = it
                isDateDialogShown = false
                dateState = date.toString()

            })
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
                    isDateDialogShown = true },
                title = "Start time",
                icon = R.drawable.ic_schedule,
                value=dateState,

                )

            CreateClickableTextField(
                onClick = {  focusManager.clearFocus()
                    isDateDialogShown = true },
                modifier = Modifier,
                title = "Time length",
                value=dateState,

                icon = R.drawable.ic_hourglass
            )


            Spacer(modifier = Modifier.height(48.dp))
            CreateActivityButton(onClick = {
                onEvent(
                    LiveEvent.CreateActiveUser(
                         start_time=dateState.toString(),
                         time_length=dateState.toString()
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
                currentScreen = Create
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

    @Preview(showBackground = true)
@Composable
fun previewLiveScreen () {
    SocialTheme{
        LiveScreen(onEvent = {}, bottomNavEvent = {}, activeUsersViewModel = null)
    }
}
@Preview(showBackground = true,uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun previewLiveScreenDark(){
    SocialTheme{
        LiveScreen(onEvent = {}, bottomNavEvent = {}, activeUsersViewModel = null)
    }
}
