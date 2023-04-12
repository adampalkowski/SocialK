package com.example.socialk.create.FriendPicker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.socialk.Main.Screen
import com.example.socialk.Main.navigate
import com.example.socialk.create.CreateViewModel
import com.example.socialk.di.ActivityViewModel
import com.example.socialk.di.ChatViewModel
import com.example.socialk.di.UserViewModel
import com.example.socialk.model.Activity
import com.example.socialk.model.Chat
import com.example.socialk.model.UserData
import com.example.socialk.signinsignup.AuthViewModel
import com.example.socialk.ui.theme.SocialTheme
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*


@AndroidEntryPoint
class FriendsPickerFragment : Fragment() {

    private val viewModel by viewModels<CreateViewModel>()
    private val userViewModel by viewModels<UserViewModel>()
    private val authViewModel by viewModels<AuthViewModel>()
    private val chatViewModel by viewModels<ChatViewModel>()
    private val activityViewModel by viewModels<ActivityViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel.navigateTo.observe(viewLifecycleOwner) { navigateToEvent ->
            navigateToEvent.getContentIfNotHandled()?.let { navigateTo ->
                navigate(navigateTo, Screen.FriendsPicker)
            }

        }


        var activityCreated: Activity?=null
        if (arguments?.getSerializable("activity")!=null){
            activityCreated = arguments?.getSerializable("activity") as Activity
        }

        var group_name: String? = arguments?.getString("group_name")
        var group_picture: String? = arguments?.getString("group_picture")

        var type: String = if (activityCreated != null ) {
            "activity"
        } else if (group_name != null) {
            "group"
        }else{
            "activity"
        }

        if(activityCreated!=null && activityCreated.end_time.isNotEmpty()){
            type="update"
        }
        userViewModel.getFriends(authViewModel.currentUser!!.uid)
        chatViewModel.getGroups(authViewModel.currentUser!!.uid)
        return ComposeView(requireContext()).apply {
            setContent {
                SocialTheme {
                    FriendsPickerScreen(type, chatViewModel, userViewModel, onEvent = { event ->
                        when (event) {
                            is FriendsPickerEvent.GoBack -> {
                                activity?.onBackPressedDispatcher?.onBackPressed()
                            }
                            is FriendsPickerEvent.CreateGroup -> {
                                val uuid: UUID = UUID.randomUUID()
                                val id: String = uuid.toString()
                                val participants_profile_pictures: HashMap<String, String> =
                                    hashMapOf()
                                val participants_usernames: HashMap<String, String> = hashMapOf()
                                val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                                val current = LocalDateTime.now().format(formatter)
                                event.selected_ids.add(UserData.user!!.id)

                                val chat = Chat(
                                    create_date = current,
                                    owner_id = UserData.user!!.id,
                                    id = id,
                                    chat_name = group_name,
                                    chat_picture = UserData.user!!.pictureUrl!!,
                                    recent_message = "say hi!",
                                    recent_message_time = current,
                                    type = "group",
                                    members = event.selected_ids.distinct(),
                                    user_one_username = null,
                                    user_two_username = null,
                                    user_one_profile_pic = null,
                                    user_two_profile_pic = null,
                                    highlited_message = ""
                                )
                                chatViewModel.addChatCollection(chat,group_picture)
                                viewModel.handleGoToMap()
                            }
                            is FriendsPickerEvent.UpdateInvites -> {
                                if(activityCreated!=null){
                                    val new_invites=    activityCreated.invited_users
                                    new_invites.addAll(event.selected_ids)
                                    val distinctList = new_invites.distinct()
                                    new_invites.clear()
                                    new_invites.addAll(distinctList)
                                    activityViewModel.updateActivityInvites(activityCreated.id,new_invites)
                                    viewModel.handleGoToHome()
                                    Toast.makeText(activity,"Invites sent",Toast.LENGTH_SHORT).show()
                                }
                            }
                            is FriendsPickerEvent.CreateActivity -> {
                                Toast.makeText(activity,"Processing activity",Toast.LENGTH_SHORT).show()
                                event.selected_ids.add(UserData.user!!.id)
                                activityCreated!!.invited_users =
                                    ArrayList(event.selected_ids.distinct())

                                val chat = Chat(
                                    create_date = activityCreated.creation_time,
                                    owner_id = activityCreated.id,
                                    id = activityCreated.id,
                                    chat_name = activityCreated.title,
                                    chat_picture = UserData.user!!.pictureUrl!!,
                                    recent_message = "say hi!",
                                    recent_message_time = activityCreated.creation_time,
                                    type = "activity",
                                    members = arrayListOf(UserData.user!!.id),
                                    user_one_username = null,
                                    user_two_username = null,
                                    user_one_profile_pic = null,
                                    user_two_profile_pic = null,
                                    highlited_message = ""
                                )
                                chatViewModel.addChatCollection(chat,group_picture, onFinished = {picture->
                                    if(picture.isEmpty()){
                                        activityViewModel.addActivity(activityCreated)
                                    }else{
                                        activityCreated.creator_profile_picture=picture
                                        activityViewModel.addActivity(activityCreated)
                                    }


                                })
                                viewModel.handleGoToMap()

                            }
                        }

                    })
                }
            }
        }
    }
}

