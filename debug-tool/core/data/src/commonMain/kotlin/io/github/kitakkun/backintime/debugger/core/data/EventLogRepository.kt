package io.github.kitakkun.backintime.debugger.core.data

import com.benasher44.uuid.uuid4
import io.github.kitakkun.backintime.core.websocket.event.BackInTimeDebugServiceEvent
import io.github.kitakkun.backintime.debugger.core.database.dao.EventLogDao
import io.github.kitakkun.backintime.debugger.core.database.model.EventLogEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Clock

interface EventLogRepository {
    fun logFlow(sessionId: String): Flow<List<EventLogEntity>>
    suspend fun insert(sessionId: String, event: BackInTimeDebugServiceEvent)
    suspend fun deleteAll(sessionId: String)
}

class EventLogRepositoryImpl(private val dao: EventLogDao) : EventLogRepository {
    override fun logFlow(sessionId: String): Flow<List<EventLogEntity>> = dao.selectAsFlow(sessionId)

    override suspend fun insert(sessionId: String, event: BackInTimeDebugServiceEvent) {
        dao.insert(
            EventLogEntity(
                id = uuid4().toString(),
                sessionId = sessionId,
                payload = event,
                createdAt = Clock.System.now().epochSeconds,
            )
        )
    }

    override suspend fun deleteAll(sessionId: String) = dao.deleteAll(sessionId)
}
