package com.example.socialk.di

import com.example.socialk.ActiveUser
import com.example.socialk.model.Activity
import com.example.socialk.model.Response
import com.example.socialk.model.SocialException
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
                   trySend(Response.Failure(e= SocialException("get user snaphot doesn't exist",Exception())))

               }
            }

        awaitClose(){
            registration.remove()
        }

    }
    override suspend fun getUserByUsername(username: String): Flow<Response<User>> = callbackFlow {
        val registration= usersRef.whereEqualTo("username",username).get().addOnSuccessListener  { documents->
            var userList:List<User> = mutableListOf()

            //list should always be the size of 1
            if (userList.size>1){
                trySend(Response.Failure(e= SocialException("more than one of the usernames exist",Exception())))
            }

            val response = if (documents != null) {

                userList =documents.map { it.toObject<User>() }
                if (userList.size==1){
                    trySend(Response.Success(userList[0]))
                }else{
                    trySend(Response.Failure(e= SocialException("user found and is not 1",Exception())))
                }
            } else {
                trySend(Response.Failure(e= SocialException("getUser by name document null",Exception())))
            }

        }.addOnFailureListener{
                exception ->
            channel.close(exception)
            trySend(Response.Failure(e= SocialException("get user by name document doesnt exist",Exception())))
        }

        awaitClose(){
        }

    }

    override suspend fun addUser(user: User): Flow<Response<Void?>> = flow {
        try {
            emit(Response.Loading)
            val userId=user.id
            val addition = usersRef.document(userId).set(user).await()
            emit(Response.Success(addition))

        }catch (e:Exception){
            emit(Response.Failure(e= SocialException("addUser exception",Exception())))
        }
    }
    override suspend fun deleteUser(id: String): Flow<Response<Void?>> = flow {
        try{
            emit(Response.Loading)
            val deletion = usersRef.document(id).delete().await()
            emit(Response.Success(deletion))
        }catch (e:Exception){
            emit(Response.Failure(e= SocialException("deleteUser exception",Exception())))
        }
    }

    override suspend fun addUsernameToUser(id:String,username: String): Flow<Response<Void?>>  = flow {
        try {
            emit(Response.Loading)


            val addition = usersRef.document(id).update("username",username).await()
            emit(Response.Success(addition))

        }catch (e:Exception){
            emit(Response.Failure(e= SocialException("addUsernameToUser exception",Exception())))
        }
    }
}