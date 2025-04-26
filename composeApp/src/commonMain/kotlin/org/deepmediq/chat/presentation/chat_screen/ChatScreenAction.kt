package org.deepmediq.chat.presentation.chat_screen

import org.deepmediq.chat.domain.Chat

sealed interface ChatScreenAction {
    data class OnSearchQueryChange(val query: String): ChatScreenAction
    data class OnSendClick(val question: String): ChatScreenAction

}