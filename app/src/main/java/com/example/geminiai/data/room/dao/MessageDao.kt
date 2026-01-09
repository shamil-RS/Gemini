package com.example.geminiai.data.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.geminiai.data.room.entity.MessageEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MessageDao {

    @Insert
    suspend fun insert(message: MessageEntity): Long

    /**
     * Obtains a list of all messages in the chat. Newer messages come first.
     */
    @Query("SELECT * FROM MessageEntity WHERE chatId = :chatId ORDER BY timestamp DESC")
    fun allByChatId(chatId: Long): Flow<List<MessageEntity>>

    @Query("DELETE FROM MessageEntity WHERE chatId = :chatId")
    suspend fun clearAll(chatId: Long)
}

