package com.example.socialk.home

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.util.Log
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material3.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.*
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.socialk.*
import com.example.socialk.R
import com.example.socialk.chat.ChatEvent
import com.example.socialk.components.*
import com.example.socialk.di.ActiveUsersViewModel
import com.example.socialk.di.ActivityViewModel
import com.example.socialk.di.ChatViewModel
import com.example.socialk.model.Activity
import com.example.socialk.model.ChatMessage
import com.example.socialk.model.Response
import com.example.socialk.model.UserData
import com.example.socialk.signinsignup.AuthViewModel
import com.example.socialk.ui.theme.Inter
import com.example.socialk.ui.theme.Ocean1
import com.example.socialk.ui.theme.SocialTheme
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.marosseleng.compose.material3.datetimepickers.time.domain.noSeconds
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalTime
import java.util.*
import java.util.concurrent.TimeUnit

sealed class ActivityEvent() {
    class OpenActivitySettings(activity: Activity) : ActivityEvent(){val activity=activity}
    class OpenActivityChat (activity: Activity): ActivityEvent(){val activity=activity}
}

sealed class HomeEvent {
    object GoToProfile : HomeEvent()
    object LogOut : HomeEvent()
    object GoToMemories : HomeEvent()
    object GoToSettings : HomeEvent()
    class GoToChat (activity: Activity): HomeEvent(){val activity=activity}
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun HomeScreen(
    activeUsersViewModel: ActiveUsersViewModel?,
    activityViewModel: ActivityViewModel?,
    chatViewModel: ChatViewModel,
    viewModel: AuthViewModel?,
    onEvent: (HomeEvent) -> Unit,
    bottomNavEvent: (Destinations) -> Unit
) {
    var bottomSheetState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)
    val scope = rememberCoroutineScope()
    var bottomSheetType by remember {
        mutableStateOf("settings")
    }
    val isDark = isSystemInDarkTheme()
    val scaffoldState = rememberScaffoldState()
    var bottomSheetActivity by remember { mutableStateOf(Activity()) }

    androidx.compose.material.Scaffold(
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
                   activityViewModel= activityViewModel,
                    padding = it,
                    isDark = isDark,
                    activityEvent = {
                       when(it) {
                           is ActivityEvent.OpenActivityChat->{
                               onEvent(HomeEvent.GoToChat(it.activity))

                           }
                           is ActivityEvent.OpenActivitySettings->{
                               bottomSheetType="settings"
                               bottomSheetActivity=it.activity

                           }
                       }
                        scope.launch {
                            bottomSheetState.show()
                        }
                    },
                    onEvent = { homeEvent ->

                        onEvent(homeEvent)
                    })
            }

        })
    BottomDialog(state = bottomSheetState,activity=bottomSheetActivity, type = bottomSheetType,onEvent={
            event ->
        when (event) {
            is BottomDialogEvent.SendMessage -> {
                chatViewModel.addMessage(bottomSheetActivity.id,
                    //todo set sender picture_url
                    ChatMessage(text = event.message, sender_picture_url = UserData.user?.pictureUrl!!
                        , sent_time ="" , sender_id = UserData.user!!.id, message_type ="text" ,id="") )
            }

        }
    }, chatViewModel = chatViewModel)

}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun HomeScreenContent(
    activityEvent: (ActivityEvent) -> Unit, activeUsersViewModel: ActiveUsersViewModel?,
    viewModel: AuthViewModel?,
    activityViewModel: ActivityViewModel?,
    padding: PaddingValues,
    isDark: Boolean,
    onEvent: (HomeEvent) -> Unit
) {

    Box(modifier = Modifier.fillMaxSize()) {
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
                                items(it.data) { ActiveUser ->
                                    Spacer(modifier = Modifier.width(12.dp))
                                    ActiveUserItem(
                                        //TODO onclick of the active user
                                        onClick = {},
                                        profileUrl = ActiveUser.pictureUrl,
                                        username = ActiveUser.username
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                }
                            }
                        }
                    }
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
            activityViewModel?.activitiesListState?.value.let {
                when (it) {
                    is Response.Success -> {
                        //display activities
                        items(it.data) { item ->
                            ActivityItem(activity=item,
                                onEvent = activityEvent,
                                username = item.creator_username,
                                profilePictureUrl = item.creator_profile_picture,
                                timeLeft = item.time_left,
                                title = item.title,
                                description = "",
                                date = item.date,
                                //todo add the time end
                                timePeriod = item.start_time + " - " + item.end_time,
                                location = item.title
                            )

                        }
                    }
                }
            }
            //space for bottom bar
            item {
                Spacer(modifier = Modifier.height(56.dp))
            }
        }


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

    Box(modifier = Modifier, contentAlignment = Alignment.BottomEnd) {

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
                text = text, fontSize = 14.sp, color = Color(0xffB0B0B0), style = TextStyle(
                    fontFamily = Inter,
                    fontWeight = FontWeight.Medium
                ), textAlign = TextAlign.Center
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
            fontSize = 14.sp,
            color = if (isDark) Color.White else Color(0xFF25232A),
            modifier = Modifier,
            style = TextStyle(fontFamily = Inter, fontWeight = FontWeight.SemiBold),
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



