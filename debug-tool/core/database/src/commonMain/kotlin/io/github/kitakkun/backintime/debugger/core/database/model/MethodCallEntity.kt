package io.github.kitakkun.backintime.debugger.core.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "method_call")
data class MethodCallEntity(
    @PrimaryKey val id: String,
    val methodName: String,
    val className: String,
    val instanceId: String,
    val sessionId: String,
    val calledAt: Long,
)
