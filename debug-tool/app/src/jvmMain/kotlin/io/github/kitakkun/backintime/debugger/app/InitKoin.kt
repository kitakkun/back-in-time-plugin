package io.github.kitakkun.backintime.debugger.app

import org.koin.core.context.startKoin

fun initKoin() {
    startKoin {
        modules(appModule)
    }
}
