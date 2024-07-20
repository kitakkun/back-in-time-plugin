package io.github.kitakkun.backintime.debugger.core.data

import io.github.kitakkun.backintime.debugger.core.database.dao.MethodCallDao
import io.github.kitakkun.backintime.debugger.core.database.model.MethodCallEntity
import kotlinx.coroutines.flow.Flow

interface MethodCallRepository {
    fun selectAsFlow(sessionId: String, instanceId: String): Flow<List<MethodCallEntity>>
    suspend fun insert(sessionId: String, methodCallId: String, instanceUUID: String, className: String, methodName: String, callId: String, calledAt: Long)
    suspend fun select(sessionId: String, instanceId: String, callId: String): MethodCallEntity?
}

class MethodCallRepositoryImpl(private val dao: MethodCallDao) : MethodCallRepository {
    override fun selectAsFlow(sessionId: String, instanceId: String): Flow<List<MethodCallEntity>> = dao.selectAsFlow(sessionId, instanceId)

    override suspend fun select(sessionId: String, instanceId: String, callId: String): MethodCallEntity? {
        return dao.select(sessionId, instanceId, callId)
    }

    override suspend fun insert(
        sessionId: String,
        methodCallId: String,
        instanceUUID: String,
        className: String,
        methodName: String,
        callId: String,
        calledAt: Long,
    ) {
        dao.insert(
            MethodCallEntity(
                id = callId,
                methodName = methodName,
                className = className,
                instanceId = instanceUUID,
                sessionId = sessionId,
                calledAt = calledAt,
            )
        )
    }
}
