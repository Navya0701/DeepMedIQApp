package org.deepmediq.chat.data.database

import androidx.room.RoomDatabaseConstructor

@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object ChatDatabaseConstructor: RoomDatabaseConstructor<ChatDatabase> {
    override fun initialize(): ChatDatabase
}