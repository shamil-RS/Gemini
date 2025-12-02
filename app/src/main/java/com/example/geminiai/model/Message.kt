package com.example.geminiai.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity
data class Message(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val chatId: Long = 0,
    val senderId: Long = 0,
    val text: String = "",
    val mediaUri: String?,
    val mediaMimeType: String?,
    val timestamp: Long,
) {

    val isIncoming: Boolean
        get() = senderId != 0L
}
