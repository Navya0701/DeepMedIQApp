package org.deepmediq.chat.presentation.chat_screen
import org.deepmediq.chat.domain.Chat


data class ChatScreenState(
    val searchQuery: String = "How can DeepMedIQ help you today..",
    var searchResults: List<Chat> = emptyList(),
    var favouriteChats: List<Chat> = emptyList(),
    var isLoading: Boolean = false,
)