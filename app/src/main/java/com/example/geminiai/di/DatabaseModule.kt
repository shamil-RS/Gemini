package com.example.geminiai.di

import android.content.ClipboardManager
import android.content.Context
import androidx.room.Room
import com.example.geminiai.data.room.AppDatabase
import com.example.geminiai.data.room.dao.ChatDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import java.util.concurrent.Executors
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun providesAppDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(
            context,
            klass = AppDatabase::class.java,
            name = "app.db"
        )
            .fallbackToDestructiveMigration(true)
            .build()

    @Provides
    fun providesMessageDao(database: AppDatabase): ChatDao = database.chatDao()

    @Provides
    fun provideClipboardManager(@ApplicationContext context: Context): ClipboardManager {
        return context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    }
}
