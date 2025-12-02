package com.example.geminiai.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.geminiai.data.room.dao.ChatDao
import com.example.geminiai.model.Message

@Database(
    entities = [
        Message::class,
    ],
    version = 1,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun chatDao(): ChatDao
}