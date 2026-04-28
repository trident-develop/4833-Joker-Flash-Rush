package org.example.project

import android.app.Application
import org.example.project.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class FlashApp : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@FlashApp)
            modules(appModule)
        }
    }
}