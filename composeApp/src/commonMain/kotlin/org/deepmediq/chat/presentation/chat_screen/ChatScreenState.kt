package org.deepmediq.chat.presentation.chat_screen
import org.deepmediq.chat.domain.Chat


data class ChatScreenState(
    val searchQuery: String = "",
    val searchResults: List<Chat> = emptyList(),
    val favouriteChats: List<Chat> = emptyList(),
    val isLoading: Boolean = false,
    val isInitialLoad: Boolean = true,
    val initialQuestions: List<String> = listOf(
        "What is the best modality to screen for Barrett's esophagus?",
        "What causes Crohn's?",
        "What is the treatment for high grade dysplasia?",
        "Which endoscopic procedures are high-risk?"
    )
)