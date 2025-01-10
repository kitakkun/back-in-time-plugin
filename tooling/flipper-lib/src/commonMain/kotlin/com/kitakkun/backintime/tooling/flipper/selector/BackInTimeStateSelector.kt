package com.kitakkun.backintime.tooling.flipper.selector

import com.kitakkun.backintime.tooling.flipper.FlipperAppState
import com.kitakkun.backintime.tooling.model.ui.BackInTimeState
import com.kitakkun.backintime.tooling.model.ui.HistoryInfo

@JsExport
fun selectBackInTimeState(
    appState: FlipperAppState,
    open: Boolean,
    instanceUUID: String,
): BackInTimeState {
    val instanceInfo = appState.instanceInfoList.find { it.uuid == instanceUUID }

    val registerEvent = HistoryInfo.RegisterHistoryInfo(
        subtitle = instanceUUID,
        description = instanceInfo?.classSignature ?: "",
        timestamp = instanceInfo?.registeredAt ?: 0,
    )

    val methodCallEvents = appState.methodCallInfoList
        .filter { it.instanceUUID == instanceUUID }
        .map {
            HistoryInfo.MethodCallHistoryInfo(
                it.methodSignature,
                it.calledAt,
                it.valueChanges.joinToString(", ") { "${it.propertySignature} = ${it.value}" },
                it.valueChanges,
            )
        }

    return BackInTimeState(
        open = open,
        instanceUUID = instanceUUID,
        histories = listOf(registerEvent) + methodCallEvents,
    )
}