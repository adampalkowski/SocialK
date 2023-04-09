package com.example.socialk.DrawerFragments

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.socialk.Main.Screen
import com.example.socialk.util.Event

class DrawerViewModel : ViewModel(){
    private val _navigateTo = MutableLiveData<Event<Screen>>()
    val navigateTo: LiveData<Event<Screen>> = _navigateTo

    fun handleGoToFriendPicker(group_name: String) {
        _navigateTo.value = Event(Screen.FriendsPicker)
    }
    fun handleGoBack( ) {
        _navigateTo.value = Event(Screen.ChatCollection)
    }



}