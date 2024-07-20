package io.github.kitakkun.backintime.debugger.core.data

import io.github.kitakkun.backintime.debugger.core.database.dao.InstanceDao
import io.github.kitakkun.backintime.debugger.core.database.model.InstanceEntity
import kotlinx.coroutines.flow.Flow

interface InstanceRepository {
    fun selectInstancesFlow(sessionId: String): Flow<List<InstanceEntity>>
    suspend fun select(sessionId: String, instanceId: String): InstanceEntity?
    suspend fun insert(id: String, className: String, registeredAt: Long, sessionId: String)
    suspend fun updateAlive(sessionId: String, id: String, alive: Boolean)
    suspend fun updateClassName(sessionId: String, id: String, className: String)
    suspend fun deleteAll(sessionId: String)
    suspend fun addChildInstance(sessionId: String, parentId: String, childId: String)
}

class InstanceRepositoryImpl(private val dao: InstanceDao) : InstanceRepository {
    override fun selectInstancesFlow(sessionId: String): Flow<List<InstanceEntity>> = dao.getInstancesAsFlow(sessionId)

    override suspend fun insert(id: String, className: String, registeredAt: Long, sessionId: String) {
        dao.insert(
            InstanceEntity(
                id = id,
                sessionId = sessionId,
                className = className,
                registeredAt = registeredAt,
                isAlive = true,
                referencingInstanceIds = emptyList(),
            )
        )
    }

    override suspend fun updateAlive(sessionId: String, id: String, alive: Boolean) {
        dao.updateAlive(sessionId, id, alive)
    }

    override suspend fun updateClassName(sessionId: String, id: String, className: String) {
        dao.updateClassName(
            sessionId = sessionId,
            id = id,
            newClassName = className
        )
    }

    override suspend fun deleteAll(sessionId: String) {
        dao.deleteAll(sessionId)
    }

    override suspend fun addChildInstance(sessionId: String, parentId: String, childId: String) {
        val existingEntry = dao.select(sessionId, parentId) ?: return
        dao.updateReferencingInstanceIds(
            sessionId = sessionId,
            id = parentId,
            newReferencingInstanceIds = existingEntry.referencingInstanceIds + childId
        )
    }

    override suspend fun select(sessionId: String, instanceId: String): InstanceEntity? {
        return dao.select(sessionId = sessionId, id = instanceId)
    }
}
