package com.example.socialk.di

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.socialk.model.Activity
import com.example.socialk.model.Response
import com.example.socialk.model.User
import com.example.socialk.model.UserData
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Exception
import javax.inject.Inject


@HiltViewModel
class UserViewModel @Inject constructor(
    private val repo: UserRepository
) : ViewModel() {

    private val _userValidation= MutableStateFlow<Response<Boolean>>(Response.Loading)
    val userValidation : StateFlow<Response<Boolean>> =_userValidation

    private val _userState= mutableStateOf<Response<User>>(Response.Loading)
    val userState : State<Response<User>> =_userState

    private val _isUserAddedState = mutableStateOf<Response<Void?>?>(Response.Success(null))
    val isUserAddedState: State<Response<Void?>?> = _isUserAddedState

    private val _isUserDeletedState = mutableStateOf<Response<Void?>>(Response.Success(null))
    val isUserDeletedState: State<Response<Void?>> = _isUserDeletedState

    fun getUser(id:String){
        viewModelScope.launch {
            repo.getUser(id).collect{ response->
                _userState.value=response
            }
        }
    }

    fun addUser(user: User){
        viewModelScope.launch {
            repo.addUser(user).collect{ response ->
                _isUserAddedState.value=response
            }
        }
    }

    fun userAdded(){
        _isUserAddedState.value=null
    }

    fun deleteUser(id:String){
        viewModelScope.launch {
            repo.deleteUser(id).collect{ response ->
                _isUserDeletedState.value=response
            }
        }
    }

     fun validateUser(firebaseUser: FirebaseUser){
            val id:String=firebaseUser.uid
         viewModelScope.launch {
             repo.getUser(id).collect{ response->
                 when(response){
                     is Response.Success->{
                         val user:User=response.data
                         //emails don't match
                         //TODO SHOW CORRECT EXCEPTION
                         if (user.email!=firebaseUser.email){
                             _userValidation.value=Response.Failure(e = Exception())
                         }
                         //SET THE GLOBAL USER
                         UserData.user=user
                         Log.d("TAG","succes")
                         _userValidation.value=Response.Success(true)
                     }
                     is Response.Failure->{
                         //issue with retreiving user from database
                         _userValidation.value=Response.Failure(e = Exception())
                     }
                 }


             }
         }

    }

}