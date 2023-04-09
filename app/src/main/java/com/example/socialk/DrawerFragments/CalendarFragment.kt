package com.example.socialk.DrawerFragments

import android.os.Bundle
import android.service.autofill.UserData
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.example.socialk.Main.Screen
import com.example.socialk.Main.navigate
import com.example.socialk.components.ActivityItem
import com.example.socialk.di.ActivityViewModel
import com.example.socialk.model.Response
import com.example.socialk.model.User
import com.example.socialk.ui.theme.SocialTheme

class CalendarFragment : Fragment(){
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
        activityViewModel.getJoinedActivities( com.example.socialk.model.UserData.user!!.id)


        return ComposeView(requireContext()).apply {
            setContent {
                SocialTheme {
                    Surface(Modifier.fillMaxSize().background(color=SocialTheme.colors.uiBackground)) {
                        CalendarScreen(activityViewModel,
                        onEvent = {event->
                            when(event){
                                is CalendarEvent.GoBack ->{activity?.onBackPressedDispatcher?.onBackPressed()}
                            }
                        })
                    }
                }
            }
        }
    }

}