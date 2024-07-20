package io.github.kitakkun.backintime.debugger.core.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "value_change"
)
data class ValueChangeEntity(
    @PrimaryKey val id: String,
    val sessionId: String,
    val instanceId: String,
    val methodCallId: String,
    val propertyName: String,
    val propertyOwnerClassName: String,
    val newValue: String,
)
