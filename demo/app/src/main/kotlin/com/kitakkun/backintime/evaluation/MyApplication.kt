package com.kitakkun.backintime.evaluation

import android.app.Application
import com.kitakkun.backintime.core.annotations.BackInTimeEntryPoint
import com.kitakkun.backintime.evaluation.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class MyApplication : Application() {
    @BackInTimeEntryPoint(host = "10.0.2.2", port = 50023)
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@MyApplication)
            modules(appModule)
        }
    }
}
