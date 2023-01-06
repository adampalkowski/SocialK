package com.example.socialk

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.window.isPopupLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.socialk.Main.Screen
import com.example.socialk.Main.navigate
import com.example.socialk.chat.ChatEvent
import com.example.socialk.chat.ChatScreen
import com.example.socialk.chat.ChatViewModel
import com.example.socialk.home.HomeEvent
import com.example.socialk.home.HomeScreen
import com.example.socialk.ui.theme.SocialTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFragment :Fragment(){
    private val viewModel by viewModels<ProfileViewModel>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel.navigateTo.observe(viewLifecycleOwner) { navigateToEvent ->
            navigateToEvent.getContentIfNotHandled()?.let { navigateTo ->
                navigate(navigateTo, Screen.Profile)
            }
        }
        return ComposeView(requireContext()).apply {
            setContent {
                SocialTheme {
                    ProfileScreen(
                        onEvent = { event ->
                            when (event) {
                                is ProfileEvent.GoToProfile -> viewModel.handleGoToProfile()
                                is ProfileEvent.LogOut -> viewModel.handleLogOut()
                                is ProfileEvent.GoToSettings -> viewModel.handleGoToSettings()
                            }
                        },
                        bottomNavEvent  ={screen->
                            when (screen) {
                                is Home -> viewModel.handleGoToHome()
                                is Map -> viewModel.handleGoToMap()
                                is Chats -> viewModel.handleGoToChats()
                                is Create -> viewModel.handleGoToCreate()

                            }
                        }
                    )
                }
            }
        }
    }

}