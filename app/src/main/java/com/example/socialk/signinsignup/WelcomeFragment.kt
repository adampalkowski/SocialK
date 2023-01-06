package com.example.socialk.signinsignup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.socialk.Main.Screen
import com.example.socialk.Main.navigate
import com.example.socialk.ui.theme.SocialTheme
import dagger.hilt.android.AndroidEntryPoint
@AndroidEntryPoint
class WelcomeFragment:Fragment() {
    private val viewModel by viewModels<WelcomeViewModel>()
    private val authViewModel by viewModels<AuthViewModel>()


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
                    WelcomeScreen(  onEvent = { event ->
                        when (event) {
                            is WelcomeEvent.GoToSignIn -> viewModel.handleGoToSignIn()
                            is WelcomeEvent.GoToRegister -> viewModel.handleGoToRegister()
                            is WelcomeEvent.ContinueWithGoogle->authViewModel.oneTapSignIn()
                        }
                    }   , navigateToHome = {viewModel.handleGoToHome()})
                }
                checkAuthState()
            }
        }
    }
    private fun checkAuthState() {
        if(authViewModel.isUserAuthenticated) {
            viewModel.handleGoToHome()
        }
    }

}