package com.example.medcopilot.data

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
    val output: String? = ""
)