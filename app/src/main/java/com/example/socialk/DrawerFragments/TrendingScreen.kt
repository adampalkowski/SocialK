package com.example.socialk.DrawerFragments

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.socialk.components.ActivityItem
import com.example.socialk.di.ActivityViewModel
import com.example.socialk.model.Response
import com.example.socialk.model.UserData

@Composable
fun TrendingScreen(activityViewModel: ActivityViewModel){
    val context = LocalContext.current

    Surface(Modifier.fillMaxSize()) {
        LazyColumn {
            activityViewModel.trendingActivitiesListState.value.let {response->
                when(response){
                    is Response.Success->{
                        Toast.makeText(context,"success",Toast.LENGTH_SHORT).show()
                        Log.d("TrendingSCreen",response.data.toString())
                        items(response.data){activity->
                            ActivityItem(
                                activity = activity,
                                username =activity.creator_username ,
                                profilePictureUrl =activity.creator_profile_picture ,
                                timeLeft = activity.time_left,
                                title = activity.title,
                                description =activity.description ,
                                date = activity.date,
                                timePeriod = activity.time_length,
                                custom_location = activity.custom_location,
                                location = activity.location,
                                liked = if(activity.participants_usernames.containsKey(UserData.user!!.id) ) true else false,
                                onEvent = {}
                            )
                        }
                    }
                    is Response.Failure->{

                    }
                    is Response.Loading->{
                        Toast.makeText(context,"loading",Toast.LENGTH_SHORT).show()
                    }
                }
            }

        }
    }

}