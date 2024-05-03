package com.github.kitakkun.backintime.debugger.featurecommon.view.sessionselect

import com.github.kitakkun.backintime.debugger.featurecommon.view.sessionselect.component.OpenableSessionBindModel

sealed class SessionSelectBindModel {
    data object Loading : SessionSelectBindModel()
    data class Loaded(
        val openableSessions: List<OpenableSessionBindModel>,
    ) : SessionSelectBindModel()

    data object Empty : SessionSelectBindModel()
}
