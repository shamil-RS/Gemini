package com.example.geminiai.repository

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import com.example.geminiai.BuildConfig
import com.example.geminiai.R
import com.example.geminiai.data.room.dao.ChatDao
import com.example.geminiai.di.AppCoroutineScope
import com.example.geminiai.model.Message
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.Content
import com.google.ai.client.generativeai.type.content
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatRepository @Inject constructor(
    @ApplicationContext private val appContext: Context,
    @AppCoroutineScope private val coroutineScope: CoroutineScope,
    private val chatDao: ChatDao,
    private val clipboardManager: ClipboardManager,
) {

    var currentJob: Job? = null

    @SuppressLint("StringFormatInvalid")
    suspend fun sendMessage(
        text: String,
        mediaUri: String?,
        mediaMimeType: String?,
    ) {
        // Save the message to the database
        saveMessage(text, senderId = 0L, mediaUri, mediaMimeType)

        // Create a generative AI Model to interact with the Gemini API.
        val generativeModel = GenerativeModel(
            modelName = "gemini-2.5-flash",
            // Set your Gemini API in as an `API_KEY` variable in your local.properties file
            apiKey = BuildConfig.API_KEY,
            // Set a system instruction to set the behavior of the model.
            systemInstruction = content {
                text("Please respond to this chat.")
            },
        )

        coroutineScope.launch {
            // Get the previous messages and them generative model chat
            val pastMessages = getMessageHistory()
            val chat = generativeModel.startChat(
                history = pastMessages,
            )

            // Send a message prompt to the model to generate a response
            val response = try {
                if (mediaMimeType?.contains("image") == true) {
                    appContext.contentResolver.openInputStream(
                        Uri.parse(mediaUri),
                    ).use {
                        if (it != null) chat.sendMessage(BitmapFactory.decodeStream(it)).text?.trim()
                            ?: "..."
                        else appContext.getString(R.string.image_error)
                    }
                } else {
                    chat.sendMessage(text).text?.trim() ?: "..."
                }
            } catch (e: Exception) {
                e.printStackTrace()
                appContext.getString(
                    R.string.gemini_error,
                    e.message ?: appContext.getString(R.string.unknown_error),
                )
            }

            // Save the generated response to the database
            saveMessage(response, 1, null, null)
        }
    }

    fun cancelCurrentJob() {
        currentJob?.cancel()
        currentJob = null
    }

    fun findMessages(): Flow<List<Message>> = chatDao.allByChatId()

    @RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
    private suspend fun getMessageHistory(): List<Content> {
        val pastMessages = findMessages().first().filter { message ->
            message.text.isNotEmpty()
        }.sortedBy { message ->
            message.timestamp
        }.fold(initial = mutableListOf<Message>()) { acc, message ->
            if (acc.isEmpty()) acc.add(message)
            else {
                if (acc.last().isIncoming == message.isIncoming) {
                    val lastMessage = acc.removeLast()
                    val combinedMessage = Message(
                        id = lastMessage.id,
                        // User
                        senderId = lastMessage.senderId,
                        text = lastMessage.text + " " + message.text,
                        mediaUri = null,
                        mediaMimeType = null,
                        timestamp = System.currentTimeMillis(),
                    )
                    acc.add(combinedMessage)
                } else {
                    acc.add(message)
                }
            }
            return@fold acc
        }

        val lastUserMessage = pastMessages.removeLast()

        val pastContents = pastMessages.mapNotNull { message: Message ->
            val role = if (message.isIncoming) "model" else "user"
            return@mapNotNull content(role = role) { text(message.text) }
        }
        return pastContents
    }

    private suspend fun saveMessage(
        text: String,
        senderId: Long,
        mediaUri: String?,
        mediaMimeType: String?,
    ) {
        chatDao.insert(
            Message(
                id = 0L,
                senderId = senderId,
                text = text,
                mediaUri = mediaUri,
                mediaMimeType = mediaMimeType,
                timestamp = System.currentTimeMillis(),
            ),
        )
    }

    fun copyToClipboard(text: String) {
        val clip = ClipData.newPlainText("Text", text)
        clipboardManager.setPrimaryClip(clip)
    }

    suspend fun clearAllChatMessage() = chatDao.clearAll()
}
