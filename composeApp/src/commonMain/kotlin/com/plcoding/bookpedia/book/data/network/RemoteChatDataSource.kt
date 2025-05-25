package com.plcoding.bookpedia.book.data.network

import com.plcoding.bookpedia.book.data.dto.FetchResponseDto
import com.plcoding.bookpedia.core.domain.DataError
import com.plcoding.bookpedia.core.domain.Result


interface RemoteChatDataSource {
    suspend fun fetchChats(
        query: String
    ): Result<FetchResponseDto, DataError.Remote>
}

// this is the place the taken FetchResponseDto is converted to List<Chat> after conversion