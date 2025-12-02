package com.example.geminiai.data

import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import com.example.geminiai.data.room.AppDatabase

fun createTestDatabase(): AppDatabase {
    val context = InstrumentationRegistry.getInstrumentation().targetContext
    return Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
        .build()
}
