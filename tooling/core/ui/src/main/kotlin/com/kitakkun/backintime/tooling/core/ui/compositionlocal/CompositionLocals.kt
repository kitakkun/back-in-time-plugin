package com.kitakkun.backintime.tooling.core.ui.compositionlocal

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.staticCompositionLocalOf
import com.kitakkun.backintime.tooling.core.shared.BackInTimeDebuggerService
import com.kitakkun.backintime.tooling.core.shared.BackInTimeDebuggerSettings
import com.kitakkun.backintime.tooling.core.shared.IDENavigator
import com.kitakkun.backintime.tooling.core.shared.PluginStateService

val LocalPluginStateService = staticCompositionLocalOf<PluginStateService> {
    error("No PluginStateProvider specified via composition local!")
}

val LocalSettings = compositionLocalOf<BackInTimeDebuggerSettings> {
    error("No BackInTimeDebuggerSettings provided!")
}

val LocalServer = compositionLocalOf<BackInTimeDebuggerService> {
    error("No BackInTimeDebuggerService provided!")
}

val LocalIDENavigator = compositionLocalOf<IDENavigator> {
    error("No IDENavigator provided!")
}
