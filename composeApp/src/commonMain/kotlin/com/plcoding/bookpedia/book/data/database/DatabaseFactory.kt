package com.plcoding.bookpedia.book.data.database

import androidx.room.RoomDatabase
import com.plcoding.bookpedia.book.data.database.ChatDatabase

expect class DatabaseFactory {
    fun create(): RoomDatabase.Builder<ChatDatabase>
}
