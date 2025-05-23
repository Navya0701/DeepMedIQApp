package org.deepmediq.chat.presentation.chat_screen
import org.deepmediq.chat.domain.Chat


data class ChatScreenState(
    val searchQuery: String = "",
    var searchResults: List<Chat> = emptyList(),
    var favouriteChats: List<Chat> = emptyList(),
    var isLoading: Boolean = true,
    var goLast: Boolean = true,
    var isInitialLoad:Boolean = true
)