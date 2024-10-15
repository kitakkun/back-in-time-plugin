package io.github.kitakkun.backintime.debugger.core.database.di

import io.github.kitakkun.backintime.debugger.core.database.BackInTimeDatabase
import io.github.kitakkun.backintime.debugger.core.database.createBackInTimeDatabase
import org.koin.dsl.module

val databaseModule = module {
    single { createBackInTimeDatabase() }
    single { get<BackInTimeDatabase>().sessionInfoDao() }
    single { get<BackInTimeDatabase>().classInfoDao() }
    single { get<BackInTimeDatabase>().instanceDao() }
    single { get<BackInTimeDatabase>().methodCallDao() }
    single { get<BackInTimeDatabase>().eventLogDao() }
    single { get<BackInTimeDatabase>().valueChangeDao() }
}
