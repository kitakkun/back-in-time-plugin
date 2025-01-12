package com.kitakkun.backintime.tooling.flipper.selector

import com.kitakkun.backintime.tooling.flipper.FlipperAppState
import com.kitakkun.backintime.tooling.model.RawEventLog
import com.kitakkun.backintime.tooling.model.ui.RawLogState

@JsExport
fun selectRawLogState(appState: FlipperAppState): RawLogState {
    return RawLogState(
        logs = appState.events.map {
            RawEventLog(
                eventId = it.uuid,
                time = it.time.toString(),
                label = it.label,
                payload = it.payload,
            )
        },
        selectedLogId = null,
    )
}