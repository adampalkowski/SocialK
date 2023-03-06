package com.example.socialk.create

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.socialk.Chats
import com.example.socialk.Home
import com.example.socialk.Main.Screen
import com.example.socialk.Main.navigate
import com.example.socialk.Map
import com.example.socialk.Profile
import com.example.socialk.di.ActivityViewModel
import com.example.socialk.di.ChatViewModel
import com.example.socialk.di.UserViewModel
import com.example.socialk.model.Activity
import com.example.socialk.model.Chat
import com.example.socialk.model.User
import com.example.socialk.model.UserData
import com.example.socialk.signinsignup.AuthViewModel
import com.example.socialk.ui.theme.SocialTheme
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

@AndroidEntryPoint
class CreateFragment : Fragment() {
    private val viewModel by viewModels<CreateViewModel>()
    private val activityViewModel by viewModels<ActivityViewModel>()
    private val authViewModel by viewModels<AuthViewModel>()
    private val userViewModel by viewModels<UserViewModel>()
    private val chatViewModel by viewModels<ChatViewModel>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        activityViewModel.activityAdded()
        viewModel.navigateTo.observe(viewLifecycleOwner) { navigateToEvent ->
            navigateToEvent.getContentIfNotHandled()?.let { navigateTo ->
                if (navigateTo==Screen.FriendsPicker){
                    val bundle=Bundle()
                    bundle.putSerializable("activity",viewModel.created_activity.value)
                    navigate(navigateTo, Screen.Create,bundle)

                }else{
                    navigate(navigateTo, Screen.Create)

                }
            }

        }
        var location:String?=arguments?.getString("location")
        userViewModel.getFriends(authViewModel.currentUser!!.uid)

        Log.d("mapscreen",location.toString())



        return ComposeView(requireContext()).apply {
            setContent {
                SocialTheme {
                    CreateScreen(location,userViewModel,activityViewModel, onEvent = { event ->
                        when (event) {
                            is CreateEvent.GoToProfile -> viewModel.handleGoToProfile()
                            is CreateEvent.GoToHome -> {

                                viewModel.handleGoToHome()
                            }
                            is CreateEvent.LogOut -> viewModel.handleLogOut()
                            is CreateEvent.ClearState -> activityViewModel.activityAdded()
                            is CreateEvent.GoToSettings -> viewModel.handleGoToSettings()
                            is CreateEvent.GoToEvent -> viewModel.handleGoToEvent()
                            is CreateEvent.GoToLive -> viewModel.handleGoToLive()
                            is CreateEvent.GoToActivity -> viewModel.handleGoToActivity()
                            is CreateEvent.GoToMap -> viewModel.handleGoToMap()
                            is CreateEvent.CreateActivity -> {

                                val uuid: UUID = UUID.randomUUID()
                                val id: String = uuid.toString()
                                val participants_profile_pictures: HashMap<String,String> = hashMapOf()
                                val participants_usernames: HashMap<String,String> = hashMapOf()
                                val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                                val current = LocalDateTime.now().format(formatter)

                                participants_profile_pictures[authViewModel.currentUser!!.uid]=UserData.user!!.pictureUrl!!
                                participants_usernames[authViewModel.currentUser!!.uid]=UserData.user!!.username!!

                                val activity=Activity(
                                    id = id,
                                    creator_id = if (authViewModel.currentUser == null) {
                                        ""
                                    } else {
                                        authViewModel.currentUser!!.uid.toString()
                                    },
                                    title = event.title,
                                    date = event.date,
                                    start_time = event.start_time,
                                    time_length = event.time_length,
                                    creator_name =UserData.user!!.name!! ,
                                    creator_profile_picture = UserData.user!!.pictureUrl!! ,
                                    creator_username = UserData.user!!.username!! ,
                                    description= event.description,
                                    time_left = "",
                                    custom_location = event.custom_location,
                                    end_time = "",latLng="",minUserCount=if(event.min.equals("")){0}else{event.min.toInt()},maxUserCount=if(event.max.equals("")){0}else{event.max.toInt()},
                                    disableChat = false, disableMemories = false, likes = 0,
                                    invited_users = arrayListOf(),
                                    participants_profile_pictures =participants_profile_pictures ,
                                    participants_usernames =participants_usernames,
                                    creation_time = current,
                                    location=event.location
                                )

                                viewModel.handleGoToFriendsPicker(activity)



                            }
                            else->{}
                        }
                    },
                        bottomNavEvent = { screen ->
                            when (screen) {
                                is Home -> viewModel.handleGoToHome()
                                is Map -> viewModel.handleGoToMap()
                                is Chats -> viewModel.handleGoToChats()
                                is Profile -> viewModel.handleGoToProfile()
                                else->{}

                            }
                        })
                }
            }
        }
    }

}
