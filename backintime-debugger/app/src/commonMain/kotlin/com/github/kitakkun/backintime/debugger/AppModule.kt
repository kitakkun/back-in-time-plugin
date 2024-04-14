package com.github.kitakkun.backintime.debugger

import com.github.kitakkun.backintime.debugger.root.RootScreenModel
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val appModule = module {
    factoryOf(::RootScreenModel)
}
