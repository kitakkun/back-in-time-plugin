package com.github.kitakkun.backintime.debugger.data.repository

import app.cash.sqldelight.ColumnAdapter
import com.github.kitakkun.backintime.debugger.database.ClassInfo
import com.github.kitakkun.backintime.debugger.database.ClassInfoQueries
import com.github.kitakkun.backintime.runtime.event.PropertyInfo
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * manage class information related to each session
 */
interface ClassInfoRepository {
    fun select(sessionId: String, className: String): ClassInfo?
    fun insert(sessionId: String, className: String, superClassName: String, properties: List<PropertyInfo>)
}

class ClassInfoRepositoryImpl(private val queries: ClassInfoQueries) : ClassInfoRepository {
    override fun select(sessionId: String, className: String): ClassInfo? {
        return queries.select(className = className, sessionId = sessionId).executeAsOneOrNull()
    }

    override fun insert(sessionId: String, className: String, superClassName: String, properties: List<PropertyInfo>) {
        queries.insert(
            id = "$sessionId/$className",
            className = className,
            superClassName = superClassName,
            properties = properties,
            sessionId = sessionId,
        )
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
