package com.example.socialk

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.socialk.Main.Screen
import com.example.socialk.model.Chat
import com.example.socialk.util.Event


class CreateGroupViewModel : ViewModel(){
    private val _navigateTo = MutableLiveData<Event<Screen>>()
    val navigateTo: LiveData<Event<Screen>> = _navigateTo
    private val _group_name = MutableLiveData<String>()
    val group_name: LiveData<String> = _group_name


    fun handleGoToFriendPicker(group_name: String) {
        _group_name.value=group_name
        _navigateTo.value = Event(Screen.FriendsPicker)
    }
    fun handleGoBack( ) {
        _navigateTo.value = Event(Screen.ChatCollection)
    }



}