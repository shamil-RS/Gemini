package com.example.geminiai.repository

import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ChatRepositoryTest {

    @Test
    fun findMessages() = runTest {
        val repository = createTestRepository()
        repository.findMessages().test {
            assertThat(awaitItem()).hasSize(2)
        }
    }
}
