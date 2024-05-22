package com.github.kitakkun.backintime.debugger.data.repository

import com.github.kitakkun.backintime.debugger.database.MethodCallInfoQueries
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.annotation.Singleton

interface MethodCallInfoRepository {
    suspend fun insert(sessionId: String, instanceUUID: String, className: String, methodName: String, callId: String)
}

@Singleton(binds = [MethodCallInfoRepository::class])
class MethodCallInfoRepositoryImpl(
    private val queries: MethodCallInfoQueries,
) : MethodCallInfoRepository {
    private val dispatcher = Dispatchers.IO

    private fun generateId(sessionId: String, instanceUUID: String, callId: String) = "$sessionId/$instanceUUID/$callId"

    override suspend fun insert(
        sessionId: String,
        instanceUUID: String,
        className: String,
        methodName: String,
        callId: String,
    ) {
        withContext(dispatcher) {
            queries.insert(
                id = generateId(sessionId, instanceUUID, callId),
                instanceId = instanceUUID,
                className = className,
                methodName = methodName,
                sesionId = sessionId,
            )
        }
    }
}
