package com.plcoding.bookpedia.book.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ChatEntity(
    @PrimaryKey(autoGenerate = false) val id: String,
    val question: String,
    val response: String,
    val relatedQuestions: String
)