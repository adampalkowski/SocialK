package com.example.socialk.di

import com.example.socialk.ActiveUser
import com.example.socialk.model.Activity
import com.example.socialk.model.Response
import com.example.socialk.model.User
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@ExperimentalCoroutinesApi
class UserRepositoryImpl @Inject constructor(
    private val usersRef: CollectionReference,
):UserRepository {

    override suspend fun getUser(id: String): Flow<Response<User>> = callbackFlow {

           val registration= usersRef.document(id).addSnapshotListener  {  snapshot, exception ->

               if (exception != null) {
                   channel.close(exception)
                   return@addSnapshotListener
               }
               if (snapshot != null && snapshot.exists()) {
                   val user = snapshot.toObject(User::class.java)
                   if (user!=null){
                       trySend(Response.Success(user))
                   }
               } else {
                   trySend(Response.Failure(e=Exception()))
               }
            }

        awaitClose(){
            registration.remove()
        }


    }
    override suspend fun addUser(user: User): Flow<Response<Void?>> = flow {
        try {
            emit(Response.Loading)
            val userId=user.id
            val addition = usersRef.document(userId).set(user).await()
            emit(Response.Success(addition))

        }catch (e:Exception){
            emit(Response.Failure(Exception(e.message?:e.toString())))
        }
    }
    override suspend fun deleteUser(id: String): Flow<Response<Void?>> = flow {
        try{
            emit(Response.Loading)
            val deletion = usersRef.document(id).delete().await()
            emit(Response.Success(deletion))
        }catch (e:Exception){
            emit(Response.Failure(Exception(e.message?:e.toString())))
        }
    }
}