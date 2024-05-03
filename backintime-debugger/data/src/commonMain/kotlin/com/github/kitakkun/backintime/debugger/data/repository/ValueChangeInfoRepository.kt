package com.github.kitakkun.backintime.debugger.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.github.kitakkun.backintime.debugger.data.coroutines.IOScope
import com.github.kitakkun.backintime.debugger.database.ValueChangeInfo
import com.github.kitakkun.backintime.debugger.database.ValueChangeInfoQueries
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

interface ValueChangeInfoRepository {
    fun selectForSessionAsFlow(sessionId: String): Flow<List<ValueChangeInfo>>

    suspend fun insert(
        sessionId: String,
        instanceId: String,
        methodCallId: String,
        ownerClassName: String,
        propertyName: String,
        value: String,
    )
}

class ValueChangeInfoRepositoryImpl(
    private val valueChangeInfoQueries: ValueChangeInfoQueries,
) : ValueChangeInfoRepository, CoroutineScope by IOScope() {
    override fun selectForSessionAsFlow(sessionId: String): Flow<List<ValueChangeInfo>> {
        return valueChangeInfoQueries.selectBySessionId(sessionId).asFlow().mapToList(coroutineContext)
    }

    override suspend fun insert(
        sessionId: String,
        instanceId: String,
        methodCallId: String,
        ownerClassName: String,
        propertyName: String,
        value: String,
    ) {
        withContext(coroutineContext) {
            valueChangeInfoQueries.insert(
                sessionId = sessionId,
                instanceId = instanceId,
                methodCallId = methodCallId,
                className = ownerClassName,
                propertyName = propertyName,
                propertyValue = value,
            )
        }
    }
}
