package com.github.kitakkun.backintime.debugger.data.repository

import com.github.kitakkun.backintime.debugger.data.coroutines.IOScope
import com.github.kitakkun.backintime.debugger.database.MethodCallInfoQueries
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.withContext

interface MethodCallInfoRepository {
    suspend fun insert(sessionId: String, instanceUUID: String, className: String, methodName: String, callId: String)
}

class MethodCallInfoRepositoryImpl(
    private val queries: MethodCallInfoQueries,
) : MethodCallInfoRepository, CoroutineScope by IOScope() {
    private fun generateId(sessionId: String, instanceUUID: String, callId: String) = "$sessionId/$instanceUUID/$callId"

    override suspend fun insert(
        sessionId: String,
        instanceUUID: String,
        className: String,
        methodName: String,
        callId: String,
    ) {
        withContext(coroutineContext) {
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
