package com.example.socialk.di

import com.example.socialk.model.Activity
import com.example.socialk.model.Chat
import com.example.socialk.model.ChatMessage
import com.example.socialk.model.Response
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    //handle groups REPO
    suspend fun getChatCollection(id: String): Flow<Response<Chat>>
    suspend fun addChatCollection(chatCollection: Chat): Flow<Response<Void?>>
    suspend fun deleteChatCollection(id: String): Flow<Response<Void?>>
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

    // handle  CHATS REPO
    suspend fun getMessage(
        chat_collection_id: String,
        message_id: String
    ): Flow<Response<ChatMessage>>

    suspend fun getMessages(chat_collection_id: String): Flow<Response<List<ChatMessage>>>
    suspend fun addMessage(chat_collection_id: String, message: Chat): Flow<Response<Void?>>
    suspend fun deleteMessage(chat_collection_id: String,message_id: String): Flow<Response<Void?>>
}