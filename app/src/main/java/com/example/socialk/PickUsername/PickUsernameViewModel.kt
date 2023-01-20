package com.example.socialk.PickUsername

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.socialk.Main.Screen
import com.example.socialk.util.Event
import kotlinx.coroutines.launch

class PickUsernameViewModel :ViewModel(){
    private val _navigateTo = MutableLiveData<Event<Screen>>()
    val navigateTo: LiveData<Event<Screen>> = _navigateTo

    fun handleGoToHome( ) {
        _navigateTo.value = Event(Screen.Home)
    }

}