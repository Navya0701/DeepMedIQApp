package org.deepmediq

import android.app.Application
import org.deepmediq.di.initKoin
import org.koin.android.ext.koin.androidContext

class DeepMedIQApplication: Application() {
    override fun onCreate(){
        super.onCreate()
        initKoin{
            androidContext(this@DeepMedIQApplication)
        }
    }
}