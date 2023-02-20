package com.example.socialk.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.socialk.Main.Screen
import com.example.socialk.model.Activity
import com.example.socialk.model.User
import com.example.socialk.util.Event
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow


class HomeViewModel : ViewModel() {
    private val _navigateTo = MutableLiveData<Event<Screen>>()
    val navigateTo: LiveData<Event<Screen>> = _navigateTo
    private val _clicked_chat_activity = MutableLiveData<Activity>()
    val clicked_chat_activity: LiveData<Activity> = _clicked_chat_activity

    private val _showDialog = MutableStateFlow(false)
    val showDialog: StateFlow<Boolean> = _showDialog.asStateFlow()

    private val _activity = MutableLiveData<Activity?>()
    val activity: LiveData<Activity?> = _activity

    private val _activity_link = MutableLiveData<String?>()
    val activity_link: LiveData<String?> = _activity_link
    fun setShowDialog(b: Boolean) {
        _showDialog.value=b
    }
    fun resetLink() {
        _activity_link.value=null
    }
    fun setActivity(data: Activity) {
        _activity.value=data
    }
    fun removeActivity() {
        _activity.value=null
    }
    fun setActivityLink(link:String){
        _activity_link.value=link
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
        _navigateTo.value = Event(Screen.Home)
    }
    fun handleGoToChat( activity:Activity) {
        _clicked_chat_activity.value = activity
        _navigateTo.value = Event(Screen.Chat)
    }
    fun handleGoToMap( ) {
        _navigateTo.value = Event(Screen.Map)
    }
    fun handleGoToChats( ) {
        _navigateTo.value = Event(Screen.ChatCollection)
    }
    fun handleGoToMemories( ) {
        _navigateTo.value = Event(Screen.Memories)
    }
    fun handleGoToCreate( ) {
        _navigateTo.value = Event(Screen.Create)
    }




}
