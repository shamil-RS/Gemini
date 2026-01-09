package com.example.geminiai.navigation

import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.example.geminiai.ui.screen.chats.ChatScreen
import kotlinx.serialization.Serializable

@Serializable
data class ChatScreen(
    val chatID: Long,
    val chatTitle: String = "Gemini"
) : NavKey

@Composable
fun NavStack(navBackStack: NavBackStack<NavKey>) {
    NavDisplay(
        backStack = navBackStack,
        onBack = { navBackStack.removeLastOrNull() },
        entryProvider = entryProvider {
            entry<ChatScreen> {
                ChatScreen(
                    chatId = it.chatID,
                    title = it.chatTitle,
                    navigateToNewChat = {
                        val newChatId = -(System.currentTimeMillis())
                        navBackStack.add(ChatScreen(newChatId))
                    },
                    onExistingChatClick = { chat ->
                        navBackStack.add(ChatScreen(chat.id, chat.title))
                    }
                )
            }
        }
    )
}
