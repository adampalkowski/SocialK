package com.example.socialk.home

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.activity.OnBackPressedCallback
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.*
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.socialk.*
import com.example.socialk.R
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
import com.example.socialk.ui.theme.SocialTheme
import com.google.accompanist.systemuicontroller.SystemUiController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executor

sealed class ActivityEvent() {
    class OpenActivitySettings(activity: Activity) : ActivityEvent() {
        val activity = activity
    }
    class GoToProfile(user_id: String) : ActivityEvent() {
        val user_id = user_id
    }

    class OpenActivityChat(activity: Activity) : ActivityEvent() {
        val activity = activity
    }

    class ActivityLiked(activity: Activity) : ActivityEvent() {
        val activity = activity
    }

    class ActivityUnLiked(activity: Activity) : ActivityEvent() {
        val activity = activity
    }
    class GoToMap( latlng: String) : ActivityEvent() {
        val latlng = latlng
    }
    object OpenCamera: ActivityEvent()
}

sealed class HomeEvent {
    object GoToProfile : HomeEvent()
    object OpenCamera : HomeEvent()
    object LogOut : HomeEvent()
    object GoToMemories : HomeEvent()
    object BackPressed : HomeEvent()
    object GoToSettings : HomeEvent()
    class GoToMap ( latlng: String): HomeEvent(){
        val latlng=latlng
    }
    class GoToChat(activity: Activity) : HomeEvent() {
        val activity = activity
    }
    class ActivityLiked(activity: Activity) : HomeEvent() {
        val activity = activity
    }
    class ActivityUnLiked(activity: Activity) : HomeEvent() {
        val activity = activity
    }
    class GoToProfileWithID(user_id: String) : HomeEvent() {
        val user_id = user_id
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun HomeScreen(systemUiController: SystemUiController,
    activeUsersViewModel: ActiveUsersViewModel?,
    activityViewModel: ActivityViewModel?,
    chatViewModel: ChatViewModel,
    viewModel: AuthViewModel?,homeViewModel:HomeViewModel?,
    onEvent: (HomeEvent) -> Unit,
    bottomNavEvent: (Destinations) -> Unit
) {
    //set status bar TRANSPARENT
    SideEffect {
        systemUiController.setStatusBarColor(color = androidx.compose.ui.graphics.Color.Transparent)
        systemUiController.setNavigationBarColor(color = androidx.compose.ui.graphics.Color.Transparent)
    }
    val backCallback = remember { // remember the callback to avoid recompositions
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                onEvent(HomeEvent.BackPressed)
            }
        }
    }
    val openDialog = remember { mutableStateOf(false)  }
    var bottomSheetState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)
    val scope = rememberCoroutineScope()
    var bottomSheetType by remember {
        mutableStateOf("settings")
    }
    val isDark = isSystemInDarkTheme()
    val scaffoldState = rememberScaffoldState()
    val showDialogState: Boolean by homeViewModel?.showDialog!!.collectAsState()
    var bottomSheetActivity by rememberSaveable{ mutableStateOf(Activity()) }

    androidx.compose.material.Scaffold(modifier=Modifier.systemBarsPadding(),
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
                    activityViewModel = activityViewModel,
                    padding = it,
                    isDark = isDark,
                    activityEvent = {
                        when (it) {
                            is ActivityEvent.OpenActivityChat -> {
                                onEvent(HomeEvent.GoToChat(it.activity))
                            }
                            is ActivityEvent.ActivityLiked -> {
                                Log.d("HomeScreen","like")
                                onEvent(HomeEvent.ActivityLiked(it.activity))
                            }
                            is ActivityEvent.ActivityUnLiked -> {
                                Log.d("HomeScreen","dislike")
                                onEvent(HomeEvent.ActivityUnLiked(it.activity))

                            }
                            is ActivityEvent.GoToMap -> {
                                onEvent(HomeEvent.GoToMap(latlng = it.latlng))

                            }
                            is ActivityEvent.GoToProfile -> {
                                onEvent(HomeEvent.GoToProfileWithID(user_id = it.user_id))

                            }
                            is ActivityEvent.OpenCamera -> {
                                onEvent(HomeEvent.OpenCamera)

                            }
                            is ActivityEvent.OpenActivitySettings -> {
                                Log.d("HomeScreen","open settings")
                                bottomSheetType = "settings"
                                bottomSheetActivity = it.activity
                                scope.launch {
                                    bottomSheetState.show()
                                }
                            }
                        }

                    },
                    onEvent = { homeEvent ->

                        onEvent(homeEvent)
                    })
            }

        })

    activityViewModel?.activityState?.value.let {   event->
     when(event){
            is Response.Success->{
                Log.d("Homefragment2",event.toString())
                if(event.data!=null){
                  homeViewModel?.setActivity(event.data)
                    homeViewModel?.setShowDialog(true)
                }

            }
            is Response.Loading->{

            }
            is Response.Failure->{

            }
         else->{}
        }
    }
    activityDialog(activity =homeViewModel?.activity?.value , activityDialogState =showDialogState,onEvent={
        homeViewModel?.setShowDialog(false)
        activityViewModel?.resetActivityState()
        homeViewModel?.removeActivity()
        Log.d("homescreen","falseeee")
    })

    BottomDialog(
        state = bottomSheetState,
        activity = bottomSheetActivity,
        type = bottomSheetType,
        onEvent = { event ->
            when (event) {
                is BottomDialogEvent.SendMessage -> {
                    chatViewModel.addMessage(
                        bottomSheetActivity.id,
                        //todo set sender picture_url
                        ChatMessage(
                            text = event.message,
                            sender_picture_url = UserData.user?.pictureUrl!!,
                            sent_time = "",
                            sender_id = UserData.user!!.id,
                            message_type = "text",
                            id = ""
                        )
                    )
                }
                is BottomDialogEvent.removeUserFromActivity->{
                    activityViewModel?.removeUserFromActivityInvites(bottomSheetActivity,UserData.user!!.id!!)
                    activityViewModel?.getActivitiesForUser(viewModel?.currentUser!!.uid)

                }
                is BottomDialogEvent.LeaveActivity->{
                    activityViewModel?.unlikeActivity(
                        event.activity.id,
                        UserData.user!!
                    )
                }
                else->{}

            }
        },
        chatViewModel = chatViewModel
    )

}


