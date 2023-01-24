package com.example.socialk.di

import com.example.socialk.model.Response
import com.example.socialk.model.User
import kotlinx.coroutines.flow.Flow

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
    suspend fun getInvites(
        id: String,
    ): Flow<Response<ArrayList<User>>>

}