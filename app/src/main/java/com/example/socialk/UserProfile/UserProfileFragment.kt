package com.example.socialk.UserProfile

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.socialk.*
import com.example.socialk.Main.Screen
import com.example.socialk.Main.navigate
import com.example.socialk.model.User
import com.example.socialk.model.UserData
import com.example.socialk.ui.theme.SocialTheme
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class UserProfileFragment : Fragment(){
    private val viewModel by viewModels<UserProfileViewModel>()
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
        var user: User? = UserData.user
        if (user==null){
            user= User(id="",name="", pictureUrl = "", username = "", email = "")
        }
        if (user.username==null){
            user.username=""
        }
        if (user.name==null){
            user.name=user.email
        }
        return ComposeView(requireContext()).apply {
            setContent {
                SocialTheme {
                    UserProfileScreen(user,
                        onEvent = { event ->
                            when (event) {
                                is UserProfileEvent.GoToProfile -> viewModel.handleGoToProfile()
                                is UserProfileEvent.GoToSettings -> viewModel.handleGoToSettings()
                                is UserProfileEvent.GoToHome -> viewModel.handleGoToHome()
                                is UserProfileEvent.GoToEditProfile -> viewModel.handleGoToEditProfile()
                                is UserProfileEvent.GoToSearch -> viewModel.handleGoToSearch()
                            }
                        }
                    )
                }
            }
        }
    }

}