package com.kitakkun.backintime.tooling.flipper.selector

import com.kitakkun.backintime.tooling.flipper.useAppState
import com.kitakkun.backintime.tooling.model.ClassInfo
import com.kitakkun.backintime.tooling.model.PropertyInfo
import com.kitakkun.backintime.tooling.model.ui.PropertyInspectorState
import com.kitakkun.backintime.tooling.model.ui.ValueChangeInfo

@JsExport
fun selectPropertyInspectorState(
    instanceUUID: String,
    propertySignature: String,
): PropertyInspectorState? {
    val appState = useAppState()

    val instanceInfo = appState.instanceInfoList.find { it.uuid == instanceUUID } ?: return null
    val propertyInfo = getPropertyInfoRecursively(classInfoList = appState.classInfoList, instanceInfo.classSignature)
        .find { it.signature == propertySignature } ?: return null

    val valueChanges = appState.methodCallInfoList.filter {
        it.instanceUUID == instanceUUID && it.valueChanges.any { it.propertySignature == propertySignature }
    }.map {
        ValueChangeInfo(
            methodCallUUID = it.callUUID,
            time = it.calledAt,
            value = it.valueChanges.reversed().find { it.propertySignature == propertySignature }?.value.orEmpty()
        )
    }

    return PropertyInspectorState(
        instanceInfo = instanceInfo,
        propertyInfo = propertyInfo,
        valueChanges = valueChanges,
    )
}

private fun getPropertyInfoRecursively(
    classInfoList: List<ClassInfo>,
    classSignature: String,
): List<PropertyInfo> {
    val classInfo = classInfoList.find { it.classSignature == classSignature }
    if (classInfo == null) return emptyList()
    return classInfo.properties + getPropertyInfoRecursively(classInfoList, classInfo.superClassSignature)
}