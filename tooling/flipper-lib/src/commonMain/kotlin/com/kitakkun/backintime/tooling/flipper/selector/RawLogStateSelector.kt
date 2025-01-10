package com.kitakkun.backintime.tooling.flipper.selector

import com.benasher44.uuid.uuid4
import com.kitakkun.backintime.tooling.flipper.FlipperAppState
import com.kitakkun.backintime.tooling.model.RawEventLog
import com.kitakkun.backintime.tooling.model.ui.RawLogState

@JsExport
fun selectRawLogState(appState: FlipperAppState): RawLogState {
    return RawLogState(
        logs = appState.events.map {
            RawEventLog(
                eventId = uuid4().toString(),
                time = "", // TODO
                label = "", // TODO
                payload = it,
            )
        },
        selectedLogId = null,
    )
}