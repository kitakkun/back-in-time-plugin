package com.github.kitakkun.backintime.debugger.data.repository

import app.cash.sqldelight.ColumnAdapter
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
    suspend fun addChildInstance(sessionId: String, parentId: String, childId: String)
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
                childInstanceIds = emptyList(),
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

    override suspend fun addChildInstance(sessionId: String, parentId: String, childId: String) {
        withContext(coroutineContext) {
            val childInstanceIds = queries.select(
                id = parentId,
                sessionId = sessionId,
            ).executeAsOne().childInstanceIds.toMutableList()
            childInstanceIds.add(childId)
            queries.updateChildInstanceIds(
                id = parentId,
                sessionId = sessionId,
                childInstanceIds = childInstanceIds,
            )
        }
    }
}

val listOfStringAdapter = object : ColumnAdapter<List<String>, String> {
    override fun encode(value: List<String>): String {
        return value.joinToString(",")
    }

    override fun decode(databaseValue: String): List<String> {
        return databaseValue.split(",")
    }
}
