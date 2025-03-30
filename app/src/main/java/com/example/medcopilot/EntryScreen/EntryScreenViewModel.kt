package com.example.medcopilot.EntryScreen

import ChatListItem
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medcopilot.util.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.example.medcopilot.data.Chat

@HiltViewModel
class EntryScreenViewModel @Inject constructor(private val repository: com.example.medcopilot.backendRepository.ChatRepository, private val chatRepository: com.example.medcopilot.data.ChatRepository) : ViewModel(){

    val chats = chatRepository.getChats() // this is a flow

//    private val _uiEvent = Channel<UiEvent>()
//    val uiEvent = _uiEvent.receiveAsFlow()

    val chatMessages: StateFlow<ChatListItem>
        get() = repository.chatMessages

    fun onEvent(event: ChatGenEvent){
        when(event){
            is ChatGenEvent.OnAddChatGenClick ->{
                viewModelScope.launch{
                    chatRepository.insertChat(
                        Chat(
                            input = event.chatListItem.input,
                            output = event.chatListItem.answer
                        )
                    )
                }
            }
        }
    }

    fun getChatMessages(inputString: String){
        viewModelScope.launch {
            repository.getChatMessages(inputString)
        }
    }


}