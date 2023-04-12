package com.example.socialk.di

import android.net.Uri
import com.example.socialk.model.Chat
import com.example.socialk.model.ChatMessage
import com.example.socialk.model.Response
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    //handle groups REPO
    suspend fun getChatCollection(id: String): Flow<Response<Chat>>
    suspend fun addChatCollection(chatCollection: Chat): Flow<Response<Void?>>
    suspend fun deleteChatCollection(id: String): Flow<Response<Void?>>
    suspend fun addGroupHighlight(group_id:String,text_message:String): Flow<Response<Void?>>
    suspend fun removeGroupHighlight(group_id:String): Flow<Response<Void?>>
    suspend fun addImageFromGalleryToStorage(id:String,imageUri: Uri): Flow<Response<String>>
    suspend fun addLoweResImageFromGalleryToStorage(id:String,imageUri: Uri): Flow<Response<String>>
    suspend fun updateChatCollectionRecentMessage(
        id: String,
        recentMessage: String
    ): Flow<Response<Void?>>

    suspend fun updateChatCollectionMembers(
        members_list: List<String>,
        id: String
    ): Flow<Response<Void?>>

    suspend fun updateChatCollectionName(
        chatCollectionName: String,
        id: String
    ): Flow<Response<Void?>>
    suspend fun updateChatCollectionRecentMessage(
        id: String,recent_message_time:String,recent_message:String
    ): Flow<Response<Void?>>

    // handle  CHATS REPO
    suspend fun getMessage(
        chat_collection_id: String,
        message_id: String
    ): Flow<Response<ChatMessage>>

    suspend fun getMessages(chat_collection_id: String,current_time:String): Flow<Response<ArrayList<ChatMessage>>>
    suspend fun getMoreMessages(chat_collection_id: String): Flow<Response<ArrayList<ChatMessage>>>
    suspend fun getFirstMessages(chat_collection_id: String,current_time:String): Flow<Response<ArrayList<ChatMessage>>>
    suspend fun getGroups(id: String): Flow<Response<ArrayList<Chat>>>
    suspend fun getChatCollections(user_id: String): Flow<Response<ArrayList<Chat>>>
    suspend fun addMessage(chat_collection_id: String, message: ChatMessage): Flow<Response<Void?>>
    suspend fun deleteMessage(chat_collection_id: String,message_id: String): Flow<Response<Void?>>
}