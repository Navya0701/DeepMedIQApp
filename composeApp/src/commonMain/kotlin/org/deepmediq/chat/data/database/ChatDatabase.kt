package org.deepmediq.chat.data.database

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters


@Database(
    entities = [ChatEntity::class],
    version = 1
)

@TypeConverters(
    StringListTypeConverter::class
)

@ConstructedBy(ChatDatabaseConstructor::class)
abstract class ChatDatabase: RoomDatabase() {
    abstract val chatDao: ChatDao

    companion object {
        const val DB_NAME = "chat.db"
    }
}


