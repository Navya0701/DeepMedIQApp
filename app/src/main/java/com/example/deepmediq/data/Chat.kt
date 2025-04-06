package com.example.deepmediq.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity("chat")
data class Chat(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int = 0,
    @ColumnInfo(name = "input")
    val input: String? = "",
    @ColumnInfo(name = "output")
    val output: String? = "",
    @ColumnInfo(name = "timestamp")
    val timeStamp: String? = ""
)