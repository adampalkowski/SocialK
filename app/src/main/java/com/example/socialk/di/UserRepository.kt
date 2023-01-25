package com.example.socialk.di

import com.example.socialk.model.*
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

interface UserRepository {
    suspend fun getUser(id:String):Flow<Response<User>>
    suspend fun getUserByUsername(username:String):Flow<Response<User>>
    suspend fun addUser(user: User): Flow<Response<Void?>>
    suspend fun deleteUser(id:String): Flow<Response<Void?>>
    suspend fun addUsernameToUser(id:String,username:String): Flow<Response<Void?>>

    suspend fun addInvitedIDs(
        my_id: String,
        invited_id: String
    ): Flow<Response<Void?>>

    suspend fun addBlockedIDs(
        my_id: String,
        blocked_id: String
    ): Flow<Response<Void?>>

    suspend fun addFriendsIDs(
        my_id: String,
        friend_id: String
    ): Flow<Response<Void?>>
    suspend fun removeInvitedIDs(
        my_id: String,
        invited_id: String
    ): Flow<Response<Void?>>

    suspend fun removeBlockedIDs(
        my_id: String,
        blocked_id: String
    ): Flow<Response<Void?>>

    suspend fun removeFriendsIDs(
        my_id: String,
        friend_id: String
    ): Flow<Response<Void?>>
    suspend fun addFriendToBothUsers(
        my_id: String,
        friend_id: String
    ): Flow<Response<Void?>>
    suspend fun removeFriendFromBothUsers(
        my_id: String,
        friend_id: String
    ): Flow<Response<Void?>>
    suspend fun addChatCollectionToUsers(
        id: String,
        friend_id: String,
        chat_id:String
    ): Flow<Response<Void?>>
    suspend fun getInvites(
        id: String,
    ): Flow<Response<ArrayList<User>>>
    suspend fun checkIfChatCollectionExists(id: String,chatter_id: String): Flow<Response<User>>

}