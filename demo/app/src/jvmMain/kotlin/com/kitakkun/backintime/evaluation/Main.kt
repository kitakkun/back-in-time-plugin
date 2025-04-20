package com.kitakkun.backintime.evaluation

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.kitakkun.backintime.core.annotations.BackInTimeEntryPoint
import com.kitakkun.backintime.evaluation.data.TodoDatabase
import com.kitakkun.backintime.evaluation.di.commonAppModule
import org.koin.compose.KoinApplication
import org.koin.dsl.module
import java.io.File

@BackInTimeEntryPoint(host = "localhost", port = 50023)
fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
    ) {
        KoinApplication(
            application = {
                modules(commonAppModule)
                modules(
                    module {
                        single {
                            val dbFile = File(System.getProperty("java.io.tmpdir"), "todo-database.db")
                            Room.databaseBuilder<TodoDatabase>(name = dbFile.absolutePath)
                                .setDriver(BundledSQLiteDriver())
                                .build()
                        }
                    }
                )
            }
        ) {
            App()
        }
    }
}
