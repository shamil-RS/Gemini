@file:OptIn(ExperimentalCoroutinesApi::class)

package com.example.geminiai.ui.screen.chats

import android.content.Context
import android.graphics.Bitmap
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.geminiai.R
import com.example.geminiai.model.Chat
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

    private val _chat = MutableStateFlow(Chat(id = -1, title = "Gemini"))
    val chat = _chat.asStateFlow()

    val chatListState: StateFlow<ChatListState?> = _chat
        .flatMapLatest { repository.allDetails().map { chats -> ChatListState(chats) } }
        .stateInUi(null)

    val messages = _chat
        .flatMapLatest { chat -> repository.findMessages(chat.id) }
        .map { it.reversed().map { it } }
        .stateInUi(emptyList())

    private val _input = MutableStateFlow("")
    val input = _input.asStateFlow()

    val sendEnabled = _input.map(::isInputValid).stateInUi(false)

    private val _isSendMessageState = MutableStateFlow(Message())
    val isSendMessageState = _isSendMessageState.asStateFlow()

    private val _isImageState = MutableStateFlow(Message())

    private val _isCanceledMessage = MutableStateFlow(false)
    val isCanceledMessage = _isCanceledMessage.asStateFlow()

    private val _messageBlock = MutableStateFlow(BlockMessageState())
    val messageBlock = _messageBlock.asStateFlow()

    private val _isChatTitleDialogOpen = MutableStateFlow(false)
    val isChatTitleDialogOpen = _isChatTitleDialogOpen.asStateFlow()

    var sendJob: Job? = null

    fun sendMessage(image: Bitmap? = null) {
        val text = _input.value
        if (text.isBlank() && image == null) return
        if (image != null) _isImageState.update { it.copy(image = image) }

        sendJob = viewModelScope.launch {
            val currentRoom = _chat.value
            val isNewChat = currentRoom.id <= 0
            val chatId = if (isNewChat) repository.createNewChat() else currentRoom.id

            _isSendMessageState.update { it.copy(isStateMessage = true) }

            try {
                _input.value = ""
                _isCanceledMessage.value = false

                if (isNewChat) {
                    _chat.update { it.copy(id = chatId, title = text) }
                    repository.updateChatTitle(_chat.value, text)
                }

                repository.sendMessage(
                    chatId = chatId,
                    text = text,
                    image = _isImageState.value.image
                )

                _isImageState.update { it.copy(image = null) }
            } finally {
                _isSendMessageState.update { it.copy(isStateMessage = false) }
            }
        }
    }

    fun setChatId(chatId: Long, title: String) {
        _chat.update { it.copy(id = chatId, title = title) }
    }

    fun retryMessage(image: Bitmap? = null) {
        if (image != null) _isImageState.update { it.copy(image = image) }

        sendJob = viewModelScope.launch {
            _isSendMessageState.value = _isSendMessageState.value.copy(isStateMessage = true)
            try {
                repository.sendMessage(
                    chatId = _chat.value.id,
                    text = messages.value.dropLast(1).last().text,
                    image = _isImageState.value.image
                )
                _isImageState.update { it.copy(image = null) }
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
            val currentRoom = _chat.value
            val isNewChat = currentRoom.id <= 0
            val chatId = if (isNewChat) repository.createNewChat() else currentRoom.id

            _isSendMessageState.value = _isSendMessageState.value.copy(isStateMessage = true)

            try {
                val currentSize = messages.value.size

                val updateList = _messageBlock.value.messageList.map { message ->
                    if (message.id == messageBlock.id) {
                        val updateMessage = message.copy(isSelectedBlock = true)

                        if (isNewChat) {
                            _chat.update { it.copy(id = chatId, title = message.textBlock) }
                            repository.updateChatTitle(_chat.value, message.textBlock)
                        }

                        repository.sendMessage(_chat.value.id, messageBlock.textBlock, null)
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
        sendJob = null
    }

    fun clearMessages() {
        viewModelScope.launch {
            repository.clearChatMessage(_chat.value.id)
            Toast.makeText(
                application.applicationContext,
                application.getString(R.string.clear_chat_message),
                Toast.LENGTH_SHORT,
            ).show()
        }
    }

    fun copyToClipboard(text: String) = repository.copyToClipboard(text)

    private fun isInputValid(input: String): Boolean = input.isNotBlank()

    fun updateChatTitle(title: String) {
        if (_chat.value.id > 0) {
            _chat.update { it.copy(title = title) }
            viewModelScope.launch {
                repository.updateChatTitle(_chat.value, title)
            }
        }
    }

    fun closeChatTitleDialog() = _isChatTitleDialogOpen.update { false }
    fun openChatTitleDialog() = _isChatTitleDialogOpen.update { true }

    fun exportChat(): Pair<String, String> {
        // Build the chat history in Markdown format
        val chatHistoryMarkdown = buildString {
            appendLine("# Chat Export: \"${chat.value.title}\"")
            appendLine()
            appendLine("**Exported on:** ${formatCurrentDateTime()}")
            appendLine()
            appendLine("---")
            appendLine()
            appendLine("## Chat History")
            appendLine()
            messages.value.forEach { message ->
                val sender = if (message.isIncoming) "User" else "Assistant"
                appendLine("**$sender:**")
                appendLine(message.text)
                appendLine()
            }
        }

        // Save the Markdown file
        val fileName = "export_${chat.value.title}_${System.currentTimeMillis()}.md"
        return Pair(fileName, chatHistoryMarkdown)
    }

    private fun formatCurrentDateTime(): String {
        val currentDate = java.util.Date()
        val format = java.text.SimpleDateFormat("yyyy-MM-dd hh:mm a", java.util.Locale.getDefault())
        return format.format(currentDate)
    }
}

data class ChatListState(
    val chats: List<Chat> = listOf(),
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