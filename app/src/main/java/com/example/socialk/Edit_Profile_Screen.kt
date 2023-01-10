package com.example.socialk

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.socialk.components.ScreenHeading
import com.example.socialk.ui.theme.SocialTheme

sealed class EditProfileEvent {
    object GoToProfile : EditProfileEvent()
    object LogOut : EditProfileEvent()
    object GoToSettings : EditProfileEvent()
    object GoToEditProfile : EditProfileEvent()
    object GoToHome : EditProfileEvent()
}

@Composable
fun EditProfileScreen(onEvent: (EditProfileEvent) -> Unit,){
    Column(modifier = Modifier.fillMaxSize()) {
        ScreenHeading(onClick = {onEvent(EditProfileEvent.GoToProfile)}, title = "Edit profile")
    }
}

@Preview
@Composable
fun EditProfileScreenPreview(){
    SocialTheme() {
        EditProfileScreen(onEvent = {})

    }
}