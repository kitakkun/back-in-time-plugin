package com.github.kitakkun.backintime.debugger.data.repository

import app.cash.sqldelight.ColumnAdapter
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOneOrNull
import com.github.kitakkun.backintime.debugger.database.Instance
import com.github.kitakkun.backintime.debugger.database.InstanceQueries
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import org.koin.core.annotation.Singleton

interface InstanceRepository {
    fun selectInstanceAsFlow(sessionId: String, instanceId: String): Flow<Instance?>
    fun selectInstancesFlow(sessionId: String): Flow<List<Instance>>
    fun selectActiveInstances(sessionId: String): Flow<List<Instance>>
    fun selectDeadInstances(sessionId: String): Flow<List<Instance>>
    suspend fun select(sessionId: String, instanceId: String): Instance?
    suspend fun insert(id: String, className: String, registeredAt: Long, sessionId: String)
    suspend fun updateAlive(sessionId: String, id: String, alive: Boolean)
    suspend fun updateClassName(sessionId: String, id: String, className: String)
    suspend fun deleteAll(sessionId: String)
    suspend fun addChildInstance(sessionId: String, parentId: String, childId: String)
}

@Singleton(binds = [InstanceRepository::class])
class InstanceRepositoryImpl(
    private val queries: InstanceQueries,
) : InstanceRepository {
    private val dispatcher = Dispatchers.IO

    override fun selectInstanceAsFlow(sessionId: String, instanceId: String): Flow<Instance?> = queries.select(id = instanceId, sessionId = sessionId).asFlow().mapToOneOrNull(dispatcher)
    override fun selectInstancesFlow(sessionId: String): Flow<List<Instance>> = queries.selectBySessionId(sessionId).asFlow().mapToList(dispatcher)
    override fun selectActiveInstances(sessionId: String) = queries.selectAliveBySessionId(sessionId).asFlow().mapToList(dispatcher)
    override fun selectDeadInstances(sessionId: String): Flow<List<Instance>> = queries.selectDeadBySessionId(sessionId).asFlow().mapToList(dispatcher)

    override suspend fun insert(id: String, className: String, registeredAt: Long, sessionId: String) {
        withContext(dispatcher) {
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
        withContext(dispatcher) {
            queries.updateAlive(sessionId = sessionId, id = id, alive = alive)
        }
    }

    override suspend fun updateClassName(sessionId: String, id: String, className: String) {
        withContext(dispatcher) {
            queries.updateClassName(sessionId = sessionId, id = id, className = className)
        }
    }

    override suspend fun deleteAll(sessionId: String) {
        withContext(dispatcher) {
            queries.deleteBySessionId(sessionId)
        }
    }

    override suspend fun addChildInstance(sessionId: String, parentId: String, childId: String) {
        withContext(dispatcher) {
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

    override suspend fun select(sessionId: String, instanceId: String): Instance? {
        return withContext(dispatcher) {
            queries.select(id = instanceId, sessionId = sessionId).executeAsOneOrNull()
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
