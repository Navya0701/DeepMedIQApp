package org.deepmediq.di

import android.content.Context
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.okhttp.OkHttp
import org.deepmediq.chat.data.database.DatabaseFactory
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformModule: Module
    get() = module {
        single< HttpClientEngine>{ OkHttp.create()}
        single { DatabaseFactory(androidApplication()) }
    }