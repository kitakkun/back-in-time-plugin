package com.kitakkun.backintime.evaluation

import android.app.Application
import com.kitakkun.backintime.core.runtime.BackInTimeDebugService
import com.kitakkun.backintime.core.runtime.connector.BackInTimeKtorWebSocketConnector
import com.kitakkun.backintime.core.runtime.getBackInTimeDebugService
import com.kitakkun.backintime.core.runtime.internal.BackInTimeCompilerInternalApi
import com.kitakkun.backintime.evaluation.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        @OptIn(BackInTimeCompilerInternalApi::class)
        val service: BackInTimeDebugService = getBackInTimeDebugService()

        service.setConnector(BackInTimeKtorWebSocketConnector(host = "10.0.2.2", port = 50023))
        service.startService()

        startKoin {
            androidLogger()
            androidContext(this@MyApplication)
            modules(appModule)
        }
    }
}
