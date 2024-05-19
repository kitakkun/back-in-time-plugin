package com.github.kitakkun.backintime.debugger.feature.settings

import org.koin.compose.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val settingsFeatureModule = module {
    viewModelOf(::SettingsViewModel)
}
