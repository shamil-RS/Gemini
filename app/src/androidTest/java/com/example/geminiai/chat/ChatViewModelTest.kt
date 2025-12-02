package com.example.geminiai.chat

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.test
import com.example.geminiai.awaitNotEmpty
import com.example.geminiai.model.Message
import com.example.geminiai.repository.createTestRepository
import com.example.geminiai.ui.screen.ChatViewModel
import com.google.common.truth.Truth.assertThat
import dagger.hilt.android.testing.HiltAndroidRule
import io.mockk.coEvery
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ChatViewModelTest {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var application: Application

    @Before
    fun setup() {
        hiltRule.inject()
        application = ApplicationProvider.getApplicationContext()
    }

    @Test
    fun shouldEmitMessagesFromRepository() = runTest {
        val repository = createTestRepository()

        val testMessages = listOf(
            Message(id = 1, text = "Hello", senderId = 0),
            Message(id = 2, text = "Hi!", senderId = 1)
        )
        coEvery { repository.findMessages() } returns flowOf(testMessages)

        val viewModel = ChatViewModel(application, repository)

        viewModel.messages.test {
            val emittedMessages = awaitItem()
            assertThat(emittedMessages).hasSize(2)
            assertThat(emittedMessages[0].text).isEqualTo("Hi!")
            assertThat(emittedMessages[1].text).isEqualTo("Hello")
            awaitComplete()
        }
    }

    @Test
    fun messagesEmission() = runTest {
        val repository = createTestRepository()
        val viewModel = ChatViewModel(application, repository)
        viewModel.messages.test {
            assertThat(awaitNotEmpty()).hasSize(2)
        }
    }
}