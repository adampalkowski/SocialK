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
import com.example.socialk.ui.theme.SocialTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChatCollectionFragment:Fragment() {
    private val viewModel by viewModels<ChatCollectionViewModel>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel.navigateTo.observe(viewLifecycleOwner) { navigateToEvent ->
            navigateToEvent.getContentIfNotHandled()?.let { navigateTo ->
                navigate(navigateTo, Screen.ChatCollection)
            }
        }
        return ComposeView(requireContext()).apply {
            setContent {
                SocialTheme {
                    ChatCollectionScreen(
                        onEvent = { event ->
                            when (event) {
                                is ChatCollectionEvent.GoToProfile -> viewModel.handleGoToProfile()
                                is ChatCollectionEvent.LogOut -> viewModel.handleLogOut()
                                is ChatCollectionEvent.GoToSettings -> viewModel.handleGoToSettings()
                                is ChatCollectionEvent.GoToSearch -> viewModel.handleGoToSearch()
                                is ChatCollectionEvent.GoToChat -> viewModel.handleGoToChat()
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
