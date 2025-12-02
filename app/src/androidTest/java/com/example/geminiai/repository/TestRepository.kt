package com.example.geminiai.repository

import android.content.ClipboardManager
import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry
import com.example.geminiai.data.createTestDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

fun createTestRepository(): ChatRepository {
    val context = InstrumentationRegistry.getInstrumentation().targetContext
    val database = createTestDatabase()
    val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    return ChatRepository(
        appContext = context,
        coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default),
        chatDao = database.chatDao(),
        clipboardManager = clipboardManager,
    )
}

