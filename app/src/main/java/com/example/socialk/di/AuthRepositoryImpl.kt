package com.example.socialk.di

import android.util.Log
import com.example.socialk.model.Response
import com.example.socialk.di.AuthRepository
import com.example.socialk.di.OneTapSignInResponse
import com.example.socialk.di.SignInWithGoogleResponse
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private var oneTapClient: SignInClient,
    @Named(SIGN_IN_REQUEST)
    private var signInRequest: BeginSignInRequest,
    @Named(SIGN_UP_REQUEST)
    private var signUpRequest: BeginSignInRequest,
    private val db: FirebaseFirestore
) : AuthRepository {
    override val isUserAuthenticatedInFirebase = auth.currentUser != null
    override val currentUser: FirebaseUser?
        get() = auth.currentUser

    override suspend fun login(email: String, password: String): Response<FirebaseUser> {
       return try {
            val result=auth.signInWithEmailAndPassword(email,password).await()
            Response.Success(result.user!!)
        }catch (e:Exception){
            e.printStackTrace()
            Response.Failure(e)
        }
    }

    override suspend fun signup(
        name: String,
        email: String,
        password: String
    ): Response<FirebaseUser> {
      return  try {
            val result=auth.createUserWithEmailAndPassword(email,password).await()
            result?.user?.updateProfile(UserProfileChangeRequest.Builder().setDisplayName(name).build())
            Response.Success(result.user!!)
        }catch (e:Exception){
            e.printStackTrace()
            Response.Failure(e)
        }

    }

    override fun logout() {
        auth.signOut()
    }

    override suspend fun oneTapSignInWithGoogle(): OneTapSignInResponse {
        return try {
            val signInResult = oneTapClient.beginSignIn(signInRequest).await()
            Response.Success(signInResult)

        } catch (e: Exception) {
            try {
                Log.d("looooo,",auth.toString())
                val signUpResult = oneTapClient.beginSignIn(signUpRequest).await()
                Log.d("looooo,","s")
                Response.Success(signUpResult)
            } catch (e: Exception) {
                Log.d("looooo,","exep"+e.toString())
                Response.Failure(e)
            }
        }
    }

    override suspend fun firebaseSignInWithGoogle(
        googleCredential: AuthCredential
    ): SignInWithGoogleResponse {
        return try {
            val authResult = auth.signInWithCredential(googleCredential).await()
            val isNewUser = authResult.additionalUserInfo?.isNewUser ?: false
            if (isNewUser) {
                addUserToFirestore()
            }
            Response.Success(true)
        } catch (e: Exception) {
            Response.Failure(e)
        }
    }

    private suspend fun addUserToFirestore() {
        auth.currentUser?.apply {
          //  val user = toUser()
           // db.collection(USERS).document(uid).set(user).await()
        }
    }
}

//fun FirebaseUser.toUser() = mapOf(
  //  DISPLAY_NAME to displayName,
   // EMAIL to email,
    //PHOTO_URL to photoUrl?.toString(),
   // CREATED_AT to FieldValue.serverTimestamp() )