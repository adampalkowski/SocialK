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
class LiveFragment : Fragment() {
    private val viewModel by viewModels<CreateViewModel>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel.navigateTo.observe(viewLifecycleOwner) { navigateToEvent ->
            navigateToEvent.getContentIfNotHandled()?.let { navigateTo ->
                navigate(navigateTo, Screen.Live)
            }
        }
        return ComposeView(requireContext()).apply {
            setContent {
                SocialTheme {
                    LiveScreen(  onEvent = { event ->
                        when (event) {
                            is LiveEvent.GoToProfile -> viewModel.handleGoToProfile()
                            is LiveEvent.GoToHome -> viewModel.handleGoToHome()
                            is LiveEvent.LogOut -> viewModel.handleLogOut()
                            is LiveEvent.GoToSettings -> viewModel.handleGoToSettings()
                            is LiveEvent.GoToLive -> viewModel.handleGoToLive()
                            is LiveEvent.GoToEvent -> viewModel.handleGoToEvent()
                            is LiveEvent.GoToActivity -> viewModel.handleGoToActivity()
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
