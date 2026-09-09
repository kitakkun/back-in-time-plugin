package com.kitakkun.backintime.tooling.model.ui

data class InstanceListState(
    val instances: List<InstanceItem>,
    val showNonDebuggableProperty: Boolean,
)
