package com.example.geminiai.navigation

import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.example.geminiai.ui.screen.ChatScreen
import kotlinx.serialization.Serializable

@Serializable
object ChatScreen : NavKey

@Composable
fun NavStack(navBackStack: NavBackStack<NavKey>) {
    NavDisplay(
        backStack = navBackStack,
        onBack = { navBackStack.removeLastOrNull() },
        entryProvider = entryProvider {
            entry<ChatScreen> {
                ChatScreen()
            }
        }
    )
}