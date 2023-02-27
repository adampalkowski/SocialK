package com.example.socialk.di

import android.net.Uri
import com.example.socialk.model.*
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

interface UserRepository {
    suspend fun getUser(id:String):Flow<Response<User>>
    suspend fun getUserListener(id:String):Flow<Response<User>>
    suspend fun getUserByUsername(username:String):Flow<Response<User>>
    suspend fun addUser(user: User): Flow<Response<Void?>>
    suspend fun deleteUser(id:String): Flow<Response<Void?>>

    suspend fun updateUser(id: String,firstAndLastName:String,description:String): Flow<Response<Void?>>
    suspend fun addUsernameToUser(id:String,username:String): Flow<Response<Void?>>
    suspend fun changeUserProfilePicture(user_id:String,picture_url:String): Flow<Response<Void?>>
    suspend fun addProfilePictureToStorage(user_id:String,imageUri: Uri): Flow<Response<String>>
    suspend fun addImageFromGalleryToStorage(id:String,imageUri: Uri): Flow<Response<String>>
    suspend fun deleteProfilePictureFromStorage(user_id:String,picture_url:String): Flow<Response<Void?>>
    suspend fun getProfilePictureFromStorage(user_id:String,picture_url:String): Flow<Response<String>>
    suspend fun getFriends(id:String): Flow<Response<ArrayList<User>>>
    suspend fun getMoreFriends(id:String): Flow<Response<ArrayList<User>>>


    suspend fun addInvitedIDs(
        my_id: String,
        invited_id: String
    ): Flow<Response<Void?>>
    suspend fun acceptInvite(
        current_user:User,user:User, chat:Chat
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