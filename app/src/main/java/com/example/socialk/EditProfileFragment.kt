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
import com.example.socialk.ui.theme.SocialTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditProfileFragment :Fragment() {
    private val viewModel by viewModels<ProfileViewModel>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel.navigateTo.observe(viewLifecycleOwner) { navigateToEvent ->
            navigateToEvent.getContentIfNotHandled()?.let { navigateTo ->
                navigate(navigateTo, Screen.EditProfile)
            }
        }
        return ComposeView(requireContext()).apply {
            setContent {
                SocialTheme {
                    EditProfileScreen(
                        onEvent = { event ->
                            when (event) {
                                is EditProfileEvent.GoToProfile -> viewModel.handleGoToProfile()
                                is EditProfileEvent.LogOut -> viewModel.handleLogOut()
                                is EditProfileEvent.GoToSettings -> viewModel.handleGoToSettings()
                                is EditProfileEvent.GoToHome -> viewModel.handleGoToHome()

                                is EditProfileEvent.GoToEditProfile -> viewModel.handleGoToEditProfile()
                            //TODO
                            // is EditProfileEvent.ConfirmChanges -> x
                            }
                        }
                    , profileUrl = "https://firebasestorage.googleapis.com/v0/b/socialv2-340711.appspot.com/o/uploads%2F1662065348037.null?alt=media&token=40cebce4-0c53-470c-867f-d9d34cba63ab")
                }
            }
        }
    }

}