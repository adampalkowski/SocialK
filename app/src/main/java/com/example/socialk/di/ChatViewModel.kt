package com.example.socialk.di

import android.os.Message
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.socialk.ActiveUser
import com.example.socialk.model.Chat
import com.example.socialk.model.ChatMessage
import com.example.socialk.model.Response
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    val repo: ChatRepository
) : ViewModel() {

    private val _chatCollectionState = mutableStateOf<Response<Chat>>(Response.Loading)
    val chatCollectionState: State<Response<Chat>> = _chatCollectionState

    private val _addChatCollectionState = mutableStateOf<Response<Void?>>(Response.Loading)
    val addChatCollectionState: State<Response<Void?>> = _addChatCollectionState

    private val _deleteChatCollectionState = mutableStateOf<Response<Void?>>(Response.Loading)
    val deleteChatCollectionState: State<Response<Void?>> = _deleteChatCollectionState

    private val _updateChatCollectionRecentMessageState = mutableStateOf<Response<Void?>>(Response.Loading)
    val updateChatCollectionRecentMessageState: State<Response<Void?>> = _updateChatCollectionRecentMessageState

    private val _updateChatCollectionMembersState = mutableStateOf<Response<Void?>>(Response.Loading)
    val updateChatCollectionMembersState: State<Response<Void?>> = _updateChatCollectionMembersState

    private val _updateChatCollectionNameState = mutableStateOf<Response<Void?>>(Response.Loading)
    val updateChatCollectionNameState: State<Response<Void?>> = _updateChatCollectionNameState


    private val _messageState = mutableStateOf<Response<ChatMessage>>(Response.Loading)
    val messageState: State<Response<ChatMessage>> = _messageState
    private val _addMessageState = mutableStateOf<Response<Void?>>(Response.Loading)
    val addMessageState: State<Response<Void?>> = _addMessageState
    private val _deleteMessageState = mutableStateOf<Response<Void?>>(Response.Loading)
    val deleteMessageState: State<Response<Void?>> = _deleteMessageState

    fun getChatCollection(id: String) {
        viewModelScope.launch {
            repo.getChatCollection(id).collect{
                response->
                _chatCollectionState.value=response
            }
        }
    }
    fun addChatCollection(chatCollection: Chat) {
        viewModelScope.launch {
            val uuid: UUID = UUID.randomUUID()
            val id:String = uuid.toString()
            chatCollection.id=id
            repo.addChatCollection(chatCollection).collect{
                    response->
                _addChatCollectionState.value=response
            }
        }
    }
    fun deleteChatCollection(id: String) {
        viewModelScope.launch {
            repo.deleteChatCollection(id).collect{
                    response->
                _deleteChatCollectionState.value=response
            }
        }
    }

    fun updateChatCollectionRecentMessage(
        id: String,
        recentMessage: String
    ) {
        viewModelScope.launch {
            repo.updateChatCollectionRecentMessage(id,recentMessage).collect{
                    response->
                _updateChatCollectionRecentMessageState.value=response
            }
        }
    }

    fun updateChatCollectionMembers(
        members_list: List<String>,
        id: String
    ) {
        viewModelScope.launch {
            repo.updateChatCollectionMembers(members_list,id).collect{
                    response->
                _updateChatCollectionMembersState.value=response
            }
        }
    }

    fun updateChatCollectionName(
        chatCollectionName: String,
        id: String
    ) {
        viewModelScope.launch {
            repo.updateChatCollectionName(chatCollectionName,id).collect{
                    response->
                _updateChatCollectionNameState.value=response
            }
        }
    }

    fun getMessage(
        chat_collection_id: String,
        message_id: String
    ) {
        viewModelScope.launch {
            repo.getMessage(chat_collection_id,message_id).collect{
                    response->
                _messageState.value=response
            }
        }
    }

    //todo ::PAGINATION
    fun getMessages(id: String) {}
    fun addMessage(
        chat_collection_id: String,
        message: Chat
    ) {
        viewModelScope.launch {
            val uuid: UUID = UUID.randomUUID()
            val id:String = uuid.toString()
            message.id=id
            repo.addMessage(chat_collection_id,message).collect{
                    response->
                _addMessageState.value=response
            }
        }

    }

    fun deleteMessage(
        chat_collection_id: String,
        message_id: String
    ) {
        viewModelScope.launch {
            repo.deleteMessage(chat_collection_id,message_id).collect{
                    response->
                _deleteMessageState.value=response
            }
        }
    }
}