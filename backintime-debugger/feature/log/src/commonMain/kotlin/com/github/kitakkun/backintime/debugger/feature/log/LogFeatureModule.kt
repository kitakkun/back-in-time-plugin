package com.github.kitakkun.backintime.debugger.feature.log

import com.github.kitakkun.backintime.debugger.feature.log.view.sessionlog.SessionLogViewModel
import com.github.kitakkun.backintime.debugger.feature.log.view.sessionlog.content.SessionLogContentViewModel
import org.koin.compose.viewmodel.dsl.viewModel
import org.koin.compose.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val logFeatureModule = module {
    viewModelOf(::SessionLogViewModel)
    viewModel { (sessionId: String) -> SessionLogContentViewModel(sessionId) }
}
