package io.github.kitakkun.backintime.debugger.core.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class InstanceEntity(
    @PrimaryKey val id: String,
    val sessionId: String,
    val className: String,
    val registeredAt: Long,
    val isAlive: Boolean,
    val referencingInstanceIds: List<String>,
)
