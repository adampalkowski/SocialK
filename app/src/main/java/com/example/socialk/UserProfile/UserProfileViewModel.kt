package com.example.socialk.UserProfile

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.socialk.Main.Screen
import com.example.socialk.model.Response
import com.example.socialk.model.User
import com.example.socialk.util.Event

class UserProfileViewModel:ViewModel() {
   private val _navigateTo = MutableLiveData<Event<Screen>>()
    val navigateTo: LiveData<Event<Screen>> = _navigateTo


    private val _inviteEventState = mutableStateOf<Boolean>(false)
    val inviteEventState: State<Boolean> = _inviteEventState

    fun inviteSent(){
        _inviteEventState.value=true
    }
    fun inviteRemoved(){
        _inviteEventState.value=false
    }

    fun handleGoToProfile( ) {
        _navigateTo.value = Event(Screen.Profile)
    }

    fun handleGoToSettings( ) {
        _navigateTo.value = Event(Screen.Settings)
    }
    fun handleGoToHome( ) {
        _navigateTo.value = Event(Screen.Home)
    }

    fun handleGoToChats( ) {
        _navigateTo.value = Event(Screen.ChatCollection)
    }
    fun handleGoToChat( ) {
        _navigateTo.value = Event(Screen.Chat)
    }
    fun handleGoToEditProfile( ) {
        _navigateTo.value = Event(Screen.EditProfile)
    }
    fun handleGoToSearch( ) {
        _navigateTo.value = Event(Screen.Search)
    }
}