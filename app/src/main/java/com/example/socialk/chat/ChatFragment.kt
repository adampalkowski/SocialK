package com.example.socialk.chat

import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.ComposeView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.example.socialk.Main.Screen
import com.example.socialk.Main.navigate
import com.example.socialk.di.ChatViewModel
import com.example.socialk.di.UserViewModel
import com.example.socialk.map.MapEvent
import com.example.socialk.model.*
import com.example.socialk.ui.theme.SocialTheme
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
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
    private  var fusedLocationClient: FusedLocationProviderClient?=null
    private var locationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val locationList = locationResult.locations
            if (locationList.isNotEmpty()) {
                //The last location in the list is the newest
                val location = locationList.last()
                viewModel.setLocation(LatLng(location.latitude,location.longitude))

            }
        }
    }

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



        fusedLocationClient= LocationServices.getFusedLocationProviderClient(activity?.applicationContext!!)
        if (ActivityCompat.checkSelfPermission(
                activity?.applicationContext!!,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                activity?.applicationContext!!,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {


        }
        fusedLocationClient!!.requestLocationUpdates(
            LocationRequest(),
            locationCallback,
            Looper.getMainLooper())
        val requestPermissionLauncher =
            registerForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { isGranted: Boolean ->
                if (isGranted) {
                    // Permission is granted. Continue the action or workflow in your
                    // app.\
                    viewModel.permissionGranted()
                } else {
                    // Explain to the user that the feature is unavailable because the
                    // feature requires a permission that the user has denied. At the
                    // same time, respect the user's decision. Don't link to system
                    // settings in an effort to convince the user to change their
                    // decision.
                }

            }
        when {
            ContextCompat.checkSelfPermission(
                activity?.applicationContext!!,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                viewModel.permissionGranted()
            }
            shouldShowRequestPermissionRationale(   android.Manifest.permission.ACCESS_FINE_LOCATION) -> {
                // In an educational UI, explain to the user why your app requires this
                // permission for a specific feature to behave as expected, and what
                // features are disabled if it's declined. In this UI, include a
                // "cancel" or "no thanks" button that lets the user continue
                // using your app without granting the permission.

            }
            else -> {
                // You can directly ask for the permission.
                // The registered ActivityResultCallback gets the result of this request.
                requestPermissionLauncher.launch(
                    android.Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }

        val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            // Callback is invoked after the user selects a media item or closes the
            // photo picker.
            if (uri != null) {
                Log.d("ImageFromGallery", "image received"+uri.toString())
                chatViewModel.onUriReceived(uri)
            } else {
                Log.d("PhotoPicker", "No media selected")
            }
        }
        return ComposeView(requireContext()).apply {
            setContent {
                SocialTheme {

                    if (arguments?.getSerializable("chat") != null) {


                        val chat = arguments?.getSerializable("chat") as Chat


                        val currentDateTime = Calendar.getInstance().time
                        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                        val formattedDateTime = dateFormat.format(currentDateTime)
                        chatViewModel.getMessages(chat.id!!,formattedDateTime)

                        chatViewModel.getFirstMessages(chat.id!!,formattedDateTime)
                        ChatScreen(viewModel,chat, chatViewModel,
                            onEvent = { event ->
                                when (event) {

                                    is ChatEvent.GoBack ->activity?.onBackPressedDispatcher?.onBackPressed()
                                    is ChatEvent.SendMessage -> {
                                    //id and sent_time are set in view model
                                        chatViewModel.addMessage(
                                            chat.id!!,
                                            ChatMessage(
                                                text = event.message,
                                                sender_picture_url = UserData.user?.pictureUrl!!,
                                                sent_time ="",
                                                sender_id = UserData.user!!.id,
                                                message_type = "text",
                                                id = ""
                                            )
                                        )
                                    }
                                    is ChatEvent.SendImage -> {
                                        //id and sent_time are set in view model
                                        //we have URI
                                        //add uri to storage and resize it
                                        //get the url and add it to the message
                                        chatViewModel.sendImage(chat.id!!,     ChatMessage(
                                            text = event.message.toString(),
                                            sender_picture_url = UserData.user?.pictureUrl!!,
                                            sent_time = "",
                                            sender_id = UserData.user!!.id,
                                            message_type = "uri",
                                            id = ""
                                        ),event.message)



                                    }
                                    is ChatEvent.OpenGallery -> {
                                        pickMedia.launch(PickVisualMediaRequest(
                                            ActivityResultContracts.PickVisualMedia.ImageOnly))
                                    }
                                    is ChatEvent.AskForPermission -> {
                                        requestPermissionLauncher.launch(
                                            android.Manifest.permission.ACCESS_FINE_LOCATION)
                                    }
                                }
                            }
                        )
                    } else if (arguments?.getSerializable("user") != null) {
                        val user = arguments?.getSerializable("user") as User
                        val currentDateTime = Calendar.getInstance().time
                        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                        val formattedDateTime = dateFormat.format(currentDateTime)
                        chatViewModel.getMessages(user.friends_ids[UserData.user!!.id]!!,formattedDateTime)

                        chatViewModel.getFirstMessages(user.friends_ids[UserData.user!!.id]!!,formattedDateTime)
                        if(user.friends_ids.containsKey(UserData.user!!.id)){
                            chatViewModel.getChatCollection(user.friends_ids.get(UserData.user!!.id)!!)
                        }else{
                            activity?.onBackPressedDispatcher?.onBackPressed()
                        }

                        ChatScreen(viewModel,user.friends_ids[UserData.user!!.id]!!,user, chatViewModel,
                            onEvent = { event ->
                                when (event) {
                                    is ChatEvent.GoBack ->activity?.onBackPressedDispatcher?.onBackPressed()
                                    is ChatEvent.SendMessage -> {
                                        chatViewModel.addMessage(
                                            user.friends_ids[UserData.user!!.id]!!,
                                            //todo set sender picture_url
                                            ChatMessage(
                                                text = event.message,
                                                sender_picture_url = "",
                                                sent_time = "",
                                                sender_id = UserData.user!!.id,
                                                message_type = "text",
                                                id = ""
                                            )
                                        )
                                    }
                                    is ChatEvent.SendImage -> {
                                    //id and sent_time are set in view model
                                    //we have URI
                                    //add uri to storage and resize it
                                    //get the url and add it to the message
                                    chatViewModel.sendImage(user.friends_ids[UserData.user!!.id]!!,     ChatMessage(
                                        text = event.message.toString(),
                                        sender_picture_url = UserData.user?.pictureUrl!!,
                                        sent_time = "",
                                        sender_id = UserData.user!!.id,
                                        message_type = "uri",
                                        id = ""
                                    ),event.message)



                                }
                                    is ChatEvent.OpenGallery -> {
                                        pickMedia.launch(PickVisualMediaRequest(
                                            ActivityResultContracts.PickVisualMedia.ImageOnly))
                                    }

                                }
                            }
                        )


                    }else{
                        val activity = arguments?.getSerializable("activity") as Activity

                        val currentDateTime = Calendar.getInstance().time
                        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                        val formattedDateTime = dateFormat.format(currentDateTime)
                        chatViewModel.getMessages(activity.id!!,formattedDateTime)

                        chatViewModel.getFirstMessages(activity.id!!,formattedDateTime)
                        ChatScreen(viewModel,activity.id!!,activity, chatViewModel,
                            onEvent = { event ->
                                when (event) {
                                    is ChatEvent.GoBack ->getActivity()?.onBackPressedDispatcher?.onBackPressed()
                                    is ChatEvent.SendMessage -> {
                                        chatViewModel.addMessage(
                                            activity.id!!,
                                            //todo set sender picture_url
                                            ChatMessage(
                                                text = event.message,
                                                sender_picture_url = UserData.user?.pictureUrl!!,
                                                sent_time = "",
                                                sender_id = UserData.user!!.id,
                                                message_type = "text",
                                                id = ""
                                            )
                                        )
                                    }
                                    is ChatEvent.SendImage -> {
                                        //id and sent_time are set in view model
                                        //we have URI
                                        //add uri to storage and resize it
                                        //get the url and add it to the message
                                        chatViewModel.sendImage(activity.id!!,     ChatMessage(
                                            text = event.message.toString(),
                                            sender_picture_url = UserData.user?.pictureUrl!!,
                                            sent_time = "",
                                            sender_id = UserData.user!!.id,
                                            message_type = "uri",
                                            id = ""
                                        ),event.message)



                                    }
                                    is ChatEvent.OpenGallery -> {
                                        pickMedia.launch(PickVisualMediaRequest(
                                            ActivityResultContracts.PickVisualMedia.ImageOnly))
                                    }

                                }
                            }
                        )
                    }

                }

            }


        }

    }
}
