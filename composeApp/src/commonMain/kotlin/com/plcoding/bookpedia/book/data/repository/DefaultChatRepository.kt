package com.plcoding.bookpedia.book.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import com.plcoding.bookpedia.book.data.database.ChatDao
import com.plcoding.bookpedia.book.data.network.RemoteChatDataSource
import com.plcoding.bookpedia.book.domain.Chat
import com.plcoding.bookpedia.book.domain.ChatRepository
import com.plcoding.bookpedia.book.data.mappers.toChat
import com.plcoding.bookpedia.book.data.mappers.toChatEntity
import com.plcoding.bookpedia.core.domain.DataError
import com.plcoding.bookpedia.core.domain.Result
import com.plcoding.bookpedia.core.domain.map

class DefaultChatRepository(
    private val remoteChatDataSource: RemoteChatDataSource,
    private val chatDao: ChatDao
): ChatRepository {

    // this request goes to the backend server
    override suspend fun fetchChats(query: String): Result<Chat, DataError.Remote> {
        return remoteChatDataSource.fetchChats(query).map { it->
            it.toChat()
        }
    }

    // need an end point to store the chat object as chatEntity in database
    override suspend fun persistChat(chat: Chat) {
        chatDao.upsert(chat.toChatEntity())
    }

    // this request goes to the database and fetches all the chats
    override suspend fun getLatestChats(): Flow<List<Chat>> {
        return chatDao
            .getAllChats()
            .map { chatEntities ->
                chatEntities.map { it.toChat() }
            }
    }
}