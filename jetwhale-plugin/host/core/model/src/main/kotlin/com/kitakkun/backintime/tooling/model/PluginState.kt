package com.kitakkun.backintime.tooling.model

import kotlinx.serialization.Serializable

@Serializable
data class PluginState(
    val globalState: GlobalState,
    val settingsState: SettingsState,
    val inspectorState: InspectorState,
    val logState: LogState,
) {
    companion object {
        val Default = PluginState(
            globalState = GlobalState.Default,
            settingsState = SettingsState.Default,
            inspectorState = InspectorState.Default,
            logState = LogState.Default,
        )
    }
}

@Serializable
data class GlobalState(
    val activeTab: BackInTimeTab,
) {
    companion object {
        val Default: GlobalState = GlobalState(
            activeTab = BackInTimeTab.Inspector,
        )
    }
}

@Serializable
data class InspectorState(
    val selectedInstanceId: String?,
    val selectedPropertyKey: String?,
    val expandedInstanceIds: Set<String>,
    val horizontalSplitPanePosition: Float,
    val verticalSplitPanePosition: Float,
    val selectedEventId: String?,
) {
    companion object {
        val Default: InspectorState = InspectorState(
            selectedInstanceId = null,
            selectedPropertyKey = null,
            expandedInstanceIds = emptySet(),
            horizontalSplitPanePosition = 0.5f,
            verticalSplitPanePosition = 0.5f,
            selectedEventId = null,
        )
    }
}

@Serializable
data class LogState(
    val selectedEventId: String?,
    val verticalSplitPanePosition: Float,
) {
    companion object {
        val Default: LogState = LogState(
            selectedEventId = null,
            verticalSplitPanePosition = 0.5f,
        )
    }
}

@Serializable
data class SettingsState(
    val serverPort: Int,
) {
    companion object {
        val Default: SettingsState = SettingsState(50020)
    }
}

@Serializable
enum class BackInTimeTab {
    Inspector,
    Log,
    Settings,
}
