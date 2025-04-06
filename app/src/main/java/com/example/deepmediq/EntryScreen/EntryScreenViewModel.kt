package com.example.deepmediq.EntryScreen

import ChatListItem
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.example.deepmediq.data.Chat
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@HiltViewModel
class EntryScreenViewModel @Inject constructor(private val repository: com.example.deepmediq.backendRepository.ChatRepository, private val chatRepository: com.example.deepmediq.data.ChatRepository) : ViewModel(){

    val chats = chatRepository.getChats() // this is a flow

//    private val _uiEvent = Channel<UiEvent>()
//    val uiEvent = _uiEvent.receiveAsFlow()

    val chatMessages: StateFlow<ChatListItem>
        get() = repository.chatMessages

    private val _selectedItem = MutableStateFlow<Int?>(0)
    val selectedItem: StateFlow<Int?> = _selectedItem.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _firstLoad = MutableStateFlow(false)
    val firstLoad: StateFlow<Boolean> = _firstLoad

    fun setFirstLoadTrue(){
        _firstLoad.value = true
    }
    fun invertFirstLoad(){
        _firstLoad.value = if(_firstLoad.value) false else true
    }

    fun onItemClick(id: Int) {
        setFirstLoadTrue()
        _selectedItem.value = id
        Log.d("onItemClick","yes clicked $id")
    }


    fun onEvent(event: ChatGenEvent){
        when(event){
            is ChatGenEvent.OnAddChatGenClick ->{
                viewModelScope.launch{
                    chatRepository.insertChat(
                        Chat(
                            input = event.chatListItem.input,
                            output = event.chatListItem.answer,
                             timeStamp = SimpleDateFormat("M/d/yyyy, h:mm:ss a", Locale.getDefault()).format(Date())
                        )
                    )
                }
            }
        }
    }

    fun getChatMessages(inputString: String){
        viewModelScope.launch {
            _isLoading.value = true
            repository.getChatMessages(inputString)
            _isLoading.value = false

        }
    }


}