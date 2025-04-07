package com.example.deepmediq.di

import android.app.Application
import androidx.room.Room
import com.example.deepmediq.roomDatabase.ChatDatabase
import com.example.deepmediq.roomDatabase.ChatRepository
import com.example.deepmediq.roomDatabase.ChatRepositoryImplementation
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(
    SingletonComponent::class
)
object AppModule {

    @Provides
    @Singleton
    fun provideTodoDatabase(app: Application): ChatDatabase {
        return Room.databaseBuilder(
            app,
            ChatDatabase::class.java,
            "todo_db"
        ).build()

    }

    @Provides
    @Singleton
    fun provideTodoRepository(db: ChatDatabase): ChatRepository {
        return ChatRepositoryImplementation(db.dao)
    }
}