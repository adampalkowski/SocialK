package com.example.socialk.DrawerFragments

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.socialk.chat.ChatComponents.Divider
import com.example.socialk.components.ActivityItem
import com.example.socialk.components.HomeScreenHeading
import com.example.socialk.di.ActivityViewModel
import com.example.socialk.model.Response
import com.example.socialk.ui.theme.SocialTheme


sealed class CreatedActivityEvent{
    object GoBack:CreatedActivityEvent()
}


@Composable
fun CreatedActivitiesScreen(activityViewModel: ActivityViewModel,onEvent:(CreatedActivityEvent)->Unit){
    Box(
        Modifier
            .fillMaxSize()
            .background(color = SocialTheme.colors.uiBackground)){
        Column {
            HomeScreenHeading(onEvent={onEvent(CreatedActivityEvent.GoBack)},title="Created activities")
            Divider()
            LazyColumn{
                activityViewModel.userActivitiesState.value.let {response->
                    when(response){
                        is Response.Success->{
                            items(response.data){activity->
                                ActivityItem(
                                    activity = activity,
                                    username = activity.creator_username,
                                    profilePictureUrl = activity.creator_profile_picture,
                                    timeLeft = activity.time_left,
                                    title = activity.title,
                                    description =activity.description ,
                                    date = activity.date,
                                    timePeriod = activity.time_length,
                                    custom_location =activity .custom_location,
                                    location = activity.location,
                                    liked =false ,
                                    onEvent = {}
                                )
                            }

                        }
                        is Response.Loading->{}
                        is Response.Failure->{}
                    }
                }
            }
        }

    }


}