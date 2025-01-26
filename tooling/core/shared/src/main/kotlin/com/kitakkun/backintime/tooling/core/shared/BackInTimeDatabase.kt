package com.kitakkun.backintime.tooling.core.shared

import com.kitakkun.backintime.tooling.model.EventEntity
import kotlinx.coroutines.flow.Flow

interface BackInTimeDatabase {
    fun restartDatabase(filePath: String, migrate: Boolean)
    fun insert(eventEntity: EventEntity)
    fun selectForSession(sessionId: String): Flow<List<EventEntity>>
    fun selectForInstance(sessionId: String, instanceId: String): Flow<List<EventEntity>>
    fun selectInstanceIds(sessionId: String): Flow<List<String>>
}