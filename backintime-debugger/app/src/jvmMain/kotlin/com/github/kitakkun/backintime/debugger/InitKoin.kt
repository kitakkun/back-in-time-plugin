package com.github.kitakkun.backintime.debugger

import org.koin.core.context.startKoin
import org.koin.ksp.generated.module

fun initKoin() {
    startKoin {
        modules(AppModule().module)
    }
}
