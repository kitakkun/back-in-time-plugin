package com.kitakkun.backintime.tooling.flipper.selector

import com.kitakkun.backintime.tooling.flipper.TabState
import com.kitakkun.backintime.tooling.flipper.useAppState

@JsExport
fun selectTabState(): TabState {
    return useAppState().tabState
}

@JsExport
fun selectInstanceTabState(): TabState.InstanceTabState? {
    return useAppState().tabState.let {
        if (it is TabState.InstanceTabState) it
        else null
    }
}

@JsExport
fun selectLogTabState(): TabState.LogTabState? {
    return useAppState().tabState.let {
        if (it is TabState.LogTabState) it
        else null
    }
}
