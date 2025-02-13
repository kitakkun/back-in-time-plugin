package com.kitakkun.backintime.tooling.core.shared

import com.kitakkun.backintime.tooling.model.EventEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface BackInTimeDatabase {
    val stateFlow: StateFlow<State>

    fun restartDatabaseAsFile(filePath: String, migrate: Boolean)
    fun restartDatabaseInMemory(migrate: Boolean)
    fun insert(eventEntity: EventEntity)
    fun selectForSession(sessionId: String): Flow<List<EventEntity>>
    fun selectForInstance(sessionId: String, instanceId: String): Flow<List<EventEntity>>
    fun selectInstanceIds(sessionId: String): Flow<List<String>>

    sealed interface State {
        data object RunningInMemory : State
        data class RunningWithFile(val filePath: String) : State
        data object Stopped : State
    }
}