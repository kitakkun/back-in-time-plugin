package com.kitakkun.backintime.tooling.model.ui

import com.kitakkun.backintime.tooling.model.ClassInfo
import com.kitakkun.backintime.tooling.model.InstanceInfo
import com.kitakkun.backintime.tooling.model.MethodCallInfo
import kotlin.js.JsExport

@JsExport
data class ValueEmitModalPageState(
    val classInfo: ClassInfo,
    val instanceInfo: InstanceInfo,
    val methodCallInfo: MethodCallInfo,
)
