package com.example.geminiai.navigation

import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.rememberNavBackStack

@Composable
fun AppScreen() {
    val navBackStack = rememberNavBackStack(ChatScreen)

    NavStack(navBackStack = navBackStack)
}