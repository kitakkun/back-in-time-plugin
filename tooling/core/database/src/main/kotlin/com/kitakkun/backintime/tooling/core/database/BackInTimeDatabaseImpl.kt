package com.kitakkun.backintime.tooling.core.database

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.kitakkun.backintime.tooling.core.shared.BackInTimeDatabase
import com.kitakkun.backintime.tooling.model.EventEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import java.io.File

class BackInTimeDatabaseImpl private constructor() : BackInTimeDatabase {
    companion object {
        val instance by lazy { BackInTimeDatabaseImpl() }
    }

    private val mutableStateFlow: MutableStateFlow<BackInTimeDatabase.State> = MutableStateFlow(BackInTimeDatabase.State.RunningInMemory)
    override val stateFlow: StateFlow<BackInTimeDatabase.State> = mutableStateFlow

    private var database = createDatabase()
    private val queries get() = database.eventQueries
    private val ioDispatcher = Dispatchers.IO

    override fun restartDatabase(filePath: String, migrate: Boolean) {
        val dbFile = File(filePath)
        if (!dbFile.exists()) dbFile.createNewFile()
        val newDatabase = createDatabase("jdbc:sqlite:$filePath")
        if (migrate) {
            val prevDatabase = database
            prevDatabase.eventQueries.selectAll().executeAsList().forEach {
                newDatabase.eventQueries.insert(
                    id = it.id,
                    sessionId = it.sessionId,
                    instanceId = it.instanceId,
                    time = it.time,
                    event = it.event,
                )
            }
        }
        database = newDatabase
        mutableStateFlow.update { BackInTimeDatabase.State.RunningWithFile(filePath) }
    }

    override fun insert(eventEntity: EventEntity) {
        queries.insert(
            id = eventEntity.eventId,
            sessionId = eventEntity.sessionId,
            instanceId = eventEntity.instanceId,
            time = eventEntity.time,
            event = eventEntity,
        )
    }

    override fun selectForSession(sessionId: String): Flow<List<EventEntity>> {
        return queries.selectBySessionId(sessionId)
            .asFlow()
            .mapToList(ioDispatcher)
            .map { events -> events.map { it.event } }
    }

    override fun selectForInstance(sessionId: String, instanceId: String): Flow<List<EventEntity>> {
        return queries
            .selectByInstanceId(
                instanceId = instanceId,
                sessionId = sessionId,
            )
            .asFlow()
            .mapToList(ioDispatcher)
            .map { events ->
                events.map { it.event }
            }
    }

    override fun selectInstanceIds(sessionId: String): Flow<List<String>> {
        return queries
            .selectAllInstanceId(sessionId)
            .asFlow()
            .mapToList(ioDispatcher)
            .map { instanceIds ->
                instanceIds.mapNotNull { it.instanceId }
            }
    }
}