package com.kitakkun.backintime.tooling.flipper.selector

import com.kitakkun.backintime.tooling.flipper.useAppState
import com.kitakkun.backintime.tooling.model.ui.ValueEmitModalPageState

@JsExport
fun selectValueEmitModalPageState(
    instanceId: String,
    methodCallId: String,
): ValueEmitModalPageState {
    val appState = useAppState()

    val instanceInfo = appState.instanceInfoList.first { it.uuid == instanceId }
    val classInfo = appState.classInfoList.first { it.classSignature == instanceInfo.classSignature }
    val methodCallInfo = appState.methodCallInfoList.first { it.callUUID == methodCallId }

    return ValueEmitModalPageState(
        classInfo = classInfo,
        instanceInfo = instanceInfo,
        methodCallInfo = methodCallInfo,
    )
}