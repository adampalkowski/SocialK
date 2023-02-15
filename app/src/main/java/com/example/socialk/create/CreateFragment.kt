package com.example.socialk.create

import android.os.Bundle
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
import com.example.socialk.di.UserViewModel
import com.example.socialk.home.HomeViewModel
import com.example.socialk.model.Activity
import com.example.socialk.model.UserData
import com.example.socialk.signinsignup.AuthViewModel
import com.example.socialk.ui.theme.SocialTheme
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class CreateFragment : Fragment() {
    private val viewModel by viewModels<CreateViewModel>()
    private val activityViewModel by viewModels<ActivityViewModel>()
    private val authViewModel by viewModels<AuthViewModel>()
    private val userViewModel by viewModels<UserViewModel>()
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
        userViewModel.getFriends(authViewModel.currentUser!!.uid)
        return ComposeView(requireContext()).apply {
            setContent {
                SocialTheme {
                    CreateScreen(userViewModel,activityViewModel, onEvent = { event ->
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
                                        description= "",
                                        time_left = "",
                                        end_time = "",latLng="",minUserCount=0,maxUserCount=100,
                                        disableChat = false, disableMemories = false, likes = 0

                                    )
                                )

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
