package com.example.socialk.signinsignup

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.socialk.Main.Screen
import com.example.socialk.util.Event


class SignInViewModel() : ViewModel() {

    private val _navigateTo = MutableLiveData<Event<Screen>>()
    val navigateTo: LiveData<Event<Screen>>
        get() = _navigateTo

    /**
     * Consider all sign ins successful
     */
    fun signIn() {
        _navigateTo.value = Event(Screen.Map)
    }
    fun handleGoToPickUsername() {
        _navigateTo.value = Event(Screen.PickUsername)
    }

    fun signInAsGuest() {
    }

    fun signUp() {

    }
}
