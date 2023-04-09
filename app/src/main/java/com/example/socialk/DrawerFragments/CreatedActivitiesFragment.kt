package com.example.socialk.DrawerFragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.example.socialk.CreateGroupEvent
import com.example.socialk.CreateGroupScreen
import com.example.socialk.CreateGroupViewModel
import com.example.socialk.Main.Screen
import com.example.socialk.Main.navigate
import com.example.socialk.di.ActivityViewModel
import com.example.socialk.model.Response
import com.example.socialk.model.UserData
import com.example.socialk.ui.theme.SocialTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreatedActivitiesFragment :Fragment(){
    private val drawerViewModel by viewModels<DrawerViewModel>()
    private val activityViewModel by activityViewModels<ActivityViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        drawerViewModel.navigateTo.observe(viewLifecycleOwner) { navigateToEvent ->
            navigateToEvent.getContentIfNotHandled()?.let { navigateTo ->
                    navigate(navigateTo, Screen.CreateGroup)
            }
        }
        activityViewModel.getUserActivities(UserData.user!!.id)
        return ComposeView(requireContext()).apply {
            setContent {
                SocialTheme {
                    Surface(Modifier.fillMaxSize()) {
                        CreatedActivitiesScreen(activityViewModel, onEvent = {event->
                            when(event){
                                is CreatedActivityEvent.GoBack ->{activity?.onBackPressedDispatcher?.onBackPressed()}
                            }
                        })
                    }
                }
            }
        }
    }

}