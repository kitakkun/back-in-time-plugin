package com.kitakkun.backintime.tooling.core.ui.compositionlocal

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.staticCompositionLocalOf
import com.kitakkun.backintime.tooling.core.shared.BackInTimeDebuggerService
import com.kitakkun.backintime.tooling.core.shared.BackInTimeDebuggerSettings
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

/**
 * Identifies the debug session this composition renders.
 *
 * The database keeps every session's events side by side, so every read has to be scoped by it. A
 * JetWhale host plugin instance lives exactly as long as one session, so the plugin provides its own
 * id here once and the screens never have to pass it around.
 */
val LocalSessionId = staticCompositionLocalOf<String> {
    error("No session id provided!")
}
