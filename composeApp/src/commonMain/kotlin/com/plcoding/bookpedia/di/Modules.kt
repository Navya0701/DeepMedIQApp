package com.plcoding.bookpedia.di

import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.plcoding.bookpedia.book.data.database.ChatDatabase
import com.plcoding.bookpedia.book.data.database.DatabaseFactory
import com.plcoding.bookpedia.book.data.network.KtorRemoteChatDataSource
import com.plcoding.bookpedia.book.data.network.RemoteChatDataSource
import com.plcoding.bookpedia.book.data.repository.DefaultChatRepository
import com.plcoding.bookpedia.book.domain.ChatRepository
import com.plcoding.bookpedia.book.presentation.chat_screen.ChatScreenViewModel

import com.plcoding.bookpedia.core.data.HttpClientFactory
import org.koin.compose.viewmodel.dsl.viewModelOf
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module


expect val platformModule: Module

val sharedModule = module {
    single { HttpClientFactory.create(get()) }
    singleOf(::KtorRemoteChatDataSource).bind<RemoteChatDataSource>()
    singleOf(::DefaultChatRepository).bind<ChatRepository>()
    single {
        get<DatabaseFactory>().create()
            .setDriver(BundledSQLiteDriver())
            .build()
    }
    single { get<ChatDatabase>().chatDao }
    viewModelOf(::ChatScreenViewModel)

}