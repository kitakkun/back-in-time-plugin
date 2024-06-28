package com.github.kitakkun.backintime.evaluation

import android.app.Application
import com.facebook.flipper.BuildConfig
import com.github.kitakkun.backintime.evaluation.di.appModule
import com.github.kitakkun.backintime.runtime.BackInTimeDebugService
import com.github.kitakkun.backintime.runtime.connector.BackInTimeWebSocketClientConnector
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            val connector = BackInTimeWebSocketClientConnector("10.0.2.2", 8080)
            BackInTimeDebugService.setConnector(connector)
            BackInTimeDebugService.startService()
        }

        startKoin {
            androidLogger()
            androidContext(this@MyApplication)
            modules(appModule)
        }
    }
}
