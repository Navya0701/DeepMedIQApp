package org.deepmediq.chat.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.deepmediq.chat.data.dto.RelatedQuestion

@Entity
data class ChatEntity(
    @PrimaryKey(autoGenerate = false) val id: String,
    val question: String,
    val response: String,
    val relatedQuestions: String
)