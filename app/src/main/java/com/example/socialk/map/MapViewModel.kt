package com.example.socialk.map

import android.location.Location
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

    private val _clicked_location = MutableLiveData<LatLng?>()
    val clicked_location: LiveData<LatLng?> = _clicked_location
    fun permissionGranted(){
        _granted_permission.value=true
    }
    fun setLocationPicked(location: LatLng){
        _locations_picked.value=location
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
    fun handleGoToChats( ) {
        _navigateTo.value = Event(Screen.ChatCollection)
    }
    fun handleGoToCreate(latLng: LatLng? ) {
        _clicked_location.value =latLng
        _navigateTo.value = Event(Screen.Create)

    }
}