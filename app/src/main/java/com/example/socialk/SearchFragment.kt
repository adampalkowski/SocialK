package com.example.socialk

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.compose.DialogHost
import androidx.navigation.fragment.findNavController
import com.example.socialk.Main.Screen
import com.example.socialk.Main.navigate
import com.example.socialk.di.UserViewModel
import com.example.socialk.ui.theme.SocialTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchFragment: Fragment() {
    private val viewModel by viewModels<SearchViewModel>()
    private val userViewModel by activityViewModels<UserViewModel>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel.navigateTo.observe(viewLifecycleOwner) { navigateToEvent ->
            navigateToEvent.getContentIfNotHandled()?.let { navigateTo ->
                    navigate(navigateTo, Screen.Search)

            }
        }
        return ComposeView(requireContext()).apply {
            setContent {
                SocialTheme {
                    SearchScreen(userViewModel,
                        onEvent = { event ->
                            when (event) {
                                is SearchEvent.GoToProfile -> viewModel.handleGoToChats()
                                is SearchEvent.GoToProfile -> viewModel.handleGoToProfile()
                                is SearchEvent.GoBack ->    activity?.onBackPressedDispatcher?.onBackPressed()
                                is SearchEvent.GoToUserProfile ->{

                                    userViewModel.setUserProfileId(event.user.id)
                                    userViewModel.setUserProfile(event.user)
                                    viewModel.handleGoToUserProfile()
                                }
                            }
                        }
                    )
                }
            }
        }
    }

}

