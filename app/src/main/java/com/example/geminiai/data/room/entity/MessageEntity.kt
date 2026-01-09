package com.example.geminiai.data.room.entity

import android.graphics.Bitmap
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class MessageEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val chatId: Long = 0,
    val senderId: Long = 0,
    val text: String = "",
    val image: Bitmap? = null,
    val timestamp: Long = 0,
)