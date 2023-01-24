package com.example.socialk.UserProfile

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.socialk.*
import com.example.socialk.Main.Screen
import com.example.socialk.Main.navigate
import com.example.socialk.di.UserViewModel
import com.example.socialk.model.Response
import com.example.socialk.model.User
import com.example.socialk.model.UserData
import com.example.socialk.ui.theme.SocialTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class UserProfileFragment : Fragment(){
    private val viewModel by viewModels<UserProfileViewModel>()
    private val userViewModel by activityViewModels<UserViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel.navigateTo.observe(viewLifecycleOwner) { navigateToEvent ->
            navigateToEvent.getContentIfNotHandled()?.let { navigateTo ->
                navigate(navigateTo, Screen.UserProfile)
            }
        }


        Log.d("TAGGG",userViewModel.userProfile.value.toString()+"SD")
        val user:User =   userViewModel.userProfile.value
        return ComposeView(requireContext()).apply {
            setContent {
                SocialTheme {
                    UserProfileScreen(
                        user,userViewModel,
                        onEvent = { event ->
                            when (event) {
                                is UserProfileEvent.GoToProfile -> viewModel.handleGoToProfile()
                                is UserProfileEvent.GoToSettings -> viewModel.handleGoToSettings()
                                is UserProfileEvent.GoToHome -> viewModel.handleGoToHome()
                                is UserProfileEvent.GoToEditProfile -> viewModel.handleGoToEditProfile()
                                is UserProfileEvent.GoToSearch -> viewModel.handleGoToSearch()
                                is UserProfileEvent.InviteUser -> {
                                    event.user
                                    viewModel.handleInviteUser()
                                }
                            }
                        }
                    )
                }
            }
        }
    }

}