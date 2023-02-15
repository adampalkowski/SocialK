package com.example.socialk.di

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.socialk.model.Chat
import com.example.socialk.model.ChatMessage
import com.example.socialk.model.Response
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

@HiltViewModel
class ChatViewModel @Inject constructor(
    val repo: ChatRepository
) : ViewModel() {
    private var alreadyRead:Boolean= false

    val _chatCollectionStateFlow = MutableStateFlow<Chat>(Chat())

    private val _chatCollectionState = mutableStateOf<Response<Chat>>(Response.Loading)
    val chatCollectionState: State<Response<Chat>> = _chatCollectionState

    private val _chatCollectionsState = mutableStateOf<Response<ArrayList<Chat>>>(Response.Loading)
    val chatCollectionsState: State<Response<ArrayList<Chat>>> = _chatCollectionsState

    private val _messagesState = mutableStateOf<Response<ArrayList<ChatMessage>>>(Response.Loading)
    val messagesState: State<Response<ArrayList<ChatMessage>>> = _messagesState
    private val _addedMessagesState = mutableStateOf<Response<ArrayList<ChatMessage>>>(Response.Loading)
    val addedMessagesState: State<Response<ArrayList<ChatMessage>>> = _addedMessagesState

    private val _addChatCollectionState = mutableStateOf<Response<Void?>>(Response.Success(null))
    val addChatCollectionState: State<Response<Void?>> = _addChatCollectionState

    private val _deleteChatCollectionState = mutableStateOf<Response<Void?>>(Response.Success(null))
    val deleteChatCollectionState: State<Response<Void?>> = _deleteChatCollectionState

    private val _updateChatCollectionRecentMessageState = mutableStateOf<Response<Void?>>(Response.Success(null))
    val updateChatCollectionRecentMessageState: State<Response<Void?>> = _updateChatCollectionRecentMessageState

    private val _updateChatCollectionMembersState = mutableStateOf<Response<Void?>>(Response.Success(null))
    val updateChatCollectionMembersState: State<Response<Void?>> = _updateChatCollectionMembersState

    private val _updateChatCollectionNameState = mutableStateOf<Response<Void?>>(Response.Success(null))
    val updateChatCollectionNameState: State<Response<Void?>> = _updateChatCollectionNameState

    private val _messageState = mutableStateOf<Response<ChatMessage>>(Response.Loading)
    val messageState: State<Response<ChatMessage>> = _messageState

    private val _addMessageState = mutableStateOf<Response<Void?>>(Response.Success(null))
    val addMessageState: State<Response<Void?>> = _addMessageState

    private val _deleteMessageState = mutableStateOf<Response<Void?>>(Response.Success(null))
    val deleteMessageState: State<Response<Void?>> = _deleteMessageState

    private val _checkIfChatExistsState = mutableStateOf<Response<Chat>>(Response.Loading)
    val checkIfChatExistsState: State<Response<Chat>> = _checkIfChatExistsState


    fun getChatCollections(id:String){
        viewModelScope.launch {
            repo.getChatCollections(id).collect{
                    response->
                _chatCollectionsState.value=response
            }
        }
    }

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
            if (chatCollection.id!!.isEmpty()||chatCollection.id==null){
                chatCollection.id=id
            }

            val current = LocalDateTime.now()
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            val formatted = current.format(formatter)
            chatCollection.create_date=formatted
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


    fun getMessages(id: String) {
        viewModelScope.launch {
            repo.getMessages(id).collect{response->
                when(response){
                    is Response.Success->{
                        Log.d("ChatViewModel","viewmodel response"+response.data.toString())

                        _messagesState.value=response
                       /* if(alreadyRead){
                            Log.d("TAGGG","Already read")

                            _addedMessagesState.value=response
                            _messagesState.value=Response.Loading
                        }else{

                            alreadyRead = true
                        }*/
                    }
                }

            }

        }


    }
    fun addMessage(
        chat_collection_id: String,
        message: ChatMessage
    ) {
        val uuid: UUID = UUID.randomUUID()
        val id:String = uuid.toString()
        message.id=id

        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val formatted = current.format(formatter)
        message.sent_time=formatted
        viewModelScope.launch {
            repo.addMessage(chat_collection_id,message).collect{
                    response->
                _addMessageState.value=response
            }

            repo.updateChatCollectionRecentMessage(chat_collection_id, recent_message = message.text, recent_message_time = message.sent_time).collect{
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