package com.kitakkun.backintime.tooling.flipper.selector

import com.kitakkun.backintime.tooling.flipper.FlipperAppState
import com.kitakkun.backintime.tooling.model.ClassInfo
import com.kitakkun.backintime.tooling.model.DependencyInfo
import com.kitakkun.backintime.tooling.model.InstanceInfo
import com.kitakkun.backintime.tooling.model.MethodCallInfo
import com.kitakkun.backintime.tooling.model.ValueChangeInfo
import com.kitakkun.backintime.tooling.model.ui.InstanceItem
import com.kitakkun.backintime.tooling.model.ui.InstanceListState
import com.kitakkun.backintime.tooling.model.ui.PersistentState
import com.kitakkun.backintime.tooling.model.ui.PropertyItem

@JsExport
fun selectInstanceListState(appState: FlipperAppState): InstanceListState {
    return instanceListStateProducer(
        instanceInfoList = appState.instanceInfoList,
        persistentState = appState.persistentState,
        classInfoList = appState.classInfoList,
        methodCallList = appState.methodCallInfoList,
        dependencyInfoList = appState.dependencyInfoList
    )
}

fun instanceListStateProducer(
    instanceInfoList: List<InstanceInfo>,
    classInfoList: List<ClassInfo>,
    methodCallList: List<MethodCallInfo>,
    persistentState: PersistentState,
    dependencyInfoList: List<DependencyInfo>,
): InstanceListState {
    val aliveInstances = instanceInfoList.filter { it.alive }

    val instances = aliveInstances.mapNotNull { instance ->
        resolveInstanceInfo(
            classInfoList = classInfoList,
            instanceInfoList = instanceInfoList,
            dependencyInfoList = dependencyInfoList,
            classSignature = instance.classSignature,
            instanceUUID = instance.uuid,
            allValueChangeEvents = methodCallList.filter { it.instanceUUID == instance.uuid }.flatMap { it.valueChanges }
        )
    }

    return InstanceListState(
        instances = instances,
        showNonDebuggableProperty = persistentState.showNonDebuggableProperty
    )
}

private fun resolveInstanceInfo(
    classInfoList: List<ClassInfo>,
    instanceInfoList: List<InstanceInfo>,
    dependencyInfoList: List<DependencyInfo>,
    classSignature: String,
    instanceUUID: String,
    allValueChangeEvents: List<ValueChangeInfo>,
): InstanceItem? {
    val classInfo = classInfoList.find { it.classSignature == classSignature } ?: return null
    val superTypeInfo = resolveInstanceInfo(
        classInfoList = classInfoList,
        instanceInfoList = instanceInfoList,
        dependencyInfoList = dependencyInfoList,
        classSignature = classInfo.superClassSignature,
        instanceUUID = instanceUUID,
        allValueChangeEvents = allValueChangeEvents
    )
    return InstanceItem(
        name = classInfo.classSignature,
        uuid = instanceUUID,
        superClassSignature = classInfo.superClassSignature,
        properties = classInfo.properties.map { property ->
            val dependingInstanceUUIDs = dependencyInfoList.find { it.uuid == instanceUUID }?.dependsOn.orEmpty()

            val propertyInstanceInfo = dependingInstanceUUIDs.map { dependingInstanceUUID ->
                instanceInfoList.find { it.uuid == dependingInstanceUUID }
            }.find { it?.classSignature == property.propertyType }

            PropertyItem(
                name = property.name,
                signature = property.signature,
                type = property.propertyType,
                debuggable = property.debuggable,
                eventCount = allValueChangeEvents.filter { it.propertySignature == property.signature }.size,
                stateHolderInstance = propertyInstanceInfo?.let {
                    resolveInstanceInfo(
                        classInfoList = classInfoList,
                        instanceInfoList = instanceInfoList,
                        dependencyInfoList = dependencyInfoList,
                        classSignature = property.propertyType,
                        instanceUUID = it.uuid,
                        allValueChangeEvents = allValueChangeEvents,
                    )
                }
            )
        },
        superInstanceItem = superTypeInfo,
    )
}