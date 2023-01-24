package com.example.socialk.di

import com.example.socialk.ActiveUser
import com.example.socialk.model.Activity
import com.example.socialk.model.Response
import com.example.socialk.model.SocialException
import com.example.socialk.model.User
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.coroutineScope
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

    override suspend fun addInvitedIDs(
        my_id: String,
        invited_id: String
    ): Flow<Response<Void?>> =flow{
        try {
            emit(Response.Loading)
            val addition = usersRef.document(my_id).update("friends_ids",FieldValue.arrayUnion(invited_id)).await()
            emit(Response.Success(addition))

        }catch (e:Exception){
            emit(Response.Failure(e= SocialException("addInvitedIDs exception",Exception())))
        }
    }

    override suspend fun removeInvitedIDs(
        my_id: String,
        invited_id: String
    ): Flow<Response<Void?>> =flow{
        try {
            emit(Response.Loading)
            val deletion = usersRef.document(my_id).update("friends_ids",FieldValue.arrayRemove(invited_id)).await()
            emit(Response.Success(deletion))

        }catch (e:Exception){
            emit(Response.Failure(e= SocialException("removeInvitedIDs exception",Exception())))
        }
    }

    override suspend fun addBlockedIDs(
        my_id: String,
        blocked_id: String
    ): Flow<Response<Void?>> =flow{
        try {
            emit(Response.Loading)
            val addition = usersRef.document(my_id).update("blocked_ids",FieldValue.arrayUnion(blocked_id)).await()
            emit(Response.Success(addition))

        }catch (e:Exception){
            emit(Response.Failure(e= SocialException("addBlockedIDs exception",Exception())))
        }
    }

    override suspend fun removeBlockedIDs(
        my_id: String,
        blocked_id: String
    ): Flow<Response<Void?>>  =flow{
        try {
            emit(Response.Loading)
            val deletion = usersRef.document(my_id).update("blocked_ids",FieldValue.arrayRemove(blocked_id)).await()
            emit(Response.Success(deletion))

        }catch (e:Exception){
            emit(Response.Failure(e= SocialException("removeBlockedIDs exception",Exception())))
        }
    }



    override suspend fun addFriendsIDs(my_id: String, friend_id: String): Flow<Response<Void?>>  =flow{
        try {
            emit(Response.Loading)
            val addition = usersRef.document(my_id).update("friends_ids",FieldValue.arrayUnion(friend_id)).await()
            emit(Response.Success(addition))

        }catch (e:Exception){
            emit(Response.Failure(e= SocialException("addFriendsIDs exception",Exception())))
        }
    }
    override suspend fun removeFriendsIDs(my_id: String, friend_id: String): Flow<Response<Void?>>  =flow{
        try {
            emit(Response.Loading)
            val deletion = usersRef.document(my_id).update("friends_ids",FieldValue.arrayRemove(friend_id)).await()
            emit(Response.Success(deletion))

        }catch (e:Exception){
            emit(Response.Failure(e= SocialException("removeFriendsIDs exception",Exception())))
        }
    }

    override suspend fun addFriendToBothUsers(my_id: String, friend_id: String): Flow<Response<Void?>>  =flow{
        try {
            emit(Response.Loading)
              val one =  usersRef.document(my_id).update("friends_ids",FieldValue.arrayUnion(friend_id)).await()
              val two=  usersRef.document(friend_id).update("friends_ids",FieldValue.arrayUnion(my_id)).await()
              emit(Response.Success(two))

        }catch (e:Exception){
            emit(Response.Failure(e= SocialException("addFriendToBothUsers exception",Exception())))
        }
    }


    override suspend fun removeFriendFromBothUsers(my_id: String, friend_id: String): Flow<Response<Void?>>  =flow{
        try {
            emit(Response.Loading)
            val one =  usersRef.document(my_id).update("friends_ids",FieldValue.arrayRemove(friend_id)).await()
            val two=  usersRef.document(friend_id).update("friends_ids",FieldValue.arrayRemove(my_id)).await()
            emit(Response.Success(two))

        }catch (e:Exception){
            emit(Response.Failure(e= SocialException("removeFriendFromBothUsers exception",Exception())))
        }
    }
}