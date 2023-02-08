package com.example.socialk.signinsignup

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.socialk.Main.Screen
import com.example.socialk.di.AuthRepository
import com.example.socialk.di.OneTapSignInResponse
import com.example.socialk.di.SignInWithGoogleResponse
import com.example.socialk.di.UserViewModel
import com.example.socialk.model.Response
import com.example.socialk.model.User
import com.example.socialk.model.UserData
import com.example.socialk.util.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repo : AuthRepository,
    val oneTapClient: SignInClient
): ViewModel() {

    private val _navigateTo = MutableLiveData<Event<Screen>>()
    val navigateTo: LiveData<Event<Screen>> = _navigateTo

    val currentUser :FirebaseUser?
        get()= repo.currentUser

    val isUserAuthenticated get() =repo.isUserAuthenticatedInFirebase

   // var oneTapSignInResponse by mutableStateOf<OneTapSignInResponse>(Response.Success(null))
    //    private set
   // var signInWithGoogleResponse by mutableStateOf<SignInWithGoogleResponse>(Response.Success(false))
            //private set
    private val _loginFlow= MutableStateFlow<Response<FirebaseUser>?>(null)
    val loginFlow: StateFlow<Response<FirebaseUser>?> = _loginFlow

    private val _signupFlow= MutableStateFlow<Response<FirebaseUser>?>(null)
    val signupFlow: StateFlow<Response<FirebaseUser>?> = _signupFlow


    fun signin(email:String,password:String)=viewModelScope.launch {
        _loginFlow.value=Response.Loading
        val result=repo.signin(email, password )
        _loginFlow.value=result
    }
    fun signup(name:String,email:String,password:String)=viewModelScope.launch {
        _signupFlow.value=Response.Loading
        val result=repo.signup(name=name, email = email,password= password )
        _signupFlow.value=result
    }

    fun logout(){
        repo.logout()
        _loginFlow.value=null
        _signupFlow.value=null
        UserData.user=null
    }

    fun oneTapSignIn() = viewModelScope.launch {
        //oneTapSignInResponse = Response.Loading
    //    oneTapSignInResponse = repo.oneTapSignInWithGoogle()


    }
    fun signInWithGoogle(googleCredential: AuthCredential) = viewModelScope.launch {
      //  oneTapSignInResponse = Response.Loading
      //  signInWithGoogleResponse = repo.firebaseSignInWithGoogle(googleCredential)
    }
}
