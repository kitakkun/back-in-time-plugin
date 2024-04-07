package com.github.kitakkun.backintime.debugger.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.github.kitakkun.backintime.debugger.data.coroutines.IOScope
import com.github.kitakkun.backintime.debugger.database.SessionInfo
import com.github.kitakkun.backintime.debugger.database.SessionInfoQueries
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

interface SessionInfoRepository {
    val allConnectedSessions: Flow<List<SessionInfo>>
    val allDisconnectedSessions: Flow<List<SessionInfo>>
    suspend fun insert(sessionId: String, label: String? = null, startedAt: Long = System.currentTimeMillis())
    suspend fun select(sessionId: String): SessionInfo?
}

class SessionInfoRepositoryImpl(
    private val queries: SessionInfoQueries,
) : SessionInfoRepository, CoroutineScope by IOScope() {
    override val allConnectedSessions = queries.selectAllConnectedSessions().asFlow().mapToList(coroutineContext)
    override val allDisconnectedSessions = queries.selectAllDisconnectedSessions().asFlow().mapToList(coroutineContext)

    override suspend fun select(sessionId: String): SessionInfo? {
        return withContext(coroutineContext) {
            queries.selectById(sessionId).executeAsOneOrNull()
        }
    }

    override suspend fun insert(sessionId: String, label: String?, startedAt: Long) {
        withContext(coroutineContext) {
            queries.insert(
                id = sessionId,
                label = label ?: sessionId,
                startedAt = System.currentTimeMillis(),
            )
        }
    }
}
