package com.github.kitakkun.backintime.debugger.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.github.kitakkun.backintime.debugger.database.SessionInfo
import com.github.kitakkun.backintime.debugger.database.SessionInfoQueries
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock

interface SessionInfoRepository {
    val allConnectedSessions: Flow<List<SessionInfo>>
    val allDisconnectedSessions: Flow<List<SessionInfo>>
    suspend fun insert(sessionId: String, label: String? = null, startedAt: Long = Clock.System.now().epochSeconds)
    suspend fun select(sessionId: String): SessionInfo?
    suspend fun markAsConnected(id: String)
    suspend fun markAsDisconnected(id: String)
    suspend fun markAllAsDisconnected()
}

class SessionInfoRepositoryImpl(
    private val queries: SessionInfoQueries,
) : SessionInfoRepository {
    private val dispatcher = Dispatchers.IO

    override val allConnectedSessions = queries.selectAllConnectedSessions().asFlow().mapToList(dispatcher)
    override val allDisconnectedSessions = queries.selectAllDisconnectedSessions().asFlow().mapToList(dispatcher)

    override suspend fun markAllAsDisconnected() {
        withContext(dispatcher) {
            queries.markAllAsDisconnected()
        }
    }

    override suspend fun select(sessionId: String): SessionInfo? {
        return withContext(dispatcher) {
            queries.selectById(sessionId).executeAsOneOrNull()
        }
    }

    override suspend fun insert(sessionId: String, label: String?, startedAt: Long) {
        withContext(dispatcher) {
            queries.insert(
                id = sessionId,
                label = label ?: sessionId,
                startedAt = startedAt,
            )
        }
    }

    override suspend fun markAsConnected(id: String) {
        withContext(dispatcher) {
            queries.markAsConnected(id)
        }
    }

    override suspend fun markAsDisconnected(id: String) {
        withContext(dispatcher) {
            queries.markAsDisconnected(id)
        }
    }
}
