package com.kitakkun.backintime.tooling.standalone

import com.kitakkun.backintime.tooling.core.shared.BackInTimeDebuggerSettings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.serialization.json.Json
import java.io.File

class StandaloneDebuggerSettings : BackInTimeDebuggerSettings {
    private val settingsFile = File(System.getProperty("user.home"), ".backintime/settings.json")

    private val mutableStateFlow = MutableStateFlow(loadPersistedState())
    override val stateFlow = mutableStateFlow.asStateFlow()

    private fun loadPersistedState(): BackInTimeDebuggerSettings.State {
        return try {
            Json.decodeFromString(settingsFile.readText())
        } catch (_: Exception) {
            BackInTimeDebuggerSettings.State()
        }
    }

    override fun update(block: (BackInTimeDebuggerSettings.State) -> BackInTimeDebuggerSettings.State) {
        mutableStateFlow.update(block)
        settingsFile.parentFile.mkdirs()
        settingsFile.writeText(Json.encodeToString(stateFlow.value))
    }
} 