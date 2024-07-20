package io.github.kitakkun.backintime.debugger.core.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "session",
)
data class SessionInfoEntity(
    @PrimaryKey val id: String,
    val label: String?,
    val isActive: Boolean,
    val createdAt: Long,
)
