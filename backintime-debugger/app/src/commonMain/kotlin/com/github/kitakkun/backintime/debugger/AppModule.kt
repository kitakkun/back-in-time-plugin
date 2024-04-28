package com.github.kitakkun.backintime.debugger

import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val appModule = module {
    factoryOf(::RootViewModel)
}
