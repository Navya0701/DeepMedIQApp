package com.plcoding.bookpedia.book.data.database

import androidx.room.RoomDatabaseConstructor
import com.plcoding.bookpedia.book.data.database.ChatDatabase

@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object ChatDatabaseConstructor: RoomDatabaseConstructor<ChatDatabase> {
    override fun initialize(): ChatDatabase
}