package com.example.socialk.di

import android.net.Uri
import android.util.Log
import com.example.socialk.await1
import com.example.socialk.model.*
import com.google.firebase.firestore.*
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.storage.StorageException
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import java.util.concurrent.TimeoutException
import javax.inject.Inject
import javax.inject.Singleton


@Suppress("IMPLICIT_CAST_TO_ANY")
@Singleton
@ExperimentalCoroutinesApi
class ChatRepositoryImpl @Inject constructor(
    private val messagesRef: CollectionReference,
    private val chatCollectionsRef: CollectionReference,
    private val resStorage: StorageReference,
    ):ChatRepository{
    private var lastVisibleData: DocumentSnapshot? = null
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
        awaitClose(){

        }
    }



    suspend fun keepTrying(triesRemaining: Int, storageRef: StorageReference): String {
        if (triesRemaining < 0) {
            throw TimeoutException("out of tries")
        }

        return try {
            val url = storageRef.downloadUrl.await()
            url.toString()
        } catch (error: Exception) {
            when (error) {
                is StorageException -> {
                    if (error.errorCode ==StorageException.ERROR_OBJECT_NOT_FOUND) {
                        delay(1000)
                        keepTrying(triesRemaining - 1, storageRef)
                    } else {
                        println(error)
                        throw error
                    }
                }
                else -> {
                    println(error)
                    throw error
                }
            }
        }
    }
    override suspend fun addImageFromGalleryToStorage(
        id: String,
        imageUri: Uri
    ): Flow<Response<String>>  = flow {
        try {
            emit(Response.Loading)
            if (imageUri != null) {
                val fileName = id
                val imageRef = resStorage.child("images/$fileName")
                imageRef.putFile(imageUri).await1()
                val reference= resStorage.child("images/$fileName" + "_600x600")
                val url =keepTrying(5,reference)
                emit(Response.Success(url))
            }
        } catch (e: Exception) {
            Log.d("ImagePicker", "try addProfilePictureToStorage EXCEPTION")
            emit(
                Response.Failure(
                    e = SocialException(
                        "addProfilePictureToStorage exception",
                        Exception()
                    )
                )
            )
        }
    }

    override suspend fun addChatCollection(chatCollection: Chat): Flow<Response<Void?>>  = flow {
        try {
            emit(Response.Loading)
            val chatCollectionId=chatCollection.id
            val addition = chatCollectionsRef.document(chatCollectionId!!).set(chatCollection).await()
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

    override suspend fun addGroupHighlight(
        group_id: String,
        text_message: String
    ): Flow<Response<Void?>> =flow {
        try {
            emit(Response.Loading)
            val deletion = chatCollectionsRef.document(group_id).update("highlited_message",text_message).await()
            emit(Response.Success(deletion))
        }catch (e:Exception){
            emit(Response.Failure(e= SocialException("addGroupHighlight exception",Exception())))
        }
    }
    override suspend fun removeGroupHighlight(group_id: String): Flow<Response<Void?>> =flow {
        try {
            emit(Response.Loading)
            val deletion = chatCollectionsRef.document(group_id).update("highlited_message",FieldValue.delete()).await()
            emit(Response.Success(deletion))
        }catch (e:Exception){
            emit(Response.Failure(e= SocialException("removeGroupHighlight exception",Exception())))
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

    override suspend fun updateChatCollectionRecentMessage(
        id: String,
        recent_message_time: String,
        recent_message: String
    ): Flow<Response<Void?>>  =flow{
        try {
            emit(Response.Loading)
            val update = chatCollectionsRef.document(id).update("recent_message",recent_message,
                "recent_message_time",recent_message_time).await()
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

    //todo ::PAGINATION
    override suspend fun getMessages(chat_collection_id: String): Flow<Response<ArrayList<ChatMessage>>> = callbackFlow {

        var messages : ArrayList<ChatMessage> = ArrayList()

        lastVisibleData=null
        Log.d("ChatRepository","getmessages called")
        val registration=   messagesRef.document(chat_collection_id).collection("messages")
            .orderBy("sent_time",Query.Direction.DESCENDING).limit(15)
            .addSnapshotListener{  snapshots , exception ->
            if (exception != null) {
                channel.close(exception)
                return@addSnapshotListener
            }
            var new_messages =  ArrayList<ChatMessage>()
            new_messages.addAll(messages)
            if(snapshots==null ||snapshots.isEmpty()) {
                lastVisibleData = null
            } else {
                lastVisibleData = snapshots.getDocuments()
                    .get(snapshots.size() - 1)
            }
            for (dc in snapshots!!.documentChanges) {
                when (dc.type) {
                    DocumentChange.Type.ADDED -> {
                        val message = dc.document.toObject(ChatMessage::class.java)
                        if(messages.isEmpty()){
                            new_messages.add(message)

                        }else{         new_messages.reverse()
                            new_messages.add(message)
                            new_messages.reverse()

                        }

                        Log.d("TAGGG", messages.toString())
                    }
                    DocumentChange.Type.MODIFIED -> {
                        val message = dc.document.toObject(ChatMessage::class.java)
                        new_messages.add(message)
                    }
                    DocumentChange.Type.REMOVED -> {
                        val message = dc.document.toObject(ChatMessage::class.java)
                        new_messages.remove(message)
                        messages.remove(message)
                    }
                }

            }
            messages.clear()
            messages.addAll(new_messages)
            trySend(Response.Success(new_messages))

        }
        awaitClose(){
         registration.remove()
        }

    }

    override suspend fun getChatCollections(user_id: String): Flow<Response<ArrayList<Chat>>>  = callbackFlow {

        var messages : ArrayList<Chat> = ArrayList()

        lastVisibleData=null
        val registration=   chatCollectionsRef.whereArrayContains("members",user_id)
            .orderBy("recent_message_time",Query.Direction.DESCENDING).limit(10)
            .addSnapshotListener{  snapshots , exception ->
                if (exception != null) {
                    channel.close(exception)
                    return@addSnapshotListener
                }
                var new_messages =  ArrayList<Chat>()
                new_messages.addAll(messages)
                if(snapshots==null ||snapshots.isEmpty()) {
                    lastVisibleData = null
                } else {
                    lastVisibleData = snapshots.getDocuments()
                        .get(snapshots.size() - 1)
                }
                for (dc in snapshots!!.documentChanges) {
                    when (dc.type) {
                        DocumentChange.Type.ADDED -> {
                            val message = dc.document.toObject(Chat::class.java)
                            new_messages.reverse()
                            new_messages.add(message)
                            new_messages.reverse()
                        }
                        DocumentChange.Type.MODIFIED -> {
                            val message = dc.document.toObject(Chat::class.java)
                            new_messages.add(message)
                        }
                        DocumentChange.Type.REMOVED -> {
                            val message = dc.document.toObject(Chat::class.java)
                            new_messages.remove(message)
                            messages.remove(message)
                        }
                    }

                }
                messages.clear()
                messages.addAll(new_messages)
                trySend(Response.Success(new_messages))

            }
        awaitClose(){
            registration.remove()
        }
    }


    override suspend fun addMessage(
        chat_collection_id: String,
        message: ChatMessage
    ): Flow<Response<Void?>> =flow{
        try {
            emit(Response.Loading)
            val addition = messagesRef.document(chat_collection_id).collection("messages")
                .document(message.id!!).set(message).await()
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