package com.plcoding.bookpedia.book.data.database

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow


@Dao
interface ChatDao {

    @Upsert
    suspend fun upsert(chat: ChatEntity)

    @Query("SELECT * FROM ChatEntity")
    fun getAllChats(): Flow<List<ChatEntity>>

    @Query("SELECT * FROM ChatEntity WHERE id =:id")
    suspend fun getChat(id:String): ChatEntity?

    @Query("DELETE FROM ChatEntity WHERE id = :id")
    suspend fun deleteChat(id: String)
}