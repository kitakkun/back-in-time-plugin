package com.kitakkun.backintime.tooling.shared

import com.kitakkun.backintime.tooling.model.InstanceEventData
import kotlinx.coroutines.flow.Flow

interface BackInTimeDatabase {
    fun saveEvent(sessionId: String, event: InstanceEventData)
    fun selectEventsForInstance(instanceId: String): Flow<List<InstanceEventData>>
    fun getAllEventsAsFlow(): Flow<List<InstanceEventData>>
    fun selectEventsForSession(sessionId: String?): Flow<List<InstanceEventData>>
    fun allInstanceIdForSessionAsFlow(sessionId: String): Flow<List<String>>
    fun allEventsForInstanceAsFlow(sessionId: String, instanceId: String): Flow<List<InstanceEventData>>
}
