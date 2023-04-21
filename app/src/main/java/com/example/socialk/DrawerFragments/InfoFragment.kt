package com.example.socialk.DrawerFragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.socialk.Main.Screen
import com.example.socialk.Main.navigate
import com.example.socialk.R
import com.example.socialk.ui.theme.SocialTheme
import com.google.android.material.navigation.NavigationView

class InfoFragment: Fragment(){
    private val drawerViewModel by viewModels<DrawerViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val navController = findNavController()
        drawerViewModel.navigateTo.observe(viewLifecycleOwner) { navigateToEvent ->
            navigateToEvent.getContentIfNotHandled()?.let { navigateTo ->
                navigate(navigateTo, Screen.CreateGroup)
            }
        }
        return ComposeView(requireContext()).apply {
            setContent {
                SocialTheme {
                    InfoScreen(navController,onEvent = {event->
                        when(event){
                            is InfoEvent.GoBack ->{activity?.onBackPressedDispatcher?.onBackPressed()}
                            else->{}
                        }
                    })
                }
            }
        }
    }

}