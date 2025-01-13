package com.kitakkun.backintime.tooling.database

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.kitakkun.backintime.tooling.model.InstanceEventData
import com.kitakkun.backintime.tooling.shared.BackInTimeDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class BackInTimeDatabaseImpl : BackInTimeDatabase {
    companion object {
        val instance: BackInTimeDatabaseImpl by lazy { BackInTimeDatabaseImpl() }
    }

    init {
        // Fix no suitable driver found for jdbc:sqlite:
        // FYI: https://stackoverflow.com/questions/16725377/unable-to-connect-to-database-no-suitable-driver-found
        Class.forName("org.sqlite.JDBC")
    }

    private val ioDispatcher = Dispatchers.IO

    private val database = createDatabase()
    private val instanceEventQueries: InstanceEventQueries = database.instanceEventQueries

    override fun saveEvent(sessionId: String, event: InstanceEventData) {
        instanceEventQueries.insert(event.eventId, sessionId, event.instanceId, event)
    }

    override fun selectEventsForInstance(instanceId: String): Flow<List<InstanceEventData>> {
        return instanceEventQueries.selectByInstanceId(instanceId).asFlow()
            .mapToList(ioDispatcher)
            .map { events ->
                events.mapNotNull { it.event }
            }
    }

    override fun getAllEventsAsFlow(): Flow<List<InstanceEventData>> {
        return instanceEventQueries.selectAll()
            .asFlow()
            .mapToList(ioDispatcher)
            .map { events ->
                events.mapNotNull { it.event }
            }
    }

    override fun selectEventsForSession(sessionId: String?): Flow<List<InstanceEventData>> {
        return if (sessionId == null) {
            getAllEventsAsFlow()
        } else {
            instanceEventQueries.selectBySessionId(sessionId)
                .asFlow()
                .mapToList(ioDispatcher)
                .map { events ->
                    events.mapNotNull { it.event }
                }
        }
    }

    override fun allInstanceIdForSessionAsFlow(sessionId: String): Flow<List<String>> {
        return instanceEventQueries.selectAllInstanceId(sessionId)
            .asFlow()
            .mapToList(Dispatchers.IO)
    }

    override fun allEventsForInstanceAsFlow(sessionId: String, instanceId: String): Flow<List<InstanceEventData>> {
        return instanceEventQueries.selectAllForInstance(sessionId, instanceId)
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { events ->
                events.mapNotNull { it.event }
            }
    }
}
