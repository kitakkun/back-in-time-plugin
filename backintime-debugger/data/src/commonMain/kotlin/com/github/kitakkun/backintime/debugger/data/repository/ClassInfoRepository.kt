package com.github.kitakkun.backintime.debugger.data.repository

import app.cash.sqldelight.ColumnAdapter
import com.github.kitakkun.backintime.debugger.database.ClassInfo
import com.github.kitakkun.backintime.debugger.database.ClassInfoQueries
import com.github.kitakkun.backintime.websocket.event.model.PropertyInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * manage class information related to each session
 */
interface ClassInfoRepository {
    suspend fun select(sessionId: String, className: String): ClassInfo?
    suspend fun insert(sessionId: String, className: String, superClassName: String, properties: List<PropertyInfo>)
}

class ClassInfoRepositoryImpl(private val queries: ClassInfoQueries) : ClassInfoRepository {
    private val dispatcher = Dispatchers.IO

    override suspend fun select(sessionId: String, className: String): ClassInfo? {
        return withContext(dispatcher) {
            queries.select(className = className, sessionId = sessionId).executeAsOneOrNull()
        }
    }

    override suspend fun insert(sessionId: String, className: String, superClassName: String, properties: List<PropertyInfo>) {
        withContext(dispatcher) {
            val existingEntity = queries.select(className = className, sessionId = sessionId).executeAsOneOrNull()
            if (existingEntity != null) return@withContext
            queries.insert(
                className = className,
                superClassName = superClassName,
                properties = properties,
                sessionId = sessionId,
            )
        }
    }
}

val listOfPropertyInfoAdapter = object : ColumnAdapter<List<PropertyInfo>, String> {
    override fun encode(value: List<PropertyInfo>): String {
        return Json.encodeToString(value)
    }

    override fun decode(databaseValue: String): List<PropertyInfo> {
        return Json.decodeFromString(databaseValue)
    }
}