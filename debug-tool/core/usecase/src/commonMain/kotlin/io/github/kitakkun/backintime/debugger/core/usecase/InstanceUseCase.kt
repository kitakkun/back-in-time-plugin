package io.github.kitakkun.backintime.debugger.core.usecase

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import io.github.kitakkun.backintime.debugger.core.model.Instance
import io.github.kitakkun.backintime.debugger.core.usecase.compositionlocal.localInstanceRepository

@Composable
fun instances(
    sessionId: String,
): List<Instance> {
    val instanceRepository = localInstanceRepository()
    val instanceEntities by instanceRepository.selectInstancesFlow(sessionId).collectAsState(emptyList())

    val instanceEntityToClassInfoMap by rememberUpdatedState(
        instanceEntities.associateWith { classInfo(sessionId, it.className) }
    )
    val instanceEntityToEventsMap by rememberUpdatedState(
        instanceEntities.associateWith { methodCalls(sessionId = sessionId, instanceId = it.id) }
    )

    return instanceEntityToClassInfoMap.mapNotNull { (entity, classInfo) ->
        if (classInfo == null) return@mapNotNull null
        Instance(
            id = entity.id,
            classInfo = classInfo,
            isAlive = entity.isAlive,
            events = instanceEntityToEventsMap[entity] ?: emptyList(),
        )
    }
}
