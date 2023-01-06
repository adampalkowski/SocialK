package com.example.socialk.create

import android.os.Bundle
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
import com.example.socialk.home.HomeViewModel
import com.example.socialk.ui.theme.SocialTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreateFragment :Fragment() {
    private val viewModel by viewModels<CreateViewModel>()
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
                    CreateScreen(  onEvent = { event ->
                        when (event) {
                            is CreateEvent.GoToProfile -> viewModel.handleGoToProfile()
                            is CreateEvent.GoToHome -> viewModel.handleGoToHome()
                            is CreateEvent.LogOut -> viewModel.handleLogOut()
                            is CreateEvent.GoToSettings -> viewModel.handleGoToSettings()
                            is CreateEvent.GoToEvent -> viewModel.handleGoToEvent()
                            is CreateEvent.GoToLive -> viewModel.handleGoToLive()
                            is CreateEvent.GoToActivity -> viewModel.handleGoToActivity()
                        }
                    },
                        bottomNavEvent  ={screen->
                            when (screen) {
                                is Home -> viewModel.handleGoToHome()
                                is Map -> viewModel.handleGoToMap()
                                is Chats -> viewModel.handleGoToChats()
                                is Profile ->viewModel.handleGoToProfile()
                            }
                        })
                }
            }
        }
    }
}
