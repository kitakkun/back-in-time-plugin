package io.github.kitakkun.backintime.debugger.core.usecase

import androidx.compose.runtime.Composable
import io.github.kitakkun.backintime.debugger.core.data.InstanceRepository
import io.github.kitakkun.backintime.debugger.core.data.SessionInfoRepository
import io.github.kitakkun.backintime.debugger.core.model.Session

@Composable
fun sessions(
    openedSessionIds: List<String>,
    sessionInfoRepository: SessionInfoRepository,
): List<Session> {
    return emptyList()
}


@Composable
fun session(
    sessionId: String,
    instanceRepository: InstanceRepository,
): Session? {
    instanceRepository.selectInstancesFlow(sessionId)
    return null
}
