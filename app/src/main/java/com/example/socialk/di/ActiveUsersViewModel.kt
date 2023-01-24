package com.example.socialk.di

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.socialk.ActiveUser
import com.example.socialk.model.Activity
import com.example.socialk.model.Response
import com.example.socialk.model.SocialException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ActiveUsersViewModel @Inject constructor(
    private val repo: ActivityRepository
) : ViewModel() {

    private val _activeUsersListState = mutableStateOf<Response<List<ActiveUser>>>(Response.Loading)
    val activeUsersListState: State<Response<List<ActiveUser>>> = _activeUsersListState

    private val _isActiveUsersAddedState = mutableStateOf<Response<Void?>?>(Response.Success(null))
    val isActiveUsersAddedState: State<Response<Void?>?> = _isActiveUsersAddedState

    private val _isActiveUsersDeletedState = mutableStateOf<Response<Void?>>(Response.Success(null))
    val isActiveUsersDeletedState: State<Response<Void?>> = _isActiveUsersDeletedState


    fun getActiveUsersForUser(id: String?) {
        if (id == null) {
            _activeUsersListState.value = Response.Failure(SocialException("getActiveUserForUser id passed is null",Exception()))
        } else {
            viewModelScope.launch {
                repo.getActiveUsers(id).collect { response ->
                    _activeUsersListState.value = response
                }
            }
        }

    }

    fun addActiveUser(activeUser: ActiveUser) {
        viewModelScope.launch {
            repo.addActiveUser(activeUser).collect { response ->
                _isActiveUsersAddedState.value = response
            }
        }
    }

    fun activeUserAdded() {
        _isActiveUsersAddedState.value = null
    }

    fun deleteActiveUser(id: String) {
        viewModelScope.launch {
            repo.deleteActiveUser(id).collect { response ->
                _isActiveUsersDeletedState.value = response
            }
        }
    }
}