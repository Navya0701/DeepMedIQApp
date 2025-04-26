package org.deepmediq.chat.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class FetchResponseDto(
    val answer: String,
    val context: List<FetchContext> = emptyList(),
    val input: String,
    val id: String = generateDefaultId()
)

@Serializable
data class FetchContext(
    val metadata: FetchMetadata? = null,
    val page_content: String? = null
)

@Serializable
data class FetchMetadata(
    val author: String? = null,
    val year: Int? = null,
    val journal: String? = null,
    val title: String? = null
)

expect fun generateDefaultId(): String