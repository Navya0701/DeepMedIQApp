package com.example.deepmediq.EntryScreen

import ChatListItem

sealed class ChatGenEvent {

    data class OnAddChatGenClick(val chatListItem: ChatListItem): ChatGenEvent()

}