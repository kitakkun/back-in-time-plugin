package com.kitakkun.backintime.evaluation

import android.app.Application
import com.kitakkun.backintime.demo.flipper.FlipperInitializer
import com.kitakkun.backintime.evaluation.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        FlipperInitializer().init(this)

        startKoin {
            androidLogger()
            androidContext(this@MyApplication)
            modules(appModule)
        }
    }
}
