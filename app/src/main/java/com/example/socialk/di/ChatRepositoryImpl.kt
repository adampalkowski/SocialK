package com.example.socialk.di

import android.os.Message
import com.example.socialk.await
import com.example.socialk.model.*
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
@ExperimentalCoroutinesApi
class ChatRepositoryImpl @Inject constructor(
    private val messagesRef: CollectionReference,
    private val chatCollectionsRef: CollectionReference,
):ChatRepository{
    override suspend fun getChatCollection(id: String): Flow<Response<Chat>>  = callbackFlow {
        chatCollectionsRef.document(id).get().addOnSuccessListener { documentSnapshot->
            val response = if (documentSnapshot != null) {
                val activity = documentSnapshot.toObject<Chat>()
                Response.Success(activity)
            } else {
                Response.Failure(e= SocialException("getChatCollection document null",Exception()))
            }
            trySend(response as Response<Chat>).isSuccess
        }
    }

    override suspend fun addChatCollection(chatCollection: Chat): Flow<Response<Void?>> =flow{
        try {
            emit(Response.Loading)
            val chatCollectionId=chatCollection.id
            val addition = chatCollectionsRef.document(chatCollectionId).set(chatCollection).await()
            emit(Response.Success(addition))
        }catch (e:Exception){
            emit(Response.Failure(e= SocialException("addChatCollection exception",Exception())))
        }
    }

    override suspend fun deleteChatCollection(id: String): Flow<Response<Void?>> =flow {
        try {
            emit(Response.Loading)
            val deletion = chatCollectionsRef.document(id).delete().await()
            emit(Response.Success(deletion))
        }catch (e:Exception){
            emit(Response.Failure(e= SocialException("deleteChatCollection exception",Exception())))
        }
    }

    override suspend fun updateChatCollectionRecentMessage(
        id: String,
        recentMessage: String
    ): Flow<Response<Void?>>  =flow{
        try {
            emit(Response.Loading)
            val update = chatCollectionsRef.document(id).update("recentMessage",recentMessage).await()
            emit(Response.Success(update))
        }catch (e:Exception){
            emit(Response.Failure(e= SocialException("updateChatCollectionRecentMessage exception",Exception())))
        }
    }

    override suspend fun updateChatCollectionMembers(
        members_list: List<String>,
        id: String
    ): Flow<Response<Void?>>  =flow{
        try {
            emit(Response.Loading)
            val update = chatCollectionsRef.document(id).update("members",members_list).await()
            emit(Response.Success(update))
        }catch (e:Exception){
            emit(Response.Failure(e= SocialException("updateChatCollectionMembers exception",Exception())))
        }
    }

    override suspend fun updateChatCollectionName(
        chatCollectionName: String,
        id: String
    ): Flow<Response<Void?>>  =flow{
        try {
            emit(Response.Loading)
            val update = chatCollectionsRef.document(id).update("name",chatCollectionName).await()
            emit(Response.Success(update))
        }catch (e:Exception){
            emit(Response.Failure(e= SocialException("updateChatCollectionName exception",Exception())))
        }
    }

    override suspend fun getMessage(
        chat_collection_id: String,
        message_id: String
    ): Flow<Response<ChatMessage>> = callbackFlow {
        messagesRef.document(chat_collection_id).collection("messages").document(message_id)
            .get().addOnSuccessListener { documentSnapshot->
            val response = if (documentSnapshot != null) {
                val activity = documentSnapshot.toObject<ChatMessage>()
                Response.Success(activity)
            } else {
                Response.Failure(e= SocialException("getMessage document null",Exception()))
            }
            trySend(response as Response<ChatMessage>).isSuccess
        }
    }


    override suspend fun getMessages(chat_collection_id: String): Flow<Response<List<ChatMessage>>> {
        TODO("PAGINATIONNN HEREEEE")
    }

    override suspend fun addMessage(
        chat_collection_id: String,
        message: Chat
    ): Flow<Response<Void?>> =flow{
        try {
            emit(Response.Loading)
            val addition = messagesRef.document(chat_collection_id).collection("messages")
                .document(message.id).set(message).await()
            emit(Response.Success(addition))
        }catch (e:Exception){
            emit(Response.Failure(e= SocialException("addMessage exception",Exception())))
        }
    }

    override suspend fun deleteMessage(
        chat_collection_id: String,
        message_id: String
    ): Flow<Response<Void?>> =flow{
        try {
            emit(Response.Loading)
            val deletion = chatCollectionsRef.document(chat_collection_id).collection("messages")
                .document(message_id).delete().await()
            emit(Response.Success(deletion))
        }catch (e:Exception){
            emit(Response.Failure(e= SocialException("deleteMessage exception",Exception())))
        }
    }

}