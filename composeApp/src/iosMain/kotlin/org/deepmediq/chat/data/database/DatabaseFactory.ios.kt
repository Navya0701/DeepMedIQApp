package org.deepmediq.chat.data.database

import androidx.room.Room
import androidx.room.RoomDatabase

actual class DatabaseFactory {
    actual fun create(): RoomDatabase.Builder<ChatDatabase> {
        val dbFile = documentDirectory() + "/${ChatDatabase.DB_NAME}"
        return Room.databaseBuilder<ChatDatabase>(
            name = dbFile
        )
    }

    private fun documentDirectory(): String {
        val documentDirectory = NSFileManager.defaultManager.URLForDirectory(
            directory = NSDocumentDirectory,
            inDomain = NSUserDomainMask,
            appropriateForURL = null,
            create = false,
            error = null
        )
        return requireNotNull(documentDirectory?.path)
    }
}