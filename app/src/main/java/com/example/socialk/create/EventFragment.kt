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
import com.example.socialk.ui.theme.SocialTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EventFragment : Fragment() {
    private val viewModel by viewModels<CreateViewModel>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel.navigateTo.observe(viewLifecycleOwner) { navigateToEvent ->
            navigateToEvent.getContentIfNotHandled()?.let { navigateTo ->
                navigate(navigateTo, Screen.Event)
            }
        }
        return ComposeView(requireContext()).apply {
            setContent {
                SocialTheme {
                    EventScreen(  onEvent = { event ->
                        when (event) {
                            is EventEvent.GoToProfile -> viewModel.handleGoToProfile()
                            is EventEvent.GoToHome -> viewModel.handleGoToHome()
                            is EventEvent.LogOut -> viewModel.handleLogOut()
                            is EventEvent.GoToSettings -> viewModel.handleGoToSettings()
                            is EventEvent.GoToLive -> viewModel.handleGoToLive()
                            is EventEvent.GoToEvent -> viewModel.handleGoToEvent()
                            is EventEvent.GoToActivity -> viewModel.handleGoToActivity()
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
