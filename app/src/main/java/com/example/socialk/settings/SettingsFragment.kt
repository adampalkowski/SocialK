package com.example.socialk.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.socialk.Main.Screen
import com.example.socialk.Main.navigate
import com.example.socialk.signinsignup.AuthViewModel
import com.example.socialk.ui.theme.SocialTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsFragment :Fragment(){
    private val viewModel by viewModels<SettingsViewModel>()
    private val authViewModel by viewModels<AuthViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel.navigateTo.observe(viewLifecycleOwner) { navigateToEvent ->
            navigateToEvent.getContentIfNotHandled()?.let { navigateTo ->
                navigate(navigateTo, Screen.Settings)
            }
        }
        return ComposeView(requireContext()).apply {
            setContent {
                SocialTheme {
                    SettingsScreen(authViewModel,
                        onEvent = { event ->
                            when (event) {
                                is SettingsEvent.GoToProfile -> viewModel.handleGoToProfile()
                                is SettingsEvent.LogOut -> viewModel.handleLogOut()
                                is SettingsEvent.GoToSettings -> viewModel.handleGoToSettings()
                                is SettingsEvent.GoToHome -> viewModel.handleGoToHome()
                            }
                        }
                    )
                }
            }
        }
    }

}