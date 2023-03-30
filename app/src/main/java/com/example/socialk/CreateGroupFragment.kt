package com.example.socialk

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.socialk.Main.Screen
import com.example.socialk.Main.navigate
import com.example.socialk.create.CreateViewModel
import com.example.socialk.create.FriendPicker.FriendsPickerEvent
import com.example.socialk.create.FriendPicker.FriendsPickerScreen
import com.example.socialk.di.ActivityViewModel
import com.example.socialk.di.ChatViewModel
import com.example.socialk.di.UserViewModel
import com.example.socialk.model.Activity
import com.example.socialk.model.Chat
import com.example.socialk.model.UserData
import com.example.socialk.signinsignup.AuthViewModel
import com.example.socialk.ui.theme.SocialTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreateGroupFragment:Fragment() {

    private val viewModel by viewModels<CreateGroupViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel.navigateTo.observe(viewLifecycleOwner) { navigateToEvent ->
            navigateToEvent.getContentIfNotHandled()?.let { navigateTo ->
                if(navigateTo==Screen.FriendsPicker){
                    val bundle =Bundle()
                    bundle.putString("group_name",viewModel.group_name.value)
                    navigate(navigateTo, Screen.CreateGroup,bundle)
                }else{
                    navigate(navigateTo, Screen.CreateGroup)

                }
            }
        }

        return ComposeView(requireContext()).apply {
            setContent {
                SocialTheme {
                    CreateGroupScreen(viewModel,onEvent = {event->
                        when(event){
                            is CreateGroupEvent.GoBack->activity?.onBackPressedDispatcher?.onBackPressed()
                        }
                    }, onSubmit = {group_name ->
                        Toast.makeText(activity,"submit"+group_name, Toast.LENGTH_LONG).show()
                        viewModel.handleGoToFriendPicker(group_name = group_name)
                    })
                }
            }
        }
    }
}

