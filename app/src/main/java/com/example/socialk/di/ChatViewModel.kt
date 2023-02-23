package com.example.socialk.di

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.socialk.model.Chat
import com.example.socialk.model.ChatMessage
import com.example.socialk.model.Response
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
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

    private val _uri = MutableLiveData<Uri>()
    val uri: LiveData<Uri> = _uri
    private val _uriReceived = mutableStateOf(false)
    val uriReceived: State<Boolean> = _uriReceived
    private val _chatCollectionsState = mutableStateOf<Response<ArrayList<Chat>>>(Response.Loading)
    val chatCollectionsState: State<Response<ArrayList<Chat>>> = _chatCollectionsState


    private val _messagesState = mutableStateOf<Response<ArrayList<ChatMessage>>>(Response.Loading)
    val messagesState: State<Response<ArrayList<ChatMessage>>> = _messagesState
    private val _firstMessagesState = mutableStateOf<Response<ArrayList<ChatMessage>>>(Response.Loading)
    val firstMessagesState: State<Response<ArrayList<ChatMessage>>> = _firstMessagesState
    private val _moreMessagesState = mutableStateOf<Response<ArrayList<ChatMessage>>>(Response.Loading)
    val moreMessagesState: State<Response<ArrayList<ChatMessage>>> = _moreMessagesState
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

    private val _isImageAddedToStorageState = MutableStateFlow<Response<String>?>(null)
    val isImageAddedToStorageFlow: StateFlow<Response<String>?> = _isImageAddedToStorageState

    private val _deleteMessageState = mutableStateOf<Response<Void?>>(Response.Success(null))
    val deleteMessageState: State<Response<Void?>> = _deleteMessageState

    private val _checkIfChatExistsState = mutableStateOf<Response<Chat>>(Response.Loading)
    val checkIfChatExistsState: State<Response<Chat>> = _checkIfChatExistsState

    private val _highlightAddedState = mutableStateOf<Response<Void?>>(Response.Success(null))
    val highlightAddedState: State<Response<Void?>> = _highlightAddedState

    private val _highlightRemovedState = mutableStateOf<Response<Void?>>(Response.Success(null))
    val highlightRemovedState: State<Response<Void?>> = _highlightRemovedState
    fun getChatCollections(id:String){
        viewModelScope.launch {
            repo.getChatCollections(id).collect{
                    response->
                _chatCollectionsState.value=response
            }
        }
    }
    fun onUriReceived(uri: Uri) {
        _uri.value = uri
        _uriReceived.value = true
    }

    fun onUriProcessed() {
        _uriReceived.value = false
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


    fun getMessages(id: String,current_time:String) {
        viewModelScope.launch {
            repo.getMessages(id,current_time).collect{response->
                when(response){
                    is Response.Success->{
                        _messagesState.value=response

                    }
                }

            }

        }
    }
    fun getFirstMessages(id: String,current_time:String) {
        viewModelScope.launch {
            repo.getFirstMessages(id,current_time).collect{response->
                when(response){
                    is Response.Success->{
                        _firstMessagesState.value=response

                    }
                }

            }

        }
    }

    fun getMoreMessages(id: String){
        viewModelScope.launch {
            repo.getMoreMessages(id).collect{response->
                when(response){
                    is Response.Success->{
                        Log.d("ChatViewModel","getMoreMessages response"+response.data.size.toString())
                        _moreMessagesState.value=response

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
    fun addImageToStorage(id:String,picture_uri: Uri){
        viewModelScope.launch {
            repo.addImageFromGalleryToStorage(id, picture_uri).collect{ response ->
                _isImageAddedToStorageState.value=response
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

    fun addHighLight(group_id:String,highlitedMessageText: String) {
        viewModelScope.launch {
            repo.addGroupHighlight(group_id,highlitedMessageText).collect{
                    response->
                _highlightAddedState.value=response
            }
        }
    }
    fun removeHighLight(group_id:String,highlitedMessageText: String) {
        viewModelScope.launch {
            repo.removeGroupHighlight(group_id).collect{
                    response->
                _highlightRemovedState.value=response
            }
        }
    }

    fun sendImage(chat_id: String,message:ChatMessage, uri: Uri) {

        val uuid: UUID = UUID.randomUUID()
        val id:String = uuid.toString()
        message.id=id
        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val formatted = current.format(formatter)
        message.sent_time=formatted
        viewModelScope.launch {
            repo.addImageFromGalleryToStorage(id, uri).collect{ response ->
                _isImageAddedToStorageState.value=response
                when(response){
                    is Response.Success->{
                        val new_url:String=response.data
                        message.text=new_url
                        Log.d("ImagePicker","url add message"+message.text)
                        repo.addMessage(chat_id,message).collect{
                                response->
                            _addMessageState.value=response
                        }
                        repo.updateChatCollectionRecentMessage(chat_id, recent_message = "image sent", recent_message_time = message.sent_time).collect{
                                response->
                            _addMessageState.value=response
                        }
                    }
                    is Response.Loading->{

                    }
                    is Response.Failure->{

                    }
                }

            }



        }
    }

}