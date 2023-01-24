package com.example.socialk

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.socialk.Main.Screen
import com.example.socialk.model.User
import com.example.socialk.util.Event

class SearchViewModel : ViewModel(){
    private val _navigateTo = MutableLiveData<Event<Screen>>()
    val navigateTo: LiveData<Event<Screen>> = _navigateTo

    fun handleGoToProfile( ) {
        _navigateTo.value = Event(Screen.Profile)
    }

    fun handleGoToHome( ) {
        _navigateTo.value = Event(Screen.Home)
    }
    fun handleGoToChats( ) {
        _navigateTo.value = Event(Screen.ChatCollection)
    }

    fun handleGoToUserProfile( ) {
        _navigateTo.value = Event(Screen.UserProfile)
    }


}