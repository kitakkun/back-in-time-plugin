@file:JsExport

package com.kitakkun.backintime.tooling.flipper

@JsExport
sealed class TabState {
    data object None : TabState()

    data class InstanceTabState(
        val selectedInstanceId: String?,
        val selectedPropertyId: String?,
    ) : TabState()

    data class LogTabState(
        val selectedEvent: String?,
    ) : TabState()
}
