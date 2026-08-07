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
    // Defaulted so state persisted before this pane was adjustable still decodes. The timeline needs
    // far less room than the event details below it, hence 0.35 rather than an even split.
    val historySplitPanePosition: Float = 0.35f,
    val selectedEventId: String?,
) {
    companion object {
        val Default: InspectorState = InspectorState(
            selectedInstanceId = null,
            selectedPropertyKey = null,
            expandedInstanceIds = emptySet(),
            horizontalSplitPanePosition = 0.5f,
            verticalSplitPanePosition = 0.5f,
            historySplitPanePosition = 0.35f,
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
