package com.example.socialk.signinsignup

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.socialk.Main.Screen
import com.example.socialk.util.Event


class WelcomeViewModel() : ViewModel() {
    private val _navigateTo = MutableLiveData<Event<Screen>>()
    val navigateTo: LiveData<Event<Screen>> = _navigateTo

    fun handleGoToSignIn( ) {
            _navigateTo.value = Event(Screen.SignIn)
    }
    fun handleGoToRegister( ) {
        _navigateTo.value = Event(Screen.SignUp)
    }
    fun handleGoToHome( ) {
        _navigateTo.value = Event(Screen.Home)
    }
    fun handleGoToPickUsername( ) {
        _navigateTo.value = Event(Screen.PickUsername)
    }
}

