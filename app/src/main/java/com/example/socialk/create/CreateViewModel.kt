package com.example.socialk.create

import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.core.net.toUri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.socialk.Main.Screen
import com.example.socialk.model.Activity
import com.example.socialk.util.Event
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow


class CreateViewModel : ViewModel() {
    private val _uri = MutableLiveData<Uri>()
    val uri: LiveData<Uri> = _uri
    private val _uriReceived = mutableStateOf(false)
    val uriReceived: State<Boolean> = _uriReceived

    private val _navigateTo = MutableLiveData<Event<Screen>>()
    val navigateTo: LiveData<Event<Screen>> = _navigateTo
    private val _created_activity = MutableLiveData<Activity>()
    val created_activity: LiveData<Activity> = _created_activity


    var name =mutableStateOf("")
    var description =mutableStateOf("")
    var date =mutableStateOf("")
    var start_time =mutableStateOf("")
    var duration =mutableStateOf("")
    var custom_location =mutableStateOf("")
    var max =mutableStateOf("")
    var min =mutableStateOf("")
    var latlng =mutableStateOf("")

    var shouldShowCamera: MutableState<Boolean> = mutableStateOf(false)
    var shouldShowPhoto: MutableState<Boolean> = mutableStateOf(false)
    var displayPhoto: MutableState<Boolean> = mutableStateOf(false)
    var camera_activity_id: MutableState<String> = mutableStateOf("")
    private val _group_name = MutableLiveData<String>()
    val group_name: LiveData<String> = _group_name
    private val _photo_uri = MutableStateFlow<Uri?>("".toUri())
    val photo_uri: MutableStateFlow<Uri?> = _photo_uri
    fun setPhotoUri(uri: Uri) {
        _photo_uri.value=uri
    }
    fun onUriReceived(uri: Uri) {
        _uri.value = uri
        _uriReceived.value = true
    }

    fun onUriProcessed() {
        _uriReceived.value = false
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
    fun handleGoToFriendsPicker(activity: Activity) {
        _created_activity.value=activity
        _navigateTo.value = Event(Screen.FriendsPicker)
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
    fun handleGoToMemories( ) {
        _navigateTo.value = Event(Screen.Memories)
    }
    fun handleGoToCreate( ) {
        _navigateTo.value = Event(Screen.Create)
    }
    fun handleGoToLive( ) {
        _navigateTo.value = Event(Screen.Live)
    }
    fun handleGoToActivity( ) {
        _navigateTo.value = Event(Screen.Create)
    }
    fun handleGoToEvent( ) {
        _navigateTo.value = Event(Screen.Event)
    }
   fun handleCreateActivity( ) {
        _navigateTo.value = Event(Screen.Event)
    }

}
