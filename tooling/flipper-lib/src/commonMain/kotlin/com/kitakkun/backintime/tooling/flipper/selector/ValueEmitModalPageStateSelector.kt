package com.kitakkun.backintime.tooling.flipper.selector

import com.kitakkun.backintime.tooling.flipper.FlipperAppState
import com.kitakkun.backintime.tooling.model.ui.ValueEmitModalPageState

@JsExport
fun selectValueEmitModalPageState(
    appState: FlipperAppState,
    instanceId: String,
    methodCallId: String,
): ValueEmitModalPageState {
    val instanceInfo = appState.instanceInfoList.first { it.uuid == instanceId }
    val classInfo = appState.classInfoList.first { it.classSignature == instanceInfo.classSignature }
    val methodCallInfo = appState.methodCallInfoList.first { it.callUUID == methodCallId }

    return ValueEmitModalPageState(
        classInfo = classInfo,
        instanceInfo = instanceInfo,
        methodCallInfo = methodCallInfo,
    )
}