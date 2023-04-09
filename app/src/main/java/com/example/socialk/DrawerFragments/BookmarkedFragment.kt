package com.example.socialk.DrawerFragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.socialk.Main.Screen
import com.example.socialk.Main.navigate
import com.example.socialk.ui.theme.SocialTheme

class BookmarkedFragment : Fragment(){
    private val drawerViewModel by viewModels<DrawerViewModel>()

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
        return ComposeView(requireContext()).apply {
            setContent {
                SocialTheme {

                }
            }
        }
    }

}