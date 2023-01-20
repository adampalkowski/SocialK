package com.example.socialk.PickUsername

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.socialk.Main.Screen
import com.example.socialk.Main.navigate
import com.example.socialk.R
import com.example.socialk.di.UserViewModel
import com.example.socialk.signinsignup.AuthViewModel

import com.example.socialk.ui.theme.SocialTheme
import com.google.android.gms.auth.api.Auth
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PickUsernameFragment : Fragment() {
    private val viewModel by viewModels<PickUsernameViewModel>()
    private val userViewModel by viewModels<UserViewModel>()
    private val authViewModel by viewModels<AuthViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel.navigateTo.observe(viewLifecycleOwner) { navigateToEvent ->
            navigateToEvent.getContentIfNotHandled()?.let { navigateTo ->
                navigate(navigateTo, Screen.PickUsername)
            }
        }

        return ComposeView(requireContext()).apply {
            // In order for savedState to work, the same ID needs to be used for all instances.
            id = R.id.sign_in_fragment
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            setContent {
                SocialTheme {
                    PickUsernameScreen(userViewModel,authViewModel,
                        onEvent = { event ->
                            when (event) {
                                is PickUserEvent.GoToHome -> {
                                    viewModel.handleGoToHome()
                                }
                                is PickUserEvent.NavigateBack -> {
                                    activity?.onBackPressedDispatcher?.onBackPressed()
                                }

                            }
                        }
                    )
                }
            }
        }
    }
}
