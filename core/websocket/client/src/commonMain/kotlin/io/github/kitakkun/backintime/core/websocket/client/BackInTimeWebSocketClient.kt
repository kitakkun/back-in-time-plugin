package io.github.kitakkun.backintime.core.websocket.client

import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.websocket.DefaultClientWebSocketSession
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.plugins.websocket.webSocketSession
import io.ktor.serialization.kotlinx.KotlinxWebsocketSerializationConverter
import kotlinx.serialization.json.Json

/**
 * This class is responsible for connecting to the back-in-time debugger server
 */
class BackInTimeWebSocketClient(
    private val host: String,
    private val port: Int,
    engine: HttpClientEngine = CIO.create(),
    client: HttpClient? = null,
) {
    private val client = client ?: HttpClient(engine) {
        install(WebSockets.Plugin) {
            maxFrameSize = Long.MAX_VALUE
            contentConverter = KotlinxWebsocketSerializationConverter(Json.Default)
        }
    }

    suspend fun openSession(): DefaultClientWebSocketSession = client.webSocketSession(
        host = host,
        port = port,
        path = "/backintime",
    )
}