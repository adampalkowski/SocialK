package com.example.socialk.Main

import androidx.core.app.ActivityOptionsCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import com.example.socialk.R
import java.security.InvalidParameterException
enum class Screen { Welcome, SignUp, SignIn,
    Home,Profile,Settings,Map,Chats,
    Memories,Create,Live,Event,EditProfile,Search,PickUsername}

fun Fragment.navigate(to: Screen, from: Screen) {
    if (to == from) {
    }
    when (to) {
        Screen.Welcome -> {
            findNavController().navigate(R.id.welcome_fragment)
        }
        Screen.SignUp -> {
            findNavController().navigate(R.id.sign_up_fragment)
        }
        Screen.SignIn -> {
            findNavController().navigate(R.id.sign_in_fragment)
        }
        Screen.Home -> {
            val navController = findNavController()
            navController.navigate(R.id.home_fragment)
        }
        Screen.Profile -> {
            findNavController().navigate(R.id.profile_fragment)
        }
        Screen.Settings -> {
            findNavController().navigate(R.id.settings_fragment)
        }
        Screen.Map -> {
            findNavController().navigate(R.id.map_fragment)
        }
        Screen.Chats -> {
            findNavController().navigate(R.id.chats_fragment)
        }
        Screen.Memories -> {
            findNavController().navigate(R.id.memories_fragment)
        }
        Screen.Create -> {
            findNavController().navigate(R.id.create_fragment)
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
    }

}