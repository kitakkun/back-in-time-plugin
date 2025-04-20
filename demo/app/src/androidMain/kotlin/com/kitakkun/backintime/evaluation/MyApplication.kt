package com.kitakkun.backintime.evaluation

import android.app.Application
import androidx.room.Room
import com.kitakkun.backintime.core.annotations.BackInTimeEntryPoint
import com.kitakkun.backintime.evaluation.data.TodoDatabase
import com.kitakkun.backintime.evaluation.di.commonAppModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.dsl.module

class MyApplication : Application() {
    @BackInTimeEntryPoint(host = "10.0.2.2", port = 50023)
    override fun onCreate() {
        super.onCreate()

        startKoin {
            modules(commonAppModule)
            androidLogger()
            androidContext(this@MyApplication)
            modules(
                module {
                    single {
                        val dbFile = applicationContext.getDatabasePath("todo-database.db")
                        Room.databaseBuilder<TodoDatabase>(
                            context = applicationContext,
                            name = dbFile.absolutePath,
                        ).build()
                    }
                }
            )
        }
    }
}
