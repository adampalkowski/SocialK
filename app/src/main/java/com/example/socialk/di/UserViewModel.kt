package com.example.socialk.di

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.socialk.model.*
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.ListenerRegistration
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class UserViewModel @Inject constructor(
    private val repo: UserRepository
) : ViewModel() {
    val userProfileId = MutableLiveData<String>()
    val _currentUserProfile = MutableStateFlow<User?>(null)
    val currentUserProfile: StateFlow<User?> = _currentUserProfile
    private val _userValidation = MutableStateFlow<Response<Boolean>>(Response.Loading)
    val userValidation: StateFlow<Response<Boolean>> = _userValidation

    private val _userState = MutableStateFlow<Response<User>>(Response.Loading)
    val userState: StateFlow<Response<User>> = _userState


    private val _friendState = MutableStateFlow<Response<ArrayList<User>>>(Response.Loading)
    val friendState: StateFlow<Response<ArrayList<User>>> = _friendState

    private val _friendMoreState = MutableStateFlow<Response<ArrayList<User>>>(Response.Loading)
    val friendMoreState: StateFlow<Response<ArrayList<User>>> = _friendMoreState


    private val _isUserAddedState = mutableStateOf<Response<Void?>?>(Response.Success(null))
    val isUserAddedState: State<Response<Void?>?> = _isUserAddedState

    private val _isUsernameAddedFlow = MutableStateFlow<Response<Void?>>(Response.Loading)
    val isUsernameAddedFlow: StateFlow<Response<Void?>> = _isUsernameAddedFlow

    private val _loginFlow = MutableStateFlow<Response<FirebaseUser>?>(null)
    val loginFlow: StateFlow<Response<FirebaseUser>?> = _loginFlow

    private val _isUserDeletedState = mutableStateOf<Response<Void?>>(Response.Success(null))
    val isUserDeletedState: State<Response<Void?>> = _isUserDeletedState
    private val _isUserUpdated = mutableStateOf<Response<Void?>>(Response.Success(null))
    val isUserUpdated: State<Response<Void?>> = _isUserUpdated


    private val _isInviteAddedState = mutableStateOf<Response<Void?>>(Response.Success(null))
    val isInviteAddedState: State<Response<Void?>> = _isInviteAddedState

    private val _isInviteRemovedState = mutableStateOf<Response<Void?>>(Response.Success(null))
    val isInviteRemovedState: State<Response<Void?>> = _isInviteRemovedState

    private val _isBlockedRemovedState = mutableStateOf<Response<Void?>>(Response.Success(null))
    val isBlockedRemovedState: State<Response<Void?>> = _isBlockedRemovedState

    private val _isBlockedAddedState = mutableStateOf<Response<Void?>>(Response.Success(null))
    val isBlockedAddedState: State<Response<Void?>> = _isBlockedAddedState

    private val _isFriendAddedState = mutableStateOf<Response<Void?>>(Response.Success(null))
    val isFriendAddedState: State<Response<Void?>> = _isFriendAddedState

    private val _isFriendRemovedState = mutableStateOf<Response<Void?>>(Response.Success(null))
    val isFriendRemovedState: State<Response<Void?>> = _isFriendRemovedState

    private val _isFriendAddedToBothUsersState =
        mutableStateOf<Response<Void?>>(Response.Success(null))
    val isFriendAddedToBothUsersState: State<Response<Void?>> = _isFriendAddedToBothUsersState

    private val _isFriendRemovedFromBothUsersState =
        mutableStateOf<Response<Void?>>(Response.Success(null))
    val isFriendRemovedFromBothUsersState: State<Response<Void?>> =
        _isFriendRemovedFromBothUsersState

    private val _isChatCollectionAddedToUsersState =
        mutableStateOf<Response<Void?>>(Response.Success(null))
    val isChatCollectionAddedToUsersState: State<Response<Void?>> =
        _isChatCollectionAddedToUsersState
    private val _isChatAddedToUsersState = mutableStateOf<Response<Void?>>(Response.Success(null))
    val isChatAddedToUsersState: State<Response<Void?>> = _isChatAddedToUsersState

    private val _invitesStateFlow = mutableStateOf<Response<ArrayList<User>>>(Response.Loading)
    val invitesStateFlow: State<Response<ArrayList<User>>> = _invitesStateFlow

    private val _pictureAddedStateFlow = mutableStateOf<Response<User>>(Response.Loading)
    val pictureAddedStateFlow: State<Response<User>> = _pictureAddedStateFlow

    private val _isInviteAcceptedState = mutableStateOf<Response<Void?>>(Response.Success(null))
    val isInviteAcceptedState: State<Response<Void?>> = _isInviteAcceptedState
    private val _isChatCollectionRecreatedState = mutableStateOf<Response<Void?>>(Response.Success(null))
    val isChatCollectionRecreatedState: State<Response<Void?>> = _isChatCollectionRecreatedState

    private val _isImageAddedToStorageState = mutableStateOf<Response<String>>(Response.Loading)
    val isImageAddedToStorageState: State<Response<String>> = _isImageAddedToStorageState

    private val _isUserProfilePictureChangedState =
        mutableStateOf<Response<Void?>>(Response.Success(null))
    val isUserProfilePictureChangedState: State<Response<Void?>> = _isUserProfilePictureChangedState

    private var registration: ListenerRegistration? = null

    fun addChatCollectionToUsers(id: String, friend_id: String, chat_id: String) {
        viewModelScope.launch {
            repo.addChatCollectionToUsers(id, friend_id, chat_id).collect { response ->
                _isChatCollectionAddedToUsersState.value = response
            }
        }
    }

    fun getFriends(id: String) {
        viewModelScope.launch {
            repo.getFriends(id).collect { response ->
                _friendState.value = response
            }
        }
    }
    fun getMoreFriends(id: String) {
        viewModelScope.launch {
            repo.getMoreFriends(id).collect { response ->
                _friendMoreState.value = response
                registration?.remove()
            }
        }
    }
    fun acceptInvite(current_user: User, user: User, chat: Chat) {
        viewModelScope.launch {
            repo.acceptInvite(current_user, user, chat).collect { response ->
                _isInviteAcceptedState.value = response
            }
        }
    }
    fun recreateChatCollection(current_user_id: String, user_id: String, chat: Chat) {
        viewModelScope.launch {
            repo.recreateChatCollection(current_user_id, user_id, chat).collect { response ->
                _isChatCollectionRecreatedState.value = response
            }
        }
    }

    fun changeUserProfilePicture(user_id: String, picture_uri: Uri) {
        viewModelScope.launch {
            Log.d("ImagePicker", "changeUserProfilePicture called")
            repo.addProfilePictureToStorage(user_id, picture_uri).collect { response ->
                when (response) {
                    is Response.Success -> {
                        val imageUrl=response.data
                        Log.d("UserViewModel", "changeUserProfilePicture response"+response.data)
                        repo.changeUserProfilePicture(user_id, response.data).collect { response ->
                            when (response) {
                                is Response.Success -> {
                                    Log.d("UserViewModel", "changeUserProfilePicture response"+response.data)
                                    currentUserProfile.value?.pictureUrl = imageUrl
                                    UserData.user!!.pictureUrl = imageUrl
                                    Log.d("Edit_profile_screen", "get user called")
                                     registration=repo.getUser(user_id).collect { response ->
                                        _userState.value = response
                                        when (response) {
                                            is Response.Success -> {
                                                UserData.user = response.data
                                            }
                                            else->{}
                                        }

                                    }
                                }
                                is Response.Failure -> {
                                    Log.d("ImagePicker", response.e.message)
                                }
                                else->{}
                            }

                        }
                    }
                    is Response.Failure -> {
                        Log.d("ImagePicker", response.e.message)
                    }
                    else->{}
                }

            }

        }
    }

    fun addImageToStorage(id: String, picture_uri: Uri) {
        viewModelScope.launch {
            repo.addProfilePictureToStorage(id, picture_uri).collect { response ->
                _isImageAddedToStorageState.value = response
            }
        }
    }

    fun addFriendToBothUsers(my_id: String, friend_id: String) {
        viewModelScope.launch {
            repo.addFriendToBothUsers(my_id, friend_id).collect { response ->
                _isFriendAddedToBothUsersState.value = response
            }
        }
    }

    fun removeFriendFromBothUsers(my_id: String, friend_id: String) {
        viewModelScope.launch {
            repo.removeFriendFromBothUsers(my_id, friend_id).collect { response ->
                _isFriendRemovedFromBothUsersState.value = response
            }
        }
    }

    fun addFriendIdToUser(my_id: String, friend_id: String) {
        viewModelScope.launch {
            repo.addInvitedIDs(my_id, friend_id).collect { response ->
                _isFriendAddedState.value = response
            }
        }
    }

    fun removeFriendIdFromUser(my_id: String, friend_id: String) {
        viewModelScope.launch {
            repo.removeInvitedIDs(my_id, friend_id).collect { response ->
                _isFriendRemovedState.value = response
            }
        }
    }

    fun addBlockedIdToUser(my_id: String, blocked_id: String) {
        viewModelScope.launch {
            repo.addInvitedIDs(my_id, blocked_id).collect { response ->
                _isBlockedAddedState.value = response
            }
        }
    }

    fun removeBlockedIdFromUser(my_id: String, blocked_id: String) {
        viewModelScope.launch {
            repo.removeInvitedIDs(my_id, blocked_id).collect { response ->
                _isBlockedRemovedState.value = response
            }
        }
    }

    fun removeInvitedIdFromUser(my_id: String, invited_id: String) {
        viewModelScope.launch {
            repo.removeInvitedIDs(my_id, invited_id).collect { response ->
                _isInviteRemovedState.value = response
            }
        }
    }

    fun addInvitedIdToUser(my_id: String, invited_id: String) {
        viewModelScope.launch {
            repo.addInvitedIDs(my_id, invited_id).collect { response ->
                _isInviteAddedState.value = response
            }
        }
    }


    fun getInvites(id: String) {
        viewModelScope.launch {
            repo.getInvites(id).collect { response ->
                _invitesStateFlow.value = response
            }
        }
    }


    fun resetUserValue() {
        _userState.value = Response.Loading
    }

    fun setCurrentUser(user: User) {
        _currentUserProfile.value = user
    }

    fun getUser(id: String) {
        viewModelScope.launch {
            repo.getUser(id).collect { response ->
                _userState.value = response
            }
        }
    }

    fun getUserListener(id: String) {
        viewModelScope.launch {
            repo.getUserListener(id).collect { response ->
                _userState.value = response
            }
        }
    }

    //eror prone ? uses _userState same as get user above
    fun getUserByUsername(username: String) {
        viewModelScope.launch {
            repo.getUserByUsername(username).collect { response ->
                _userState.value = response
            }
        }
    }

    fun addUser(user: User) {
        viewModelScope.launch {
            repo.addUser(user).collect { response ->
                _isUserAddedState.value = response
            }
        }
    }

    fun userAdded() {
        _isUserAddedState.value = null
    }

    fun deleteUser(id: String) {
        viewModelScope.launch {
            repo.deleteUser(id).collect { response ->
                _isUserDeletedState.value = response
            }
        }
    }

    fun addUsernameToUser(id: String, username: String) {
        viewModelScope.launch {
            repo.getUserByUsername(username).collect { response ->
                when (response) {
                    is Response.Success -> {
                        _isUsernameAddedFlow.value =
                            Response.Failure(
                                e = SocialException(
                                    "addUsernameToUser erro user with same username has been found",
                                    Exception()
                                )
                            )
                    }
                    is Response.Failure -> {
                        repo.addUsernameToUser(id = id, username = username).collect { response ->
                            _isUsernameAddedFlow.value = response
                        }
                    }
                    else->{}
                }
            }

        }
    }


    fun validateUser(firebaseUser: FirebaseUser) {
        val id: String = firebaseUser.uid
        viewModelScope.launch {
            repo.getUser(id).collect { response ->
                when (response) {
                    is Response.Success -> {
                        val user: User = response.data
                        //emails don't match
                        //TODO SHOW CORRECT EXCEPTION
                        if (user.email != firebaseUser.email) {
                            _userValidation.value = Response.Failure(
                                SocialException(
                                    message = "validate user error emails dont match",
                                    Exception()
                                )
                            )
                        }
                        if (user.username == null) {
                            UserData.user = user
                            Log.d("TAG", "succes")
                            _userValidation.value = Response.Success(false)
                        } else {
                            //SET THE GLOBAL USER
                            UserData.user = user
                            Log.d("TAG", "succes")
                            _userValidation.value = Response.Success(true)
                        }

                    }
                    is Response.Failure -> {
                        //issue with retreiving user from database
                        _userValidation.value = Response.Failure(
                            SocialException(
                                "validate error issue with retreiving user from database",
                                Exception()
                            )
                        )
                    }
                    else->{}
                }


            }
        }

    }

    fun profileChanges(id:String,firstAndLastName: String, description: String) {
        viewModelScope.launch {
            repo.updateUser(id,firstAndLastName, description =description ).collect { response ->
                _isUserUpdated.value = response
            }
        }
    }

}