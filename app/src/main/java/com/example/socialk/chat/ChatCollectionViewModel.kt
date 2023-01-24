package com.example.socialk.chat

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.socialk.Main.Screen
import com.example.socialk.util.Event

class ChatCollectionViewModel :ViewModel(){
    private val _navigateTo = MutableLiveData<Event<Screen>>()
    val navigateTo: LiveData<Event<Screen>> = _navigateTo

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
    fun handleGoToChat( ) {
        Log.d("TAG","CJAT")
        _navigateTo.value = Event(Screen.Chat)
    }
    fun handleGoBack( ) {
        _navigateTo.value = Event(Screen.ChatCollection)
    }

}