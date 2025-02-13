package com.kitakkun.backintime.tooling.model.ui

import com.kitakkun.backintime.tooling.model.InstanceInfo
import com.kitakkun.backintime.tooling.model.PropertyInfo
import kotlin.js.JsExport

@JsExport
data class PropertyInspectorState(
    val instanceInfo: InstanceInfo,
    val propertyInfo: PropertyInfo,
    val valueChanges: List<ValueChangeInfo>,
)
