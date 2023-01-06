package com.example.socialk.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.socialk.*
import com.example.socialk.Main.Screen
import com.example.socialk.Main.navigate
import com.example.socialk.Map
import com.example.socialk.home.HomeEvent
import com.example.socialk.home.HomeScreen
import com.example.socialk.map.MapViewModel
import com.example.socialk.ui.theme.SocialTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChatFragment:Fragment() {
    private val viewModel by viewModels<ChatViewModel>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel.navigateTo.observe(viewLifecycleOwner) { navigateToEvent ->
            navigateToEvent.getContentIfNotHandled()?.let { navigateTo ->
                navigate(navigateTo, Screen.Create)
            }
        }
        return ComposeView(requireContext()).apply {
            setContent {
                SocialTheme {
                    ChatScreen(
                        onEvent = { event ->
                            when (event) {
                                is ChatEvent.GoToProfile -> viewModel.handleGoToProfile()
                                is ChatEvent.LogOut -> viewModel.handleLogOut()
                                is ChatEvent.GoToSettings -> viewModel.handleGoToSettings()
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
