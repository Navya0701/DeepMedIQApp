package com.plcoding.bookpedia.book.domain

import com.plcoding.bookpedia.core.domain.Result
import com.plcoding.bookpedia.book.domain.Chat
import com.plcoding.bookpedia.core.domain.DataError
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    suspend fun fetchChats(query: String): Result<Chat, DataError.Remote>  // only one functionality for now

    suspend fun persistChat(chat: Chat)

    suspend fun getLatestChats(): Flow<List<Chat>>
}

// this repository is telling that what is the basic functionality that we want from the data source , its just fetching the data right but it must be in the form  of domain model Chat here but we actually fetch from the Remote data source specified in the network module in the form of FetchResponseDTO dto stands for data transfer object