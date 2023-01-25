package com.example.socialk.chat

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
import com.example.socialk.Map
import com.example.socialk.di.ChatViewModel
import com.example.socialk.di.UserViewModel
import com.example.socialk.model.Chat
import com.example.socialk.model.Response
import com.example.socialk.model.User
import com.example.socialk.model.UserData
import com.example.socialk.ui.theme.SocialTheme
import dagger.hilt.android.AndroidEntryPoint
import java.util.*



/*
To enter chat fragment you either click in 1.users proifle (message),.2 click on chat in chat collection or 3. chat in activity
if _chatCollectionStateFlow.value.id = empty then it needs to have a user vice versa

1.user profile
If you go to chat through user profile, you have user object in the userviewmodel

first check if chat already exists between users = if user->friends_ids.my_id= GROUP_CHAT_ID then call the getmessages with the id and wait for response
if however user->friends_ids.my_id= EMPTY then CREATE_CHAT_COLLECTION  -> wait for response -> ADD CHAT_COLLECTION_ID to both users
Screen can be started but we need to wait for response of create chat to be able to then write messages, so if no response from adding chat to users then loading


2.chat activity, holds a field called chat that has an id of chat collection, on activity create -> create chat collection with
    -same create_date
    -random id
    -chat_name = activity_name
    -members = participants
    -type = group
    -owner_id - creator_id

on click on the activity chat -> read the messages and read the chat collection to get the collection data
IMPORTANT read the activities_chats in chat_collection SIMPLYFIES SEARCH FOR ACTIVITY CHAT

if (user patricipates in the activity) -> put him as member in chat collection

3.chat collection, gives you chat_collection objects that you can then fill the content here with(sometimes no profile pic)
and call for messages with collection_id to then read the values and put them in chat


* */
@AndroidEntryPoint
class ChatFragment : Fragment() {
    private val viewModel by viewModels<ChatCollectionViewModel>()
    private val userViewModel by activityViewModels<UserViewModel>()
    private val chatViewModel by viewModels<ChatViewModel>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel.navigateTo.observe(viewLifecycleOwner) { navigateToEvent ->
            navigateToEvent.getContentIfNotHandled()?.let { navigateTo ->
                navigate(navigateTo, Screen.Chat)
            }
        }
        var chat_collection_id:String =""
        if(chatViewModel._chatCollectionStateFlow.value.id!!.isEmpty())
        {
            val currentUser= UserData.user
            val user: User = userViewModel.userProfile.value
            // user friendsids hashmap holds values to the chat_ids with our id as key
            user.friends_ids.forEach{ (key,value ) ->
                //in user find our id
                if (key==currentUser!!.id){
                    //if value if not empty then there is a key to chat
                    if (value.isNotEmpty()){
                        chat_collection_id=value.toString()
                    }else{
                        //value empty, create chat_collection, save the id here
                        val uuid: UUID = UUID.randomUUID()
                        val id:String = uuid.toString()
                        chat_collection_id=id
                        chatViewModel.addChatCollection(    Chat(null,
                            owner_id =currentUser!!.id,
                            id =chat_collection_id,
                            chat_name =null,
                            chat_picture =null,
                            recent_message =null,
                            recent_message_time =null,
                            type ="duo",
                            members = arrayListOf(UserData.user!!.id,user.id),
                            user_one_username = user.username,
                            user_two_username = currentUser!!.username,
                            user_one_profile_pic =user.pictureUrl,
                            user_two_profile_pic = currentUser!!.pictureUrl
                        ))
                        userViewModel.addChatCollectionToUsers(currentUser!!.id,user.id,chat_collection_id)
                    }
                }else{

                }
            }
            return ComposeView(requireContext()).apply {
                setContent {
                    SocialTheme {
                        ChatScreen(user, chatViewModel,
                            onEvent = { event ->
                                when (event) {
                                    is ChatEvent.GoBack -> viewModel.handleGoBack()
                                }
                            }
                        )
                    }
                }
            }

        }else{
            if (chat_collection_id.isNotEmpty()) {
                chatViewModel.getMessages(chatViewModel._chatCollectionStateFlow.value.id!!)
            } else {

            }
            val chat:Chat=chatViewModel._chatCollectionStateFlow.value
            return ComposeView(requireContext()).apply {
                setContent {

                    SocialTheme {
                        ChatScreen(chat, chatViewModel,
                            onEvent = { event ->
                                when (event) {
                                    is ChatEvent.GoBack -> viewModel.handleGoBack()

                                }
                            }
                        )
                    }
                }
            }
        }

    }

}
