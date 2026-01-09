package com.example.geminiai.di

import android.content.ClipboardManager
import android.content.Context
import androidx.room.Room
import com.example.geminiai.data.room.AppDatabase
import com.example.geminiai.data.room.dao.ChatDao
import com.example.geminiai.data.room.dao.MessageDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
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
    fun providesChatDao(database: AppDatabase): ChatDao = database.chatDao()

    @Provides
    fun providesMessageDao(database: AppDatabase): MessageDao = database.messageDao()

    @Provides
    fun provideClipboardManager(@ApplicationContext context: Context): ClipboardManager {
        return context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    }
}
