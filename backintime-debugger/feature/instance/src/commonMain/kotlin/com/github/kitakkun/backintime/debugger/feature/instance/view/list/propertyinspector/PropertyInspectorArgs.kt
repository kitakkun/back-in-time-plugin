package com.github.kitakkun.backintime.debugger.feature.instance.view.list.propertyinspector

data class PropertyInspectorArgs(
    val sessionId: String,
    val instanceId: String,
    val propertyName: String,
    val propertyOwnerClassName: String,
)
