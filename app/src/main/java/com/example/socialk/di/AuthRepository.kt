package com.example.socialk.di

import com.example.socialk.model.Response
import com.example.socialk.model.User
import com.google.android.gms.auth.api.identity.BeginSignInResult
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseUser
import io.grpc.internal.SharedResourceHolder.Resource

typealias OneTapSignInResponse = Response<BeginSignInResult>
typealias SignInWithGoogleResponse = Response<Boolean>

interface AuthRepository {
    val isUserAuthenticatedInFirebase : Boolean
    val currentUser: FirebaseUser?

    suspend fun signin(email:String, password :String):  Response<FirebaseUser>

    suspend fun signup(name:String,email:String,password: String): Response<FirebaseUser>

    fun logout()

    suspend fun oneTapSignInWithGoogle(): OneTapSignInResponse


    suspend fun firebaseSignInWithGoogle(googleCredential: AuthCredential): SignInWithGoogleResponse
}
