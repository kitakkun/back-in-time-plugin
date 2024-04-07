package com.github.kitakkun.backintime.debugger.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.github.kitakkun.backintime.debugger.data.coroutines.IOScope
import com.github.kitakkun.backintime.debugger.database.Instance
import com.github.kitakkun.backintime.debugger.database.InstanceQueries
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

interface InstanceRepository {
    fun selectActiveInstances(sessionId: String): Flow<List<Instance>>
    fun selectDeadInstances(sessionId: String): Flow<List<Instance>>
    suspend fun insert(id: String, className: String, registeredAt: Long, sessionId: String)
    suspend fun updateAlive(sessionId: String, id: String, alive: Boolean)
    suspend fun updateClassName(sessionId: String, id: String, className: String)
    suspend fun deleteAll(sessionId: String)
}

class InstanceRepositoryImpl(
    private val queries: InstanceQueries,
) : InstanceRepository, CoroutineScope by IOScope() {
    override fun selectActiveInstances(sessionId: String) = queries.selectAliveBySessionId(sessionId).asFlow().mapToList(coroutineContext)
    override fun selectDeadInstances(sessionId: String): Flow<List<Instance>> = queries.selectDeadBySessionId(sessionId).asFlow().mapToList(coroutineContext)

    override suspend fun insert(id: String, className: String, registeredAt: Long, sessionId: String) {
        withContext(coroutineContext) {
            queries.insert(
                id = id,
                className = className,
                registeredAt = registeredAt,
                alive = true,
                sessionId = sessionId,
            )
        }
    }

    override suspend fun updateAlive(sessionId: String, id: String, alive: Boolean) {
        withContext(coroutineContext) {
            queries.updateAlive(sessionId = sessionId, id = id, alive = alive)
        }
    }

    override suspend fun updateClassName(sessionId: String, id: String, className: String) {
        withContext(coroutineContext) {
            queries.updateClassName(sessionId = sessionId, id = id, className = className)
        }
    }

    override suspend fun deleteAll(sessionId: String) {
        withContext(coroutineContext) {
            queries.deleteBySessionId(sessionId)
        }
    }
}
