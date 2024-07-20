package io.github.kitakkun.backintime.debugger.core.usecase

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import io.github.kitakkun.backintime.debugger.core.model.SessionInfo
import io.github.kitakkun.backintime.debugger.core.usecase.compositionlocal.localSessionInfoRepository

@Composable
fun sessionInfoList(): List<SessionInfo> {
    val repository = localSessionInfoRepository()
    val sessions by repository.allSessions.collectAsState(emptyList())
    return sessions.map {
        SessionInfo(
            id = it.id,
            label = it.label,
            createdAt = it.createdAt,
            isActive = it.isActive,
        )
    }
}
