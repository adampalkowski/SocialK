package com.example.socialk.UserProfile

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.load.model.DataUrlLoader
import com.example.socialk.*
import com.example.socialk.Main.Screen
import com.example.socialk.Main.navigate
import com.example.socialk.di.UserViewModel
import com.example.socialk.model.Response
import com.example.socialk.model.User
import com.example.socialk.model.UserData
import com.example.socialk.signinsignup.AuthViewModel
import com.example.socialk.ui.theme.SocialTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.net.UnknownServiceException

/*
* User profile fragment
* display current users profile
* his activities and memories
*
*
* DB READS 1 for user
* THIS CAN BE OPTIMIZED , NO NEED TO DOWNLOAD USER since we have USERDATA SAVED
* */
@AndroidEntryPoint
class UserProfileFragment : Fragment(){
    private val viewModel by viewModels<UserProfileViewModel>()
    private val userViewModel by activityViewModels<UserViewModel>()
    private val authViewModel by viewModels<AuthViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel.navigateTo.observe(viewLifecycleOwner) { navigateToEvent ->
            navigateToEvent.getContentIfNotHandled()?.let { navigateTo ->
                if (navigateTo==Screen.Chat){
                    val bundle = Bundle()
                    bundle.putSerializable("user",viewModel.clicked_user.value)
                    navigate(navigateTo, Screen.UserProfile,bundle)

                }else{
                    navigate(navigateTo, Screen.UserProfile)

                }
            }
        }

        val user:User? = arguments?.getSerializable("user") as User?
        val user_id:String? = arguments?.getSerializable("user_id") as String?
        if (user_id!=null){
            userViewModel.getUser(user_id)
        }
        return ComposeView(requireContext()).apply {
            setContent {
                SocialTheme {
                    UserProfileScreen(viewModel,user,userViewModel,
                        onEvent = { event ->
                            when (event) {
                                is UserProfileEvent.GoToProfile -> viewModel.handleGoToProfile()
                                is UserProfileEvent.GoToSettings -> viewModel.handleGoToSettings()
                                is UserProfileEvent.GoToHome -> viewModel.handleGoToHome()
                                is UserProfileEvent.GoToEditProfile -> viewModel.handleGoToEditProfile()
                                is UserProfileEvent.GoToSearch -> viewModel.handleGoToSearch()
                                is UserProfileEvent.GoToChat -> {
                                    viewModel.handleGoToChat(event.user)
                                }
                                is UserProfileEvent.InviteUser -> {
                                    //TODO !! here should always be null ???
                                    userViewModel.addInvitedIdToUser(UserData.user!!.id,event.user.id)
                                    Toast.makeText(activity?.applicationContext,
                                        "User " + event.user.username+ " invited ",Toast.LENGTH_LONG).show()
                                }
                                is UserProfileEvent.RemoveInvite -> {
                                    //TODO !! here should always be null ???
                                    userViewModel.removeInvitedIdFromUser(UserData.user!!.id,event.user.id)
                                    userViewModel.removeFriendFromBothUsers(UserData.user!!.id,event.user.id)
                                    Toast.makeText(activity?.applicationContext,
                                        "Invite to " + event.user.username+ " removed ",Toast.LENGTH_LONG).show()
                                }
                            }
                        }
                    )
                }
            }
        }
    }

}