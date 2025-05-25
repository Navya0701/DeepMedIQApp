package com.plcoding.bookpedia.book.data.mappers

import com.plcoding.bookpedia.book.data.database.ChatEntity
import com.plcoding.bookpedia.book.data.dto.FetchResponseDto
import com.plcoding.bookpedia.book.domain.Chat

fun FetchResponseDto.toChat(): Chat {
    return Chat(
        id = id,
        question = input,
        response = answer
    )
}

fun Chat.toChatEntity(): ChatEntity {
    return ChatEntity(
        id = id,
        question = question,
        response = response,
        relatedQuestions = relatedQuestions
    )
}

fun ChatEntity.toChat(): Chat {
    return Chat(
        id = id,
        question = question,
        response = response,
        relatedQuestions = relatedQuestions
    )
}