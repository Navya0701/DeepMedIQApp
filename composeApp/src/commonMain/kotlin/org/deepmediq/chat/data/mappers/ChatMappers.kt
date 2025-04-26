package org.deepmediq.chat.data.mappers

import org.deepmediq.chat.data.database.ChatEntity
import org.deepmediq.chat.data.dto.FetchResponseDto
import org.deepmediq.chat.domain.Chat

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

fun ChatEntity.toChat(): Chat{
    return Chat(
        id = id,
        question = question,
        response = response,
        relatedQuestions = relatedQuestions
    )
}