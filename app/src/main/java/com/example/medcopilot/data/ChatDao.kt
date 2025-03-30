package com.example.medcopilot.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow


@Dao
interface ChatDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChat(chat: Chat)

    @Delete
    suspend fun deleteChat(chat: Chat)

    @Query("SELECT * FROM chat WHERE id = :id")
    suspend fun getChatById(id: Int): Chat?

    @Query("SELECT * FROM chat")
    fun getChats(): Flow<List<Chat>>
}