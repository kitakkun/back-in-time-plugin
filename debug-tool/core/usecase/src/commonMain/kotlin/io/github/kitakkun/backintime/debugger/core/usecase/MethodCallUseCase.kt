package io.github.kitakkun.backintime.debugger.core.usecase

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import io.github.kitakkun.backintime.debugger.core.model.MethodCall
import io.github.kitakkun.backintime.debugger.core.model.ValueChange
import io.github.kitakkun.backintime.debugger.core.usecase.compositionlocal.localMethodCallRepository
import io.github.kitakkun.backintime.debugger.core.usecase.compositionlocal.localValueChangeRepository

@Composable
fun methodCalls(
    sessionId: String,
    instanceId: String,
): List<MethodCall> {
    val methodCallRepository = localMethodCallRepository()
    val methodCallEntities by methodCallRepository.selectAsFlow(sessionId, instanceId).collectAsState(emptyList())
    val callAndValueChangeMap by rememberUpdatedState(
        methodCallEntities.associateWith { valueChanges(callId = it.id, sessionId = it.sessionId, instanceId = it.instanceId) }
    )

    return callAndValueChangeMap.map { (methodCall, valueChanges) ->
        MethodCall(
            valueChanges = valueChanges,
            calledAt = methodCall.calledAt,
        )
    }
}

@Composable
private fun valueChanges(
    callId: String,
    sessionId: String,
    instanceId: String,
): List<ValueChange> {
    val valueChangeRepository = localValueChangeRepository()
    val valueChangeEntity by valueChangeRepository.selectAsFlow(
        callId = callId,
        instanceId = instanceId,
        sessionId = sessionId
    ).collectAsState(emptyList())
    val valueChanges by rememberUpdatedState(
        valueChangeEntity.map {
            ValueChange(
                propertyOwnerClassName = it.propertyOwnerClassName,
                propertyName = it.propertyName,
                newValue = it.newValue,
            )
        }
    )
    return valueChanges
}
