package org.deepmediq.chat.data.network

import org.deepmediq.chat.data.dto.FetchResponseDto
import org.deepmediq.core.domain.Result
import org.deepmediq.core.domain.DataError


interface RemoteChatDataSource {
    suspend fun fetchChats(
        query: String
    ): Result<FetchResponseDto, DataError.Remote>
}

// this is the place the taken FetchResponseDto is converted to List<Chat> after conversion