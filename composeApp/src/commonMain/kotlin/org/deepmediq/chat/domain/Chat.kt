package org.deepmediq.chat.domain

data class Chat(
    val id: String,
    var question: String,
    var response: String,
    val relatedQuestions: String = "",
)