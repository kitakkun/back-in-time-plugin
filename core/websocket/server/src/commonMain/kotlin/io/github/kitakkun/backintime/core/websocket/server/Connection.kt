package io.github.kitakkun.backintime.core.websocket.server

import com.benasher44.uuid.uuid4
import io.ktor.server.websocket.DefaultWebSocketServerSession

data class Connection(
    val session: DefaultWebSocketServerSession,
    val id: String = uuid4().toString(),
)
