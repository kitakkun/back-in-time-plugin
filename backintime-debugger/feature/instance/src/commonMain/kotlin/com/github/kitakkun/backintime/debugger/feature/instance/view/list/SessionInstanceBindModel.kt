package com.github.kitakkun.backintime.debugger.feature.instance.view.list

import com.github.kitakkun.backintime.debugger.feature.instance.view.list.component.InstanceBindModel

sealed interface SessionInstanceBindModel {
    data object Loading : SessionInstanceBindModel
    data class InstancesAvailable(val instanceBindModels: List<InstanceBindModel>) : SessionInstanceBindModel
    data object NoInstanceRegistered : SessionInstanceBindModel
}
