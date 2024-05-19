package com.github.kitakkun.backintime.debugger.feature.connection

import org.koin.compose.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val connectionFeatureModule = module {
    viewModelOf(::ConnectionViewModel)
}
