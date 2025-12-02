package com.example.geminiai.data.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.geminiai.model.Message
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatDao {

    @Insert
    suspend fun insert(message: Message): Long

    @Query("SELECT * FROM Message ORDER BY timestamp DESC")
    fun allByChatId(): Flow<List<Message>>

    /**
     * Obtains a list of all messages in the chat. Newer messages come first.
     */
    @Query("SELECT * FROM Message ORDER BY timestamp DESC")
    suspend fun loadAll(): List<Message>

    @Query("DELETE FROM Message")
    suspend fun clearAll()
}
