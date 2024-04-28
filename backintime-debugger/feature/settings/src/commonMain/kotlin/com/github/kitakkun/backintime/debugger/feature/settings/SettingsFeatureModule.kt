package com.github.kitakkun.backintime.debugger.feature.settings

import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val settingsFeatureModule = module {
    factoryOf(::SettingsViewModel)
}
