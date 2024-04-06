package com.github.kitakkun.backintime.debugger.data.repository

import app.cash.sqldelight.ColumnAdapter
import com.github.kitakkun.backintime.debugger.data.coroutines.IOScope
import com.github.kitakkun.backintime.debugger.database.ClassInfo
import com.github.kitakkun.backintime.debugger.database.ClassInfoQueries
import com.github.kitakkun.backintime.runtime.event.PropertyInfo
import kotlinx.coroutines.CoroutineScope
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

class ClassInfoRepositoryImpl(private val queries: ClassInfoQueries) : ClassInfoRepository, CoroutineScope by IOScope() {
    override suspend fun select(sessionId: String, className: String): ClassInfo? {
        return withContext(coroutineContext) {
            queries.select(className = className, sessionId = sessionId).executeAsOneOrNull()
        }
    }

    override suspend fun insert(sessionId: String, className: String, superClassName: String, properties: List<PropertyInfo>) {
        withContext(coroutineContext) {
            queries.insert(
                id = "$sessionId/$className",
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
