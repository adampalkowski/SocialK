package com.example.socialk.create

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.socialk.Chats
import com.example.socialk.Home
import com.example.socialk.Main.Screen
import com.example.socialk.Main.navigate
import com.example.socialk.Map
import com.example.socialk.Profile
import com.example.socialk.di.ActivityViewModel
import com.example.socialk.di.ChatViewModel
import com.example.socialk.di.UserViewModel
import com.example.socialk.model.Activity
import com.example.socialk.model.Chat
import com.example.socialk.model.User
import com.example.socialk.model.UserData
import com.example.socialk.signinsignup.AuthViewModel
import com.example.socialk.ui.theme.SocialTheme
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.HashMap

@AndroidEntryPoint
class CreateFragment : Fragment() {
    private val viewModel by viewModels<CreateViewModel>()
    private val activityViewModel by viewModels<ActivityViewModel>()
    private val authViewModel by viewModels<AuthViewModel>()
    private val userViewModel by viewModels<UserViewModel>()
    private val chatViewModel by viewModels<ChatViewModel>()
    override fun onDestroyView() {
        super.onDestroyView()
        val sharedPref = requireContext().getSharedPreferences("create_data",Context.MODE_PRIVATE)
        // Get a reference to the SharedPreferences editor
        val editor = sharedPref.edit()
        // Put the data you want to save in the editor
        Log.d("CREATEFRAGMENT",viewModel.name.value.toString())
        editor.putString("name",viewModel.name.value.toString())
        editor.putString("description", viewModel.description.value.toString())
        editor.putString("date", viewModel.date.value.toString())
        editor.putString("start_time", viewModel.start_time.value.toString())
        editor.putString("duration",  viewModel.duration.value.toString())
        editor.putString("custom_location", viewModel.custom_location.value.toString())
        editor.putString("max",  viewModel.max.value.toString())
        editor.putString("min",  viewModel.min.value.toString())
        editor.putString("latlng",  viewModel.latlng.value.toString())
        // Save the changes
        editor.apply()
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        activityViewModel.activityAdded()
        viewModel.navigateTo.observe(viewLifecycleOwner) { navigateToEvent ->
            navigateToEvent.getContentIfNotHandled()?.let { navigateTo ->
                if (navigateTo==Screen.FriendsPicker){
                    val bundle=Bundle()
                    bundle.putSerializable("activity",viewModel.created_activity.value)
                    navigate(navigateTo, Screen.Create,bundle)

                }else{
                    navigate(navigateTo, Screen.Create)

                }
            }

        }

        val sharedPreferences = requireContext().getSharedPreferences("create_data", Context.MODE_PRIVATE)
        val name = sharedPreferences.getString("name", "")

        if (name!!.isNotEmpty()){
            viewModel.name.value=name}
        val description = sharedPreferences.getString("description", "")
        if (description!!.isNotEmpty()){viewModel.description.value=description}
        val date = sharedPreferences.getString("date", "")
        if (date!!.isNotEmpty()){viewModel.date.value=date}
        val start_time = sharedPreferences.getString("start_time", "")
        if (start_time!!.isNotEmpty()){viewModel.start_time.value=start_time}
        val duration = sharedPreferences.getString("duration", "")
        if (duration!!.isNotEmpty()){viewModel.duration.value=duration}
        val custom_location = sharedPreferences.getString("custom_location", "")
        if (custom_location!!.isNotEmpty()){viewModel.custom_location.value=custom_location}
        val max = sharedPreferences.getString("max", "")
        if (max!!.isNotEmpty()){viewModel.max.value=max}
        val min = sharedPreferences.getString("min", "")
        if (min!!.isNotEmpty()){viewModel.min.value=min}
        val latlng = sharedPreferences.getString("latlng", "")
        if (latlng!!.isNotEmpty()){viewModel.latlng.value=latlng}

        var location:String?=arguments?.getString("location")
        if(location!=null && location.isNotEmpty()){
            viewModel.latlng.value=location
        }
        userViewModel.getFriends(authViewModel.currentUser!!.uid)

        Log.d("mapscreen",location.toString())



        return ComposeView(requireContext()).apply {
            setContent {
                SocialTheme {
                    CreateScreen(viewModel,location,userViewModel,activityViewModel, onEvent = { event ->
                        when (event) {
                            is CreateEvent.GoToProfile -> viewModel.handleGoToProfile()
                            is CreateEvent.GoToHome -> {

                                viewModel.handleGoToHome()
                            }
                            is CreateEvent.LogOut -> viewModel.handleLogOut()
                            is CreateEvent.GoBack ->activity?.onBackPressedDispatcher?.onBackPressed()
                            is CreateEvent.ClearState -> activityViewModel.activityAdded()
                            is CreateEvent.GoToSettings -> viewModel.handleGoToSettings()
                            is CreateEvent.GoToEvent -> viewModel.handleGoToEvent()
                            is CreateEvent.GoToLive -> viewModel.handleGoToLive()
                            is CreateEvent.GoToActivity -> viewModel.handleGoToActivity()
                            is CreateEvent.GoToMap -> viewModel.handleGoToMap()
                            is CreateEvent.CreateActivity -> {
                                val uuid: UUID = UUID.randomUUID()
                                val id: String = uuid.toString()
                                val participants_profile_pictures: HashMap<String,String> = hashMapOf()
                                val participants_usernames: HashMap<String,String> = hashMapOf()
                                val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                                val current = LocalDateTime.now().format(formatter)

                                participants_profile_pictures[authViewModel.currentUser!!.uid]=UserData.user!!.pictureUrl!!
                                participants_usernames[authViewModel.currentUser!!.uid]=UserData.user!!.username!!

                                val activity=Activity(
                                    id = id,
                                    creator_id = if (authViewModel.currentUser == null) {
                                        ""
                                    } else {
                                        authViewModel.currentUser!!.uid.toString()
                                    },
                                    title = event.title,
                                    date = event.date,
                                    start_time = event.start_time,
                                    time_length = event.time_length,
                                    creator_name =UserData.user!!.name!! ,
                                    creator_profile_picture = UserData.user!!.pictureUrl!! ,
                                    creator_username = UserData.user!!.username!! ,
                                    description= event.description,
                                    time_left = "",
                                    custom_location = event.custom_location,
                                    end_time = "",latLng="",minUserCount=if(event.min.equals("")){0}else{event.min.toInt()},maxUserCount=if(event.max.equals("")){0}else{event.max.toInt()},
                                    disableChat = event.disableChat,  likes = 0,
                                    invited_users = arrayListOf(),
                                    participants_profile_pictures =participants_profile_pictures ,
                                    participants_usernames =participants_usernames,
                                    creation_time = current,
                                    location=event.location,
                                    pictures=HashMap(),
                                    enableActivitySharing=event.enableActivitySharing,disablePictures=event.disablePictures,disableNotification=event.disableNotification,privateChat=event.privateChat

                                )

                                viewModel.handleGoToFriendsPicker(activity)
                                clearSharedPrefs()


                            }
                            else->{}
                        }
                    },
                        bottomNavEvent = { screen ->
                            when (screen) {
                                is Home -> viewModel.handleGoToHome()
                                is Map -> viewModel.handleGoToMap()
                                is Chats -> viewModel.handleGoToChats()
                                is Profile -> viewModel.handleGoToProfile()
                                else->{}
                            }
                        })
                }
            }
        }
    }

    private fun clearSharedPrefs() {
        val sharedPref = requireContext().getSharedPreferences("create_data", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.clear()
        editor.apply()
    }

}
