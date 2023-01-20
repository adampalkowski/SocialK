package com.example.socialk.signinsignup

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.socialk.Main.Screen
import com.example.socialk.Main.navigate
import com.example.socialk.di.UserViewModel
import com.example.socialk.model.Response
import com.example.socialk.ui.theme.SocialTheme
import dagger.hilt.android.AndroidEntryPoint
@AndroidEntryPoint
class WelcomeFragment:Fragment() {
    private val viewModel by viewModels<WelcomeViewModel>()
    private val authViewModel by viewModels<AuthViewModel>()
    private val userViewModel by viewModels<UserViewModel>()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel.navigateTo.observe(viewLifecycleOwner) { navigateToEvent ->
            navigateToEvent.getContentIfNotHandled()?.let { navigateTo ->
                navigate(navigateTo, Screen.Welcome)
            }
        }
        return ComposeView(requireContext()).apply {
            setContent {
                SocialTheme {
                    WelcomeScreen(userViewModel, authViewModel, onEvent = { event ->
                        when (event) {
                            is WelcomeEvent.GoToSignIn -> viewModel.handleGoToSignIn()
                            is WelcomeEvent.GoToRegister -> viewModel.handleGoToRegister()
                            is WelcomeEvent.ContinueWithGoogle->authViewModel.oneTapSignIn()
                            is WelcomeEvent.GoToHome->viewModel.handleGoToHome()
                            is WelcomeEvent.PickUsername->viewModel.handleGoToPickUsername()
                        }
                    }   , navigateToHome = {viewModel.handleGoToHome()})
                }

            }
        }
    }


}