package com.kitakkun.backintime.tooling.core.usecase

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import com.kitakkun.backintime.tooling.model.EventEntity
import com.kitakkun.backintime.tooling.model.Instance
import com.kitakkun.backintime.tooling.model.Property
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map

@OptIn(ExperimentalCoroutinesApi::class)
@Composable
fun rememberInstances(sessionId: String?): State<List<Instance>> {
    val database = LocalDatabase.current

    val allInstanceIdsFlow = remember(sessionId) {
        database.selectInstanceIds(sessionId ?: "")
    }
    val eachInstanceEventsFlow = remember(allInstanceIdsFlow) {
        allInstanceIdsFlow.flatMapLatest { instanceIds ->
            combine(instanceIds.map { database.selectForInstance(sessionId = sessionId ?: "", instanceId = it) }) {
                it.toList()
            }
        }
    }

    return remember(eachInstanceEventsFlow) {
        eachInstanceEventsFlow.map {
            it.mapNotNull { events ->
                val registerEvent = events.filterIsInstance<EventEntity.Instance.Register>().firstOrNull() ?: return@mapNotNull null
                Instance(
                    id = registerEvent.instanceId,
                    className = registerEvent.classInfo.classSignature,
                    superClassName = registerEvent.classInfo.superClassSignature,
                    properties = registerEvent.classInfo.properties.map { property ->
                        Property(
                            name = property.name,
                            type = property.propertyType,
                            totalEvents = events.count { event -> event is EventEntity.Instance.StateChange && event.propertySignature == property.signature },
                            debuggable = property.debuggable,
                        )
                    },
                    events = events,
                )
            }
        }
    }.collectAsState(emptyList())
}