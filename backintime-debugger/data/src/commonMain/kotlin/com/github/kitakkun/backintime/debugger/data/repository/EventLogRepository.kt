package com.github.kitakkun.backintime.debugger.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.github.kitakkun.backintime.debugger.data.coroutines.IOScope
import com.github.kitakkun.backintime.debugger.database.EventLog
import com.github.kitakkun.backintime.debugger.database.EventLogQueries
import com.github.kitakkun.backintime.runtime.backInTimeJson
import com.github.kitakkun.backintime.runtime.event.BackInTimeDebugServiceEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import java.util.UUID

interface EventLogRepository {
    fun logFlow(sessionId: String): Flow<List<EventLog>>
    suspend fun insert(sessionId: String, event: BackInTimeDebugServiceEvent)
    suspend fun deleteAll(sessionId: String)
}

class EventLogRepositoryImpl(private val queries: EventLogQueries) : CoroutineScope by IOScope(), EventLogRepository {
    override fun logFlow(sessionId: String): Flow<List<EventLog>> = queries.selectSessionEvents(sessionId).asFlow().mapToList(coroutineContext)

    override suspend fun insert(sessionId: String, event: BackInTimeDebugServiceEvent) {
        withContext(coroutineContext) {
            queries.insert(
                id = UUID.randomUUID().toString(),
                sessionId = sessionId,
                kind = event.javaClass.name,
                payload = backInTimeJson.encodeToString(event),
                createdAt = System.currentTimeMillis(),
            )
        }
    }

    override suspend fun deleteAll(sessionId: String) {
        withContext(coroutineContext) {
            queries.deleteSessionEvents(sessionId)
        }
    }
}
