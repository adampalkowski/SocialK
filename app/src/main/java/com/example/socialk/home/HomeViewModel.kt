package com.example.socialk.home

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.core.net.toUri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.socialk.Main.Screen
import com.example.socialk.model.Activity
import com.example.socialk.model.User
import com.example.socialk.util.Event
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow


class HomeViewModel : ViewModel() {
    private val _navigateTo = MutableLiveData<Event<Screen>>()
    val navigateTo: LiveData<Event<Screen>> = _navigateTo
    private val _clicked_chat_activity = MutableLiveData<Activity>()
    val clicked_chat_activity: LiveData<Activity> = _clicked_chat_activity
    private val _clicked_location_activity = MutableLiveData<String>()
    val clicked_location_activity: LiveData<String> = _clicked_location_activity
     var shouldShowCamera: MutableState<Boolean> = mutableStateOf(false)
     var shouldShowPhoto: MutableState<Boolean> = mutableStateOf(false)
     var displayPhoto: MutableState<Boolean> = mutableStateOf(false)
     var camera_activity_id: MutableState<String> = mutableStateOf("")

    private val _showDialog = MutableStateFlow(false)
    val showDialog: StateFlow<Boolean> = _showDialog.asStateFlow()

    private val _clicked_profile = MutableLiveData<String>()
    val clicked_profile: LiveData<String> = _clicked_profile

    private val _activity = MutableLiveData<Activity?>()
    val activity: LiveData<Activity?> = _activity
    private val _photo_uri = MutableStateFlow<Uri?>("".toUri())
    val photo_uri: MutableStateFlow<Uri?> = _photo_uri

    private val _activity_link = MutableLiveData<String?>()
    val activity_link: LiveData<String?> = _activity_link
    fun setShowDialog(b: Boolean) {
        _showDialog.value=b
    }
    fun setPhotoUri(uri: Uri) {
        _photo_uri.value=uri
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
    fun handleGoToUserProfile(user_id:String){
        _clicked_profile.value=user_id
        _navigateTo.value = Event(Screen.UserProfile)
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
    fun handleGoToMapActivity(latLng: String ) {
        _clicked_location_activity.value=latLng
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

    fun handleGoToFriendsPicker(activity: Activity) {
        _clicked_chat_activity.value = activity
        _navigateTo.value = Event(Screen.FriendsPicker)
    }


}
