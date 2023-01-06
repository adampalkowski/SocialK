package com.example.socialk.map

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
import com.example.socialk.chat.ChatEvent
import com.example.socialk.home.HomeEvent
import com.example.socialk.home.HomeScreen
import com.example.socialk.home.HomeViewModel
import com.example.socialk.ui.theme.SocialTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MapFragment:Fragment() {
    private val viewModel by viewModels<MapViewModel>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel.navigateTo.observe(viewLifecycleOwner) { navigateToEvent ->
            navigateToEvent.getContentIfNotHandled()?.let { navigateTo ->
                navigate(navigateTo, Screen.Map)
            }
        }
        return ComposeView(requireContext()).apply {
            setContent {
                SocialTheme {
                    MapScreen(onEvent = { event ->
                        when (event) {
                            is MapEvent.GoToProfile -> viewModel.handleGoToProfile()
                            is MapEvent.LogOut -> viewModel.handleLogOut()
                            is MapEvent.GoToSettings -> viewModel.handleGoToSettings()
                        }
                    },
                        bottomNavEvent  ={screen->
                            when (screen) {
                                is Home -> viewModel.handleGoToHome()
                                is com.example.socialk.Map -> viewModel.handleGoToMap()
                                is Chats -> viewModel.handleGoToChats()
                                is Create -> viewModel.handleGoToCreate()
                                is Profile ->viewModel.handleGoToProfile()
                            }
                        }
                    )
                }
            }
        }
    }

}