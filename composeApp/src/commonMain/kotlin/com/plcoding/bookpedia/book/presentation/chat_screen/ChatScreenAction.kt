package com.plcoding.bookpedia.book.presentation.chat_screen

sealed interface ChatScreenAction {
    data class OnSearchQueryChange(val query: String): ChatScreenAction
    data class OnSendClick(val question: String): ChatScreenAction

}