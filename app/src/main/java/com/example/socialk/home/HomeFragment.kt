package com.example.socialk.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.socialk.Chats
import com.example.socialk.Create
import com.example.socialk.Home
import com.example.socialk.Main.Screen
import com.example.socialk.Main.navigate
import com.example.socialk.Profile
import com.example.socialk.di.ActiveUsersViewModel
import com.example.socialk.di.ActivityViewModel
import com.example.socialk.signinsignup.AuthViewModel
import com.example.socialk.ui.theme.SocialTheme
import dagger.hilt.android.AndroidEntryPoint
@AndroidEntryPoint
class HomeFragment:Fragment() {
    private val viewModel by viewModels<HomeViewModel>()
    private val authViewModel by viewModels<AuthViewModel>()
    private val activityViewModel by viewModels<ActivityViewModel>()
    private val activeUsersViewModel by viewModels<ActiveUsersViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        viewModel.navigateTo.observe(viewLifecycleOwner) { navigateToEvent ->
            navigateToEvent.getContentIfNotHandled()?.let { navigateTo ->
                navigate(navigateTo, Screen.Home)
            }
        }
        activityViewModel?.getActivitiesForUser(authViewModel?.currentUser?.uid)
        activeUsersViewModel?.getActiveUsersForUser(authViewModel?.currentUser?.uid)

        return ComposeView(requireContext()).apply {
            setContent {

                SocialTheme {
                    HomeScreen(activeUsersViewModel,activityViewModel,authViewModel,
                        onEvent = { event ->
                            when (event) {
                                is HomeEvent.GoToProfile -> viewModel.handleGoToProfile()
                                is HomeEvent.LogOut -> viewModel.handleLogOut()
                                is HomeEvent.GoToSettings -> viewModel.handleGoToSettings()
                                is HomeEvent.GoToMemories -> viewModel.handleGoToMemories()
                            }
                        },
                        bottomNavEvent  ={screen->
                            when (screen) {
                                is Home -> viewModel.handleGoToHome()
                                is com.example.socialk.Map -> viewModel.handleGoToMap()
                                is Chats -> viewModel.handleGoToChats()
                                is Profile ->viewModel.handleGoToProfile()
                                is Create -> viewModel.handleGoToCreate()
                            }
                        }
                    )
                }
            }
        }
    }

}