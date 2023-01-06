package com.example.socialk

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.socialk.Main.Screen
import com.example.socialk.Main.navigate
import com.example.socialk.home.HomeEvent
import com.example.socialk.home.HomeScreen
import com.example.socialk.home.HomeViewModel
import com.example.socialk.signinsignup.AuthViewModel
import com.example.socialk.ui.theme.SocialTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MemoriesFragment:Fragment() {
    private val viewModel by viewModels<MemoriesViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel.navigateTo.observe(viewLifecycleOwner) { navigateToEvent ->
            navigateToEvent.getContentIfNotHandled()?.let { navigateTo ->
                navigate(navigateTo, Screen.Memories)
            }
        }
        return ComposeView(requireContext()).apply {
            setContent {
                SocialTheme {
                    MemoriesScreen(
                        onEvent = { event ->
                            when (event) {
                                is MemoriesEvent.GoToProfile -> viewModel.handleGoToProfile()
                                is MemoriesEvent.LogOut -> viewModel.handleLogOut()
                                is MemoriesEvent.GoToSettings -> viewModel.handleGoToSettings()
                                is MemoriesEvent.GoToMemories -> viewModel.handleGoToMemories()
                                is MemoriesEvent.GoToHome -> viewModel.handleGoToHome()
                            }
                        }
                    )
                }
            }
        }
    }

}