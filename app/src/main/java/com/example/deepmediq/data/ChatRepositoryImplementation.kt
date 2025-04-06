package com.example.deepmediq.data

import kotlinx.coroutines.flow.Flow

class ChatRepositoryImplementation(
    private val dao: ChatDao
): ChatRepository {

    override suspend fun insertChat(chat: Chat) {
        return dao.insertChat(chat)
    }

    override suspend fun deleteChat(chat: Chat) {
        return dao.deleteChat(chat)
    }

    override suspend fun getChatById(id: Int): Chat? {
        return dao.getChatById(id)
    }

    override fun getChats(): Flow<List<Chat>> {
        return dao.getChats()
    }

}