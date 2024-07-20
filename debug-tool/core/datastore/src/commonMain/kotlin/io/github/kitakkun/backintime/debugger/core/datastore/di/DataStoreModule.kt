package io.github.kitakkun.backintime.debugger.core.datastore.di

import io.github.kitakkun.backintime.debugger.core.datastore.BackInTimePreferences
import io.github.kitakkun.backintime.debugger.core.datastore.BackInTimePreferencesImpl
import io.github.kitakkun.backintime.debugger.core.datastore.createDataStore
import io.github.kitakkun.backintime.debugger.core.datastore.dataStoreFileName
import org.koin.dsl.module

val dataStoreModule = module {
    single<BackInTimePreferences> {
        BackInTimePreferencesImpl(createDataStore { dataStoreFileName })
    }
}
