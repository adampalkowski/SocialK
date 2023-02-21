package com.example.socialk.create

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import androidx.core.view.WindowCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.viewModelScope
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.fragment.findNavController
import com.example.socialk.*
import com.example.socialk.Main.Screen
import com.example.socialk.Main.navigate
import com.example.socialk.Map
import com.example.socialk.di.ActivityViewModel
import com.example.socialk.di.ChatViewModel
import com.example.socialk.di.UserViewModel
import com.example.socialk.home.HomeViewModel
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
import kotlin.collections.HashMap

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
                navigate(navigateTo, Screen.Create)
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
                            is CreateEvent.CreateActivity -> {
                                val uuid: UUID = UUID.randomUUID()
                                val id: String = uuid.toString()
                                //todo what if current user is null
                                val userList: List<User> =event.invited_users

                                val userIdList: ArrayList<String> = arrayListOf()
                                val participants_profile_pictures: HashMap<String,String> = hashMapOf()
                                val participants_usernames: HashMap<String,String> = hashMapOf()
                                for (user in userList) {
                                    userIdList.add(user.id.toString())
                                }
                                val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                                val current = LocalDateTime.now().format(formatter)
                                userIdList.add(authViewModel.currentUser!!.uid.toString())
                                participants_profile_pictures[authViewModel.currentUser!!.uid]=UserData.user!!.pictureUrl!!
                                participants_usernames[authViewModel.currentUser!!.uid]=UserData.user!!.username!!
                                activityViewModel.addActivity(
                                    Activity(
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
                                        invited_users = userIdList,
                                        participants_profile_pictures =participants_profile_pictures ,
                                        participants_usernames =participants_usernames,
                                        creation_time = current
                                    )
                                )

                                val chat = Chat(create_date = current,
                                    owner_id =id,
                                    id =id,
                                    chat_name =event.title,
                                    chat_picture = UserData.user!!.pictureUrl!! ,
                                    recent_message =null,
                                    recent_message_time =current,
                                    type ="activity",
                                    members = arrayListOf(UserData.user!!.id),
                                    user_one_username =null,
                                    user_two_username =null,
                                    user_one_profile_pic =null,
                                    user_two_profile_pic = null,
                                highlited_message = "")
                                chatViewModel.addChatCollection(chat)

                            }
                        }
                    },
                        bottomNavEvent = { screen ->
                            when (screen) {
                                is Home -> viewModel.handleGoToHome()
                                is Map -> viewModel.handleGoToMap()
                                is Chats -> viewModel.handleGoToChats()
                                is Profile -> viewModel.handleGoToProfile()
                            }
                        })
                }
            }
        }
    }

}
