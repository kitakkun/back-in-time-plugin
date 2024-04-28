package com.github.kitakkun.backintime.debugger.feature.connection

import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val connectionFeatureModule = module {
    factoryOf(::ConnectionViewModel)
}
