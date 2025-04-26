package org.deepmediq.di

import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import org.deepmediq.chat.data.database.ChatDatabase
import org.deepmediq.chat.data.database.DatabaseFactory
import org.deepmediq.chat.data.network.KtorRemoteChatDataSource
import org.deepmediq.chat.data.network.RemoteChatDataSource
import org.deepmediq.chat.data.repository.DefaultChatRepository
import org.deepmediq.chat.domain.ChatRepository
import org.deepmediq.chat.presentation.chat_screen.ChatScreenViewModel
import org.deepmediq.core.data.HttpClientFactory
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
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