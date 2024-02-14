package com.github.kitakkun.backintime.evaluation.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
@Entity
data class Todo(
    @PrimaryKey val uuid: String = UUID.randomUUID().toString(),
    val label: String,
    val done: Boolean,
)
