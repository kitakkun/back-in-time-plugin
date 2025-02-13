package com.kitakkun.backintime.tooling.flipper

import com.kitakkun.backintime.tooling.model.ClassInfo
import com.kitakkun.backintime.tooling.model.DependencyInfo
import com.kitakkun.backintime.tooling.model.InstanceInfo
import com.kitakkun.backintime.tooling.model.MethodCallInfo
import com.kitakkun.backintime.tooling.model.ui.PersistentState

@JsExport
data class FlipperAppState(
    val activeTab: FlipperTab,
    val events: List<BackInTimeEventData>,
    val classInfoList: List<ClassInfo>,
    val instanceInfoList: List<InstanceInfo>,
    val methodCallInfoList: List<MethodCallInfo>,
    val dependencyInfoList: List<DependencyInfo>,
    val persistentState: PersistentState,
    val tabState: TabState,
) {
    companion object {
        val Default = FlipperAppState(
            activeTab = FlipperTab.Instances,
            events = emptyList(),
            classInfoList = emptyList(),
            instanceInfoList = emptyList(),
            methodCallInfoList = emptyList(),
            dependencyInfoList = emptyList(),
            persistentState = PersistentState.Default,
            tabState = TabState.None,
        )
    }
}
