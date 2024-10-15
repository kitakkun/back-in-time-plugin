package io.github.kitakkun.backintime.debugger.app

import io.github.kitakkun.backintime.debugger.core.data.di.dataModule
import io.github.kitakkun.backintime.debugger.core.database.di.databaseModule
import io.github.kitakkun.backintime.debugger.core.datastore.di.dataStoreModule
import io.github.kitakkun.backintime.debugger.core.server.di.serverModule
import org.koin.dsl.module

val appModule = module {
    includes(
        serverModule,
        dataModule,
        databaseModule,
        dataStoreModule,
    )
}
