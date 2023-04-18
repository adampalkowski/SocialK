package com.example.socialk.map

import android.location.Location
import android.net.Uri
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

class MapViewModel : ViewModel(){
    private val _navigateTo = MutableLiveData<Event<Screen>>()
    val navigateTo: LiveData<Event<Screen>> = _navigateTo
    private val _granted_permission = MutableStateFlow<Boolean>(false)
    val granted_permission: StateFlow<Boolean> = _granted_permission
    private val _location = MutableStateFlow<LatLng?>(null)
    val location: StateFlow<LatLng?> = _location

    private val _locations_picked = MutableStateFlow< LatLng?>(null)
    val locations_picked: StateFlow< LatLng?> = _locations_picked
    private val _clicked_chat_activity = MutableLiveData<Activity>()
    val clicked_chat_activity: LiveData<Activity> = _clicked_chat_activity
    private val _clicked_location_activity = MutableLiveData<String>()
    val clicked_location_activity: LiveData<String> = _clicked_location_activity
    private val _clicked_location = MutableLiveData<LatLng?>()
    val clicked_location: LiveData<LatLng?> = _clicked_location

    private val _clicked_profile = MutableLiveData<String>()
    val clicked_profile: LiveData<String> = _clicked_profile

    private val _activity = MutableLiveData<Activity?>()
    val activity: LiveData<Activity?> = _activity
    private val _activityID = MutableLiveData<String?>()
    val activityID: LiveData<String?> = _activityID
    private val _photo_uri = MutableStateFlow<Uri?>("".toUri())
    val photo_uri: MutableStateFlow<Uri?> = _photo_uri

    private val _activity_link = MutableLiveData<String?>()
    val activity_link: LiveData<String?> = _activity_link
    fun permissionGranted(){
        _granted_permission.value=true
    }
    fun setActivityID(activity_ID:String){
        _activityID.value=activity_ID
    }
    fun resetActivityID(){
        _activityID.value=null
    }
    fun setLocationPicked(location: LatLng?){
        _locations_picked.value=location
    }

    fun handleGoToChat( activity:Activity) {
        _clicked_chat_activity.value = activity
        _navigateTo.value = Event(Screen.Chat)
    }
    fun handleGoToSearch( ) {

            _navigateTo.value = Event(Screen.Search)
    }
    fun handleGoToGroup() {
            _navigateTo.value = Event(Screen.CreateGroup)
    }
    fun handleGoToEditProfile() {
            _navigateTo.value = Event(Screen.EditProfile)
    }
    fun handleGoToUserProfile(user_id:String){
        _clicked_profile.value=user_id
        _navigateTo.value = Event(Screen.UserProfile)
    }
    fun handleGoToMapActivity(latLng: String ) {
        _clicked_location_activity.value=latLng
        _navigateTo.value = Event(Screen.Map)
    }
    fun handleGoToFriendsPicker(activity: Activity) {
        _clicked_chat_activity.value = activity
        _navigateTo.value = Event(Screen.FriendsPicker)
    }
    fun setLocation(location: LatLng){
        _location.value=location
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
    fun handleGoToMap( ) {
        _navigateTo.value = Event(Screen.Map)
    }
    fun handleGoToCalendar() {
        _navigateTo.value = Event(Screen.Calendar)
    }
    fun handleGoToCreated( ) {
        _navigateTo.value = Event(Screen.CreatedActivities)
    }
    fun handleGoToTrending( ) {
        _navigateTo.value = Event(Screen.Trending)
    }
    fun handleGoToBookmarked( ) {
        _navigateTo.value = Event(Screen.Bookmarked)
    }
    fun handleGoToHelp( ) {
        _navigateTo.value = Event(Screen.Help)
    }
    fun handleGoToInfo( ) {
        _navigateTo.value = Event(Screen.Info)
    }
    fun handleGoToChats( ) {
        _navigateTo.value = Event(Screen.ChatCollection)
    }
    fun handleGoToCreate(latLng: LatLng? ) {
        _clicked_location.value =latLng
        _navigateTo.value = Event(Screen.Create)
    }
}