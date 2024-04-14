package com.github.kitakkun.backintime.debugger.data.repository

import app.cash.sqldelight.ColumnAdapter
import com.github.kitakkun.backintime.debugger.data.coroutines.IOScope
import com.github.kitakkun.backintime.debugger.data.model.ValueChangeInfo
import com.github.kitakkun.backintime.debugger.database.MethodCallInfoQueries
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

interface MethodCallInfoRepository {
    suspend fun insert(sessionId: String, instanceUUID: String, className: String, methodName: String, callId: String)
    suspend fun insertValueChange(sessionId: String, instanceUUID: String, className: String, callId: String, propertyName: String, propertyValue: String)
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
                valueChanges = emptyList(),
            )
        }
    }

    override suspend fun insertValueChange(
        sessionId: String,
        instanceUUID: String,
        className: String,
        callId: String,
        propertyName: String,
        propertyValue: String,
    ) {
        withContext(coroutineContext) {
            val id = generateId(sessionId, instanceUUID, callId)
            val valueChanges = queries.selectById(id).executeAsOne().valueChanges.toMutableList()
            valueChanges.add(ValueChangeInfo(propertyName, propertyValue))
            queries.updateValueChangesById(valueChanges, id)
        }
    }
}

val listOfValueChangeInfoAdapter = object : ColumnAdapter<List<ValueChangeInfo>, String> {
    override fun encode(value: List<ValueChangeInfo>): String {
        return Json.encodeToString(value)
    }

    override fun decode(databaseValue: String): List<ValueChangeInfo> {
        return Json.decodeFromString(databaseValue)
    }
}
