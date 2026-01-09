package com.example.geminiai.repository

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Bitmap
import com.example.geminiai.BuildConfig
import com.example.geminiai.R
import com.example.geminiai.data.mapper.asDomain
import com.example.geminiai.data.mapper.asEntity
import com.example.geminiai.data.room.dao.ChatDao
import com.example.geminiai.data.room.dao.MessageDao
import com.example.geminiai.model.Chat
import com.example.geminiai.model.Message
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.Content
import com.google.ai.client.generativeai.type.asImageOrNull
import com.google.ai.client.generativeai.type.content
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatRepository @Inject constructor(
    @ApplicationContext private val appContext: Context,
    private val chatDao: ChatDao,
    private val messageDao: MessageDao,
    private val clipboardManager: ClipboardManager,
) {
    @SuppressLint("StringFormatInvalid")
    suspend fun sendMessage(
        chatId: Long,
        text: String,
        image: Bitmap? = null,
    ) {
        try {
            // Save the message to the database
            saveMessage(chatId, text, image, senderId = 0L)

            // Create a generative AI Model to interact with the Gemini API.
            val generativeModel = GenerativeModel(
                modelName = "gemini-3-flash-preview",
                // Set your Gemini API in as an `API_KEY` variable in your local.properties file
                apiKey = BuildConfig.API_KEY,
                // Set a system instruction to set the behavior of the model.
                systemInstruction = content {
                    text("Please respond to this chat.")
                },
            )

            // Get the previous messages and them generative model chat
            val pastMessages = getMessageHistory(chatId)
            val chat = generativeModel.startChat(history = pastMessages)

            val content = content {
                text(text)
                if (image != null) {
                    image(image)
                }
            }

            val response = chat.sendMessage(content)

            val responseText = response.text?.trim() ?: "..."
            val responseImage =
                response.candidates.firstOrNull()?.content?.parts?.firstNotNullOfOrNull { it.asImageOrNull() }

            if (responseText.isBlank() && responseImage == null) {
                error("Model returned an empty response")
            } else {
                saveMessage(chatId, responseText, responseImage, 1)
            }
        } catch (e: Exception) {
            currentCoroutineContext().ensureActive()
            e.printStackTrace()
            val errorMessage = appContext.getString(
                R.string.gemini_error,
                e.message ?: appContext.getString(R.string.unknown_error),
            )
            saveMessage(chatId, errorMessage, null, 1)
        }
    }

    fun findMessages(chatId: Long): Flow<List<Message>> =
        messageDao.allByChatId(chatId)
            .map { entities ->
                entities.map { entity ->
                    entity.asDomain()
                }
            }

    fun allDetails(): Flow<List<Chat>> =
        chatDao.allDetails()
            .map { entities ->
                entities.map { entity ->
                    entity.asDomain()
                }
            }

    suspend fun createNewChat(): Long = chatDao.insert(Chat(0L, "").asEntity())

    private suspend fun getMessageHistory(chatId: Long): List<Content> {
        val pastMessages = findMessages(chatId).first().filter { message ->
            message.text.isNotEmpty()
        }.sortedBy { message ->
            message.timestamp
        }.fold(initial = mutableListOf<Message>()) { acc, message ->
            if (acc.isEmpty()) acc.add(message)
            else {
                if (acc.last().isIncoming == message.isIncoming) {
                    val lastMessage = acc.removeAt(acc.size - 1)
                    val combinedMessage = Message(
                        id = lastMessage.id,
                        // User
                        senderId = lastMessage.senderId,
                        text = lastMessage.text + " " + message.text,
                        timestamp = System.currentTimeMillis(),
                    )
                    acc.add(combinedMessage)
                } else {
                    acc.add(message)
                }
            }
            return@fold acc
        }

        val pastContents = pastMessages.map { message: Message ->
            val role = if (message.isIncoming) "model" else "user"
            content(role = role) { text(message.text) }
        }

        return pastContents
    }

    private suspend fun saveMessage(
        chatId: Long,
        text: String,
        image: Bitmap? = null,
        senderId: Long,
    ) {
        messageDao.insert(
            Message(
                id = 0L,
                chatId = chatId,
                senderId = senderId,
                text = text,
                image = image,
                timestamp = System.currentTimeMillis(),
            ).asEntity()
        )
    }

    suspend fun updateChatTitle(chat: Chat, title: String) {
        chatDao.editChat(chat.asEntity().copy(title = title.replace('\n', ' ').take(50)))
    }

    fun copyToClipboard(text: String) {
        val clip = ClipData.newPlainText("Text", text)
        clipboardManager.setPrimaryClip(clip)
    }

    suspend fun clearChatMessage(chatId: Long) {
        messageDao.clearAll(chatId)
    }
}
