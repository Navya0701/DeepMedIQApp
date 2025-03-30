package com.example.medcopilot.EntryScreen

import ChatListItem

sealed class ChatGenEvent {

    data class OnAddChatGenClick(val chatListItem: ChatListItem): ChatGenEvent()

}