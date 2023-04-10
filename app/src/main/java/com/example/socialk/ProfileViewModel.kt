package com.example.socialk

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.socialk.Main.Screen
import com.example.socialk.util.Event

class ProfileViewModel : ViewModel() {
    private val _navigateTo = MutableLiveData<Event<Screen>>()
    val navigateTo: LiveData<Event<Screen>> = _navigateTo

    fun handleImagePicker(){

    }
    fun handleGoToProfile( ) {
        _navigateTo.value = Event(Screen.Profile)
    }
    fun handleLogOut( ) {
        _navigateTo.value = Event(Screen.Welcome)
    }
    fun handleGoToSettings( ) {
        _navigateTo.value = Event(Screen.Settings)
    }
    fun handleGoToHome( ) {
        _navigateTo.value = Event(Screen.Map)
    }
    fun handleGoToMap( ) {
        _navigateTo.value = Event(Screen.Map)
    }
    fun handleGoToChats( ) {
        _navigateTo.value = Event(Screen.ChatCollection)
    }
    fun handleGoToCreate( ) {
        _navigateTo.value = Event(Screen.Create)
    }
    fun handleGoToEditProfile( ) {
        _navigateTo.value = Event(Screen.EditProfile)
    }
    fun handleGoToSearch( ) {
        _navigateTo.value = Event(Screen.Search)
    }
}
