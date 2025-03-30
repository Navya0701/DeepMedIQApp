package com.example.medcopilot.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [Chat::class],
    version = 1
)
abstract class ChatDatabase : RoomDatabase(){
    abstract val dao: ChatDao
}