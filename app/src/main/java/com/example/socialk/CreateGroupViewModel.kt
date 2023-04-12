package com.example.socialk

import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.core.net.toUri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.socialk.Main.Screen
import com.example.socialk.model.Chat
import com.example.socialk.util.Event
import kotlinx.coroutines.flow.MutableStateFlow


class CreateGroupViewModel : ViewModel(){
    private val _uri = MutableLiveData<Uri>()
    val uri: LiveData<Uri> = _uri
    private val _uriReceived = mutableStateOf(false)
    val uriReceived: State<Boolean> = _uriReceived


    private val _navigateTo = MutableLiveData<Event<Screen>>()
    val navigateTo: LiveData<Event<Screen>> = _navigateTo



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
    fun handleGoToFriendPicker(group_name: String) {
        _group_name.value=group_name
        _navigateTo.value = Event(Screen.FriendsPicker)
    }

    fun handleGoBack( ) {
        _navigateTo.value = Event(Screen.ChatCollection)
    }



}