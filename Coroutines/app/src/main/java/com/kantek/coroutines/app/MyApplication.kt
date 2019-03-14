package com.kantek.coroutines.app

import android.app.Application
import android.support.core.extensions.appModules

@Suppress("unused")
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        appModules(AppModule::class)
    }
}