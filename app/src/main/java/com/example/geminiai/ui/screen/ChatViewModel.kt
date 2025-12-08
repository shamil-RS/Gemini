@file:OptIn(ExperimentalCoroutinesApi::class)

package com.example.geminiai.ui.screen

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.geminiai.R
import com.example.geminiai.model.Message
import com.example.geminiai.repository.ChatRepository
import com.example.geminiai.ui.stateInUi
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    @ApplicationContext
    private val application: Context,
    private val repository: ChatRepository,
) : ViewModel() {
    private val chatMessagesCount = MutableStateFlow(0L)

    val messages = chatMessagesCount
        .flatMapLatest { repository.findMessages() }
        .map { it.reversed().map { it.toChatMessage() } }
        .stateInUi(emptyList())

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
            _isSendMessageState.update { it.copy(isStateMessage = true) }
            try {
                repository.sendMessage(input, null, null)
                _input.value = ""
            } finally {
                _isSendMessageState.update { it.copy(isStateMessage = false) }
            }
        }
    }

    fun retryMessage() {
        sendJob = viewModelScope.launch {
            _isSendMessageState.value = _isSendMessageState.value.copy(isStateMessage = true)
            try {
                repository.sendMessage(messages.value.dropLast(1).last().text, null, null)
                _isSendMessageState.update { it.copy(isStateMessage = false) }
                _input.value = ""
            } finally {
                _isSendMessageState.update { it.copy(isStateMessage = false) }
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
            } finally {
                _isSendMessageState.value = _isSendMessageState.value.copy(isStateMessage = false)
            }
        }
    }

    fun cancelSendMessage() {
        _isCanceledMessage.value = true
        _input.value = ""
        sendJob?.cancel()
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

fun Message.toChatMessage(): ChatMessage = ChatMessage(
    id = id,
    text = text,
    mediaUri = mediaUri,
    mediaMimeType = mediaMimeType,
    timestamp = timestamp,
    isIncoming = isIncoming,
)

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

fun Message.toChatMessage(): ChatMessage = ChatMessage(
    id = id,
    text = text,
    mediaUri = mediaUri,
    mediaMimeType = mediaMimeType,
    timestamp = timestamp,
    isIncoming = isIncoming,
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
