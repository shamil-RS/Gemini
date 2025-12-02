@file:OptIn(ExperimentalCoroutinesApi::class)

package com.example.geminiai.ui.screen

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.geminiai.R
import com.example.geminiai.repository.ChatRepository
import com.example.geminiai.ui.stateInUi
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

@HiltViewModel
class ChatViewModel @Inject constructor(
    @ApplicationContext
    private val application: Context,
    private val repository: ChatRepository,
) : ViewModel() {
    private val chatMessage = MutableStateFlow(0L)
    private val _messages = chatMessage.flatMapLatest { repository.findMessages() }
    val messages = _messages.map { messages ->
        buildList {
            for (i in messages.indices.reversed()) {
                val message = messages[i]
                add(
                    ChatMessage(
                        id = message.id,
                        text = message.text,
                        mediaUri = message.mediaUri,
                        mediaMimeType = message.mediaMimeType,
                        timestamp = message.timestamp,
                        isIncoming = message.isIncoming,
                    ),
                )
            }
        }
    }.stateInUi(emptyList())

    private val _input = MutableStateFlow("")
    val input: StateFlow<String> = _input

    val sendEnabled = _input.map(::isInputValid).stateInUi(false)

    private val _isSendMessageState = MutableStateFlow(ChatMessage())
    val isSendMessageState = _isSendMessageState.asStateFlow()

    private val _isCanceledMessage = MutableStateFlow(false)
    val isCanceledMessage = _isCanceledMessage.asStateFlow()

    private val _messageBlock = MutableStateFlow(BlockMessageState())
    val messageBlock = _messageBlock.asStateFlow()

    var sendJob: Job? = null

    fun sendMessage() {
        val input = _input.value
        if (!isInputValid(input)) return
        sendJob = viewModelScope.launch {
            _isSendMessageState.value = _isSendMessageState.value.copy(isStateMessage = true)
            try {
                ensureActive()

                repository.sendMessage(input, null, null)
                _input.value = ""
                val currentSize = messages.value.size
                messages.first { it.size > currentSize + 1 }
                _isSendMessageState.value = _isSendMessageState.value.copy(isStateMessage = false)
            } catch (e: CancellationException) {
                throw e
            } finally {
                _isSendMessageState.value = _isSendMessageState.value.copy(isStateMessage = false)
            }
        }
    }

    fun retryMessage() {
        sendJob = viewModelScope.launch {
            _isSendMessageState.value = _isSendMessageState.value.copy(isStateMessage = true)
            try {
                ensureActive()

                val currentSize = messages.value.size
                repository.sendMessage(messages.value.dropLast(1).last().text, null, null)
                messages.first { it.size > currentSize + 1 }
                _isSendMessageState.value = _isSendMessageState.value.copy(isStateMessage = false)
                _input.value = ""
            } catch (e: CancellationException) {
                throw e
            } finally {
                _isSendMessageState.value = _isSendMessageState.value.copy(isStateMessage = false)
            }
        }
    }

    fun updateInput(input: String) {
        _input.value = input
    }

    fun selectedBlock(messageBlock: MessageBlock) {
        sendJob = viewModelScope.launch {
            _isSendMessageState.value = _isSendMessageState.value.copy(isStateMessage = true)
            try {
                ensureActive()

                val currentSize = messages.value.size

                val updateList = _messageBlock.value.messageList.map { message ->
                    if (message.id == messageBlock.id) {
                        val updateMessage = message.copy(isSelectedBlock = true)
                        repository.sendMessage(messageBlock.textBlock, null, null)
                        messages.first { it.size > currentSize + 1 }
                        updateMessage
                    } else message
                }

                _messageBlock.value = _messageBlock.value.copy(messageList = updateList)
            } catch (e: CancellationException) {
                throw e
            } finally {
                _isSendMessageState.value = _isSendMessageState.value.copy(isStateMessage = false)
            }
        }
    }

    fun cancelSendMessage() {
        _isCanceledMessage.value = true
        _input.value = ""
        sendJob?.cancel()
        repository.cancelCurrentJob()
    }

    fun clearMessages() {
        viewModelScope.launch {
            repository.clearAllChatMessage()
            Toast.makeText(
                application.applicationContext,
                application.getString(R.string.clear_chat_message),
                Toast.LENGTH_SHORT,
            ).show()
        }
    }

    fun copyToClipboard(text: String) = repository.copyToClipboard(text)

    private fun isInputValid(input: String): Boolean = input.isNotBlank()
}

data class ChatMessage(
    val id: Long = 0,
    val text: String = "",
    val mediaUri: String? = "",
    val mediaMimeType: String? = "",
    val timestamp: Long = 0,
    val isIncoming: Boolean = false,
    val isTyping: Boolean = false,
    val isStateMessage: Boolean = false,
)

data class BlockMessageState(
    val messageList: List<MessageBlock> = MessageBlock.list
)

data class MessageBlock(
    val id: Int = 0,
    val textBlock: String = "",
    val isSelectedBlock: Boolean = false
) {
    companion object {
        val list = listOf(
            MessageBlock(id = 1, textBlock = "✏️ Write anything"),
            MessageBlock(id = 2, textBlock = "📖 Give me advice"),
            MessageBlock(id = 3, textBlock = "💡 Think of an idea"),
        )
    }
}
