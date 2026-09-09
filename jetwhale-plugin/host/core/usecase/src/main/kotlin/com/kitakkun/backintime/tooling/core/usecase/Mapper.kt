package com.kitakkun.backintime.tooling.core.usecase

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import com.kitakkun.backintime.tooling.model.EventEntity

@Composable
fun allEvents(sessionId: String?): List<EventEntity> {
    val database = LocalDatabase.current
    return database.selectForSession(sessionId = sessionId ?: "").collectAsState(emptyList()).value
}
