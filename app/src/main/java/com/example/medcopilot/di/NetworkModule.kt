package com.example.medcopilot.di

import com.example.medcopilot.BuildConfig
import com.example.medcopilot.api.ChatAPI
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {

    @Singleton
    @Provides
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS) // Increase connection timeout
            .readTimeout(30, TimeUnit.SECONDS)   // Increase read timeout
            .writeTimeout(30, TimeUnit.SECONDS)  // Increase write timeout
            .build()
    }

    @Singleton
    @Provides
    fun provideRetrofit(client: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://mcapisvc2.azurewebsites.net")
            .client(client) // Use custom OkHttpClient
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Singleton
    @Provides
    fun provideChatAPI(retrofit: Retrofit): ChatAPI {
        return retrofit.create(ChatAPI::class.java)
    }
}