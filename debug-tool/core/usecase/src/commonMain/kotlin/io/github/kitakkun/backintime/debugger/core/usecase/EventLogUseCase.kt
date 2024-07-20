package io.github.kitakkun.backintime.debugger.core.usecase

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import io.github.kitakkun.backintime.debugger.core.model.EventLog
import io.github.kitakkun.backintime.debugger.core.usecase.compositionlocal.localEventLogRepository

@Composable
fun eventLogs(sessionId: String): List<EventLog> {
    val logRepository = localEventLogRepository()
    val logs by logRepository.logFlow(sessionId).collectAsState(emptyList())
    return logs.map {
        EventLog(
            id = it.id,
            payload = it.payload,
            createdAt = it.createdAt,
        )
    }
}
