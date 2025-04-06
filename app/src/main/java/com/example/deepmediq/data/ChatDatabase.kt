package com.example.deepmediq.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [Chat::class],
    version = 2
)
abstract class ChatDatabase : RoomDatabase(){
    abstract val dao: ChatDao
}