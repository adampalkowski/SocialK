package com.example.socialk.create

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.socialk.*
import com.example.socialk.Main.Screen
import com.example.socialk.Main.navigate
import com.example.socialk.Map
import com.example.socialk.di.ActiveUsersViewModel
import com.example.socialk.model.Activity
import com.example.socialk.signinsignup.AuthViewModel
import com.example.socialk.ui.theme.SocialTheme
import dagger.hilt.android.AndroidEntryPoint
import java.util.*


@AndroidEntryPoint
class LiveFragment : Fragment() {
    private val viewModel by viewModels<CreateViewModel>()
    private val activeUsersViewModel by viewModels<ActiveUsersViewModel>()
    private val authViewModel by viewModels<AuthViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        activeUsersViewModel.activeUserAdded()
        viewModel.navigateTo.observe(viewLifecycleOwner) { navigateToEvent ->
            navigateToEvent.getContentIfNotHandled()?.let { navigateTo ->
                navigate(navigateTo, Screen.Live)
            }
        }
        return ComposeView(requireContext()).apply {
            setContent {
                SocialTheme {
                    LiveScreen(  activeUsersViewModel=activeUsersViewModel,onEvent = { event ->
                        when (event) {
                            is LiveEvent.GoToProfile -> viewModel.handleGoToProfile()
                            is LiveEvent.GoToHome -> viewModel.handleGoToHome()
                            is LiveEvent.LogOut -> viewModel.handleLogOut()
                            is LiveEvent.GoToSettings -> viewModel.handleGoToSettings()
                            is LiveEvent.GoToLive -> viewModel.handleGoToLive()
                            is LiveEvent.GoToEvent -> viewModel.handleGoToEvent()
                            is LiveEvent.GoToActivity -> viewModel.handleGoToActivity()
                            is LiveEvent.ClearState -> activeUsersViewModel.activeUserAdded()
                            is LiveEvent.CreateActiveUser -> {
                                val uuid: UUID = UUID.randomUUID()
                                val id:String = uuid.toString()
                                //todo what if current user is null
                                activeUsersViewModel.addActiveUser(
                    //TODO CREATE A GLOBAL USER CLASS THAT CAN BE ACCESED IN DIFFERENT FRAGMENT AND INITALIZE IT AT THE LOGIN
                                    ActiveUser(id=id,
                                        creator_id = if (authViewModel.currentUser==null){""}else{ authViewModel.currentUser!!.uid.toString()},
                                        pictureUrl = "https://firebasestorage.googleapis.com/v0/b/socialv2-340711.appspot.com/o/uploads%2F1662065348037.null?alt=media&token=40cebce4-0c53-470c-867f-d9d34cba63ab",
                                        username = "username",
                                        location = null,
                                        time_end = event.start_time,
                                        time_length = event.time_length,
                                        time_start = "123213"
                                ))
                            }
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
