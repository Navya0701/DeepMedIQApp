package com.example.medcopilot.data

import kotlinx.coroutines.flow.Flow

interface ChatRepository {

    suspend fun insertChat(chat: Chat)

    suspend fun deleteChat(chat: Chat)

    suspend fun getChatById(id: Int): Chat? // here we are using nullable to stop app from crashing

    fun getChats(): Flow<List<Chat>> // flow means we get realtime updates when something changes and these help to update the ui

}