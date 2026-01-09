package com.example.geminiai.data.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.geminiai.data.room.entity.ChatEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatDao {

    @Insert
    suspend fun insert(chat: ChatEntity): Long

    @Query("SELECT * FROM chats ORDER BY created_at DESC")
    fun allDetails(): Flow<List<ChatEntity>>

    @Query("SELECT * FROM chats WHERE chat_id = :id")
    suspend fun loadDetailById(id: Long): ChatEntity

    @Query("SELECT * FROM chats WHERE chat_id = :id")
    fun detailById(id: Long): Flow<ChatEntity?>

    @Query("SELECT * FROM chats")
    suspend fun loadAllDetails(): List<ChatEntity>

    @Update
    suspend fun editChat(chat: ChatEntity)
}