@Composable
fun activityDialog(activity:Activity?, activityDialogState: Boolean, onEvent: () -> Unit){
   if (activityDialogState){
       ActivityDialog(     onDismiss = onEvent,
           onConfirm = {  },
           onCancel = {
               onEvent()

           },
           title ="asd",
           info ="ass",
           icon = R.drawable.ic_delete,
           activity =activity!!)
   }else{

   }

}


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun HomeScreenContent(
    activityEvent: (ActivityEvent) -> Unit, activeUsersViewModel: ActiveUsersViewModel?,
    viewModel: AuthViewModel?,
    activityViewModel: ActivityViewModel?,
    padding: PaddingValues,
    isDark: Boolean,
    onEvent: (HomeEvent) -> Unit,

) {

    val refreshScope = rememberCoroutineScope()
    var refreshing by remember { mutableStateOf(false) }
    var activitiesExist =remember { mutableStateOf(false) }
    fun refresh() = refreshScope.launch {
        refreshing = true
        activityViewModel?.getActivitiesForUser(viewModel?.currentUser!!.uid)

    }

    val state = rememberPullRefreshState(refreshing, ::refresh)
    Box(modifier = Modifier
        .fillMaxSize()
        .pullRefresh(state)) {
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
                                        profileUrls = ActiveUser.participants_profile_pictures,
                                        usernames = ActiveUser.participants_usernames
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                }
                            }
                        }
                    }
                    else->{}
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
                        refreshing = false
                        //display activities
                        Log.d("homescreen", it.data.toString())
                        items(it.data) { item ->
                            ActivityItem(
                                activity = item,
                                onEvent = activityEvent,
                                username = item.creator_username,
                                profilePictureUrl = item.creator_profile_picture,
                                timeLeft = item.time_left,
                                title = item.title,
                                description = item.description,
                                date = item.date,
                                liked= item.participants_usernames.containsKey(UserData.user!!.id!!),
                                //todo add the time end
                                timePeriod = item.start_time + " - " + item.end_time,
                                custom_location = item.custom_location,
                                location=item.location
                            )

                        }
                        activitiesExist.value=true
                        }
                    else->{}
                }
            }
            activityViewModel?.moreActivitiesListState?.value.let {
                when (it) {
                    is Response.Success -> {
                        refreshing = false
                        //display activities
                        Log.d("homescreen", it.data.toString())
                        items(it.data) { item ->
                            ActivityItem(
                                activity = item,
                                onEvent = activityEvent,
                                username = item.creator_username,
                                profilePictureUrl = item.creator_profile_picture,
                                timeLeft = item.time_left,
                                title = item.title,
                                description = item.description,
                                date = item.date,
                                liked= item.participants_usernames.containsKey(UserData.user!!.id!!),
                                //todo add the time end
                                timePeriod = item.start_time + " - " + item.end_time,
                                custom_location = item.custom_location,
                                location=item.location
                            )

                        }
                    }
                    else->{}
                }
            }
            item {
                LaunchedEffect(true ){
                    if (activitiesExist.value){
                        activityViewModel?.getMoreActivitiesForUser(UserData.user!!.id)
                    }
                }
            }
            //space for bottom bar
            item {
                Spacer(modifier = Modifier.height(56.dp))
            }
        }

        PullRefreshIndicator(
            refreshing,
            state,
            Modifier
                .align(Alignment.TopCenter)
                .padding(top = 64.dp),
            backgroundColor = SocialTheme.colors.uiBackground,
            contentColor = SocialTheme.colors.textPrimary
        )
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
                text = text, color = Color(0xffB0B0B0), style =  com.example.socialk.ui.theme.Typography.body2
                , textAlign = TextAlign.Center
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
            color = if (isDark) Color.White else Color(0xFF25232A),
            modifier = Modifier,
            style =  com.example.socialk.ui.theme.Typography.body1,
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



