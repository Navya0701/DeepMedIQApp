package com.plcoding.bookpedia.book.presentation.chat_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.plcoding.bookpedia.book.domain.Chat
import com.plcoding.bookpedia.book.domain.ChatRepository
import com.plcoding.bookpedia.core.domain.onError
import com.plcoding.bookpedia.core.domain.onSuccess
import kotlin.uuid.ExperimentalUuidApi

class ChatScreenViewModel(
    private val chatRepository: ChatRepository
): ViewModel() {

    private val cachedChats = emptyList<Chat>()


    private val _state = MutableStateFlow(ChatScreenState())
    val state = _state

    init {
        // Fetch chats when ViewModel is created
        viewModelScope.launch {
            _state.update { currentState ->
                currentState.copy(isLoading = true)
            }
            chatRepository.getLatestChats().collect { result ->
                _state.update { currentState ->
                    currentState.copy(searchResults = result, isLoading = false)
                }
            }
            _state.update { currentState ->
                currentState.copy(searchResults=currentState.searchResults+cachedChats)
            }
            delay(500)

            _state.update { currentState ->
                currentState.copy(searchResults=currentState.searchResults-cachedChats)
            }
        }
    }

    fun toggleInitialLoad(){
        _state.update { it.copy(isInitialLoad = !it.isInitialLoad) }
    }



    @OptIn(ExperimentalUuidApi::class)
    fun onAction(action: ChatScreenAction){
        when(action){
            is ChatScreenAction.OnSearchQueryChange -> {
                _state.update{
                    it.copy(searchQuery = action.query)
                }
            }
            is ChatScreenAction.OnSendClick -> {

               var chat = Chat(
                   "0",
                   "basic question",
                   "basic answer"
               )
                _state.update { currentState ->
                    currentState.copy(isLoading = true, isInitialLoad = false)
                }
                viewModelScope.launch {
                    chatRepository.fetchChats(action.question).onSuccess {
                        chatRepository.persistChat(it)
                    }.onError {
                        chat.question = it.name
                        chat.response  = it.ordinal.toString()
                    }
                    chatRepository.getLatestChats().collect{ result ->
                        _state.update { currentState ->
                            currentState.copy(
                                searchResults = result, isLoading = false
                            )
                        }
                    }
                }
            }
        }
    }
}