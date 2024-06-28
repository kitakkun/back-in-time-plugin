package com.github.kitakkun.backintime.debugger.data.repository

import com.github.kitakkun.backintime.debugger.database.MethodCallInfo
import com.github.kitakkun.backintime.debugger.database.MethodCallInfoQueries
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.annotation.Singleton

interface MethodCallInfoRepository {
    suspend fun insert(sessionId: String, methodCallId: String, instanceUUID: String, className: String, methodName: String, callId: String, calledAt: Long)
    suspend fun select(sessionId: String, instanceUUID: String, callId: String): MethodCallInfo?
}

@Singleton(binds = [MethodCallInfoRepository::class])
class MethodCallInfoRepositoryImpl(
    private val queries: MethodCallInfoQueries,
) : MethodCallInfoRepository {
    private val dispatcher = Dispatchers.IO

    override suspend fun select(sessionId: String, instanceUUID: String, callId: String): MethodCallInfo? {
        return withContext(dispatcher) {
            queries.select(
                sessionId = sessionId,
                instanceId = instanceUUID,
                id = callId,
            ).executeAsOneOrNull()
        }
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
        withContext(dispatcher) {
            queries.insert(
                id = methodCallId,
                instanceId = instanceUUID,
                className = className,
                methodName = methodName,
                sessionId = sessionId,
                calledAt = calledAt,
            )
        }
    }
}
