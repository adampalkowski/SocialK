package com.example.socialk

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.socialk.Main.Screen
import com.example.socialk.Main.navigate
import com.example.socialk.model.User
import com.example.socialk.model.UserData
import com.example.socialk.ui.theme.SocialTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFragment :Fragment(){
    private val viewModel by viewModels<ProfileViewModel>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel.navigateTo.observe(viewLifecycleOwner) { navigateToEvent ->
            navigateToEvent.getContentIfNotHandled()?.let { navigateTo ->
                navigate(navigateTo, Screen.Profile)
            }
        }
        var user:User? =UserData.user
        if (user==null){
            user=User(id="",name="", pictureUrl = "", username = "", email = "", blocked_ids = ArrayList() , invited_ids = ArrayList() , friends_ids = ArrayList())

        }
        Log.d("TAG",user.toString())

        if (user.username==null){
            user.username=""
        }
        if (user.name==null){
            user.name=user.email
        }
        return ComposeView(requireContext()).apply {
            setContent {
                SocialTheme {
                    ProfileScreen(user,
                        onEvent = { event ->
                            when (event) {
                                is ProfileEvent.GoToProfile -> viewModel.handleGoToProfile()
                                is ProfileEvent.LogOut -> viewModel.handleLogOut()
                                is ProfileEvent.GoToSettings -> viewModel.handleGoToSettings()
                                is ProfileEvent.GoToHome -> viewModel.handleGoToHome()
                                is ProfileEvent.GoToEditProfile -> viewModel.handleGoToEditProfile()
                                is ProfileEvent.GoToSearch -> viewModel.handleGoToSearch()
                            }
                        },
                        bottomNavEvent  ={screen->
                            when (screen) {
                                is Home -> viewModel.handleGoToHome()
                                is Map -> viewModel.handleGoToMap()
                                is Chats -> viewModel.handleGoToChats()
                                is Create -> viewModel.handleGoToCreate()

                            }
                        }
                    )
                }
            }
        }
    }

}