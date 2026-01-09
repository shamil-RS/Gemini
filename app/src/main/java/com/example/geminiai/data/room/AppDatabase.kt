package com.example.geminiai.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.geminiai.data.room.dao.ChatDao
import com.example.geminiai.data.room.dao.MessageDao
import com.example.geminiai.data.room.entity.ChatEntity
import com.example.geminiai.data.room.entity.MessageEntity
import com.example.geminiai.model.Chat
import com.example.geminiai.model.Message
import com.example.geminiai.ui.util.bitmap.BitmapConverters

@Database(
    entities = [
        ChatEntity::class,
        MessageEntity::class,
    ],
    version = 2
)
@TypeConverters(BitmapConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun chatDao(): ChatDao
    abstract fun messageDao(): MessageDao
}