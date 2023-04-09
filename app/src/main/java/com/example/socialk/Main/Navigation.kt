package com.example.socialk.Main

import android.os.Bundle
import androidx.core.app.ActivityOptionsCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import com.example.socialk.R
import com.example.socialk.model.User
import java.security.InvalidParameterException
enum class Screen { Welcome, SignUp, SignIn,
    Home,Profile,Settings,Map,ChatCollection,Chat,UserProfile,
    Memories,Create,Live,Event,EditProfile,Search,PickUsername,FriendsPicker,CreateGroup,CreatedActivities,Bookmarked,Trending,Help,Info,Calendar}

fun Fragment.navigate(to: Screen, from: Screen,bundle: Bundle= Bundle.EMPTY) {

    if (to == from) {
    }
    when (to) {
        Screen.Welcome -> {
            findNavController().navigate(R.id.welcome_fragment)
        }
        Screen.CreatedActivities -> {
            findNavController().navigate(R.id.created_activities_fragment)
        }
        Screen.Trending -> {
            findNavController().navigate(R.id.trending_fragment)
        }
        Screen.Bookmarked -> {
            findNavController().navigate(R.id.bookmarked_fragment)
        }
        Screen.Help -> {
            findNavController().navigate(R.id.help_fragment)
        }
        Screen.Info -> {
            findNavController().navigate(R.id.info_fragment)
        }
        Screen.Calendar -> {
            findNavController().navigate(R.id.calendar_fragment)
        }
        Screen.CreateGroup -> {
            findNavController().navigate(R.id.create_group_fragment)
        }
        Screen.SignUp -> {
            findNavController().navigate(R.id.sign_up_fragment)
        }
        Screen.SignIn -> {
            findNavController().navigate(R.id.sign_in_fragment)
        }
        Screen.Home -> {
            findNavController().navigate(R.id.home_fragment)
        }
        Screen.Profile -> {
            findNavController().navigate(R.id.profile_fragment)
        }
        Screen.Settings -> {
            findNavController().navigate(R.id.settings_fragment)
        }
        Screen.Map -> {
            findNavController().navigate(R.id.map_fragment,args=bundle)
        }
        Screen.ChatCollection -> {
            findNavController().navigate(R.id.chats_collection_fragment)
        }
        Screen.Memories -> {

            findNavController().navigate(R.id.memories_fragment)
        }
        Screen.FriendsPicker -> {

            findNavController().navigate(R.id.friends_picker_fragment,args=bundle)
        }
        Screen.Create -> {
            findNavController().navigate(R.id.create_fragment,args=bundle)
        }
        Screen.Live -> {
            findNavController().navigate(R.id.live_fragment)
        }
        Screen.Event -> {
            findNavController().navigate(R.id.event_fragment)
        }
        Screen.EditProfile -> {
            findNavController().navigate(R.id.edit_profile_fragment)
        }
        Screen.Search -> {
            findNavController().navigate(R.id.search_fragment)
        }
        Screen.PickUsername -> {
            findNavController().navigate(R.id.pick_username_fragment)
        }
        Screen.Chat -> {
            findNavController().navigate(R.id.chat_fragment,args=bundle)
        }
        Screen.UserProfile -> {
            findNavController().navigate(R.id.user_profile_fragment,args=bundle)
        }
    }

}

