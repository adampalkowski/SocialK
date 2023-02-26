package com.example.socialk.chat

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.socialk.Main.Screen
import com.example.socialk.model.Chat
import com.example.socialk.model.User
import com.example.socialk.util.Event
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ChatCollectionViewModel :ViewModel(){
    private val _navigateTo = MutableLiveData<Event<Screen>>()
    val navigateTo: LiveData<Event<Screen>> = _navigateTo
    private val _chat = MutableLiveData<Chat>()
    val chat: LiveData<Chat> = _chat

    fun handleGoToProfile( ) {
        _navigateTo.value = Event(Screen.Profile)
    }
    fun handleLogOut( ) {
        _navigateTo.value = Event(Screen.Welcome)
    }
    fun handleGoToSettings( ) {
        _navigateTo.value = Event(Screen.Settings)
    }
    fun handleGoToSearch( ) {
        _navigateTo.value = Event(Screen.Search)
    }
    fun handleGoToHome( ) {
        _navigateTo.value = Event(Screen.Home)
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
    fun handleGoToChat(chat: Chat) {
        _chat.value=chat
        _navigateTo.value = Event(Screen.Chat)
    }
    fun handleGoBack( ) {

        _navigateTo.value = Event(Screen.ChatCollection)
    }



}