package com.kitakkun.backintime.tooling.flipper.selector

import com.kitakkun.backintime.tooling.flipper.FlipperAppState
import com.kitakkun.backintime.tooling.flipper.TabState

@JsExport
fun selectTabState(appState: FlipperAppState): TabState {
    return appState.tabState
}

@JsExport
fun selectInstanceTabState(appState: FlipperAppState): TabState.InstanceTabState? {
    return appState.tabState.let {
        if (it is TabState.InstanceTabState) it
        else null
    }
}

@JsExport
fun selectLogTabState(appState: FlipperAppState): TabState.LogTabState? {
    return appState.tabState.let {
        if (it is TabState.LogTabState) it
        else null
    }
}
