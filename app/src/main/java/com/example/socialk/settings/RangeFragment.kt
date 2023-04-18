package com.example.socialk.settings

import android.os.Bundle
import android.util.Range
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.socialk.Main.Screen
import com.example.socialk.Main.navigate
import com.example.socialk.ui.theme.SocialTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RangeFragment : Fragment(){

    private val viewModel by viewModels<SettingsViewModel>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel.navigateTo.observe(viewLifecycleOwner) { navigateToEvent ->
            navigateToEvent.getContentIfNotHandled()?.let { navigateTo ->
                navigate(navigateTo, Screen.Range)
            }
        }
        return ComposeView(requireContext()).apply {
            setContent {
                SocialTheme {
                    RangeScreen(
                        onEvent = { event ->
                            when (event) {
                                is SettingsEvent.GoToProfile -> viewModel.handleGoToProfile()
                                is SettingsEvent.LogOut -> viewModel.handleLogOut()
                                is SettingsEvent.GoToSettings -> viewModel.handleGoToSettings()
                                is SettingsEvent.GoBack-> activity?.onBackPressedDispatcher?.onBackPressed()
                                is SettingsEvent.GoToHome -> viewModel.handleGoToHome()
                                is SettingsEvent.GoToRange -> viewModel.handleGoToRange()
                                is SettingsEvent.SaveRange -> {event.range}
                            }
                        }
                    )
                }
            }
        }
    }
}