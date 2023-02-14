package com.example.socialk

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.example.socialk.Main.Screen
import com.example.socialk.Main.navigate
import com.example.socialk.di.ChatViewModel
import com.example.socialk.di.UserViewModel
import com.example.socialk.model.Chat
import com.example.socialk.model.Response
import com.example.socialk.model.UserData
import com.example.socialk.ui.theme.SocialTheme
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

@AndroidEntryPoint
class SearchFragment: Fragment() {
    private val viewModel by viewModels<SearchViewModel>()
    private val userViewModel by activityViewModels<UserViewModel>()
    private val chatViewModel by viewModels<ChatViewModel>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel.navigateTo.observe(viewLifecycleOwner) { navigateToEvent ->
            navigateToEvent.getContentIfNotHandled()?.let { navigateTo ->
                if(navigateTo==Screen.UserProfile){
                    val bundle = Bundle()
                    bundle.putSerializable("user",viewModel.searched_user.value)
                    Log.d("SEARCHFRAGMENT",viewModel.searched_user.value.toString())
                    navigate(navigateTo, Screen.Search,bundle)
                }else{
                    navigate(navigateTo, Screen.Search)
                }
            }
        }


        userViewModel.getInvites(UserData.user!!.id)
        return ComposeView(requireContext()).apply {
            setContent {
                SocialTheme {
                    SearchScreen(userViewModel,
                        onEvent = { event ->
                            when (event) {
                                is SearchEvent.GoToProfile -> viewModel.handleGoToChats()
                                is SearchEvent.GoToProfile -> viewModel.handleGoToProfile()
                                is SearchEvent.GoBack ->    activity?.onBackPressedDispatcher?.onBackPressed()
                                is SearchEvent.OnInviteAccepted -> {
                                    val uuid: UUID = UUID.randomUUID()
                                    val id:String = uuid.toString()
                                    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                                    val current = LocalDateTime.now().format(formatter)
                                    userViewModel.acceptInvite(UserData.user!!,event.user , Chat(current,
                                        owner_id =event.user.id,
                                        id =id,
                                        chat_name =null,
                                        chat_picture =null,
                                        recent_message =null,
                                        recent_message_time =current,
                                        type ="duo",
                                        members = arrayListOf(UserData.user!!.id,event.user.id),
                                        user_one_username =UserData.user!!.username,
                                        user_two_username =event.user.username,
                                        user_one_profile_pic = UserData.user!!.pictureUrl,
                                        user_two_profile_pic = event.user.pictureUrl
                                    ))

                                }
                                is SearchEvent.GoToUserProfile ->{

                                    // event.user stores the value of searched user , pass it to the nav component so that user profile can be displayed
                                    viewModel.handleGoToUserProfile(event.user)
                                }
                            }
                        }
                    )
                }
            }
        }
    }

}

