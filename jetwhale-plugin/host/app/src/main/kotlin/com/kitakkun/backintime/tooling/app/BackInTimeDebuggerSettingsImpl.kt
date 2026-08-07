package com.kitakkun.backintime.tooling.app

import com.kitakkun.backintime.tooling.core.shared.BackInTimeDebuggerSettings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * Plain in-memory settings holder. Persisting it is the host plugin's job — only the plugin has
 * access to JetWhale's per-plugin storage.
 */
class BackInTimeDebuggerSettingsImpl : BackInTimeDebuggerSettings {
    private val mutableStateFlow = MutableStateFlow(BackInTimeDebuggerSettings.State())
    override val stateFlow = mutableStateFlow.asStateFlow()

    override fun update(block: (BackInTimeDebuggerSettings.State) -> BackInTimeDebuggerSettings.State) {
        mutableStateFlow.update(block)
    }
}
