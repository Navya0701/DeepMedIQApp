package org.deepmediq.chat.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.deepmediq.chat.data.database.ChatDao
import org.deepmediq.chat.data.network.RemoteChatDataSource
import org.deepmediq.chat.domain.Chat
import org.deepmediq.chat.domain.ChatRepository
import org.deepmediq.core.domain.DataError
import org.deepmediq.core.domain.Result
import org.deepmediq.chat.data.mappers.toChat
import org.deepmediq.chat.data.mappers.toChatEntity
import org.deepmediq.core.domain.map

class DefaultChatRepository(
    private val remoteChatDataSource: RemoteChatDataSource,
    private val chatDao: ChatDao
): ChatRepository {

    // this request goes to the backend server
    override suspend fun fetchChats(query: String): Result<Chat, DataError.Remote> {
        return remoteChatDataSource.fetchChats(query).map {
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