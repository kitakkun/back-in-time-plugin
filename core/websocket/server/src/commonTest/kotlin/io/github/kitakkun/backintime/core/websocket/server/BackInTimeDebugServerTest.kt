package io.github.kitakkun.backintime.core.websocket.server

import io.github.kitakkun.backintime.core.websocket.event.BackInTimeDebugServiceEvent
import io.github.kitakkun.backintime.core.websocket.event.BackInTimeDebuggerEvent
import io.ktor.client.plugins.websocket.DefaultClientWebSocketSession
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.plugins.websocket.webSocket
import io.ktor.server.application.Application
import io.ktor.server.engine.connector
import io.ktor.server.testing.ApplicationTestBuilder
import io.ktor.server.testing.testApplication
import io.ktor.websocket.Frame
import io.ktor.websocket.readText
import io.ktor.websocket.send
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

// FIXME: this test fails for native target
class BackInTimeDebugServerTest {
    companion object {
        private const val TEST_HOST = "localhost"
        private const val TEST_PORT = 50026
    }

    @Test
    fun `test connect`() {
        testApplication {
            val connectedFlow = MutableSharedFlow<Boolean>(replay = 1)

            engine {
                connector {
                    host = TEST_HOST
                    port = TEST_PORT
                }
            }
            application {
                configureApplication(
                    onConnect = {
                        connectedFlow.emit(true)
                    },
                    onReceiveEvent = { _, _ ->
                    },
                )
            }

            startSession(
                host = TEST_HOST,
                port = TEST_PORT,
            ) {
                // Do nothing
            }

            assertTrue(connectedFlow.first())
        }
    }

    @Test
    fun `test receive`() {
        testApplication() {
            val connectedFlow = MutableSharedFlow<Boolean>(replay = 1)

            engine {
                connector {
                    host = TEST_HOST
                    port = TEST_PORT
                }
            }
            application {
                configureApplication(
                    onConnect = {
                        connectedFlow.emit(true)
                    },
                    onReceiveEvent = { _, _ ->
                    },
                )
            }

            startSession(
                host = TEST_HOST,
                port = TEST_PORT,
            ) {
                // Do nothing
            }

            assertTrue(connectedFlow.first())
        }
    }

    @Test
    fun `test send`() {
        testApplication {
            engine {
                connector {
                    host = TEST_HOST
                    port = TEST_PORT
                }
            }
            application {
                configureApplication(
                    onConnect = {
                        it.session.send(Json.Default.encodeToString(BackInTimeDebuggerEvent.Ping))
                    },
                    onReceiveEvent = { _, _ ->
                    },
                )
            }

            startSession(
                host = TEST_HOST,
                port = TEST_PORT,
            ) {
                // Do nothing
                val receivedEvent = (incoming.receive() as? Frame.Text)?.let {
                    Json.Default.decodeFromString<BackInTimeDebuggerEvent.Ping>(it.readText())
                }
                assertEquals(expected = BackInTimeDebuggerEvent.Ping, actual = receivedEvent)
            }
        }
    }

    private fun Application.configureApplication(
        onConnect: suspend (Connection) -> Unit,
        onReceiveEvent: suspend (Connection, BackInTimeDebugServiceEvent) -> Unit,
    ) {
        installWebSocket()
        configureWebSocketRouting(
            onConnect = onConnect,
            onReceiveEvent = onReceiveEvent,
        )
    }

    private suspend fun ApplicationTestBuilder.startSession(
        host: String,
        port: Int,
        session: suspend DefaultClientWebSocketSession.() -> Unit,
    ) {
        createClient {
            install(WebSockets.Plugin)
        }.webSocket(
            host = host,
            port = port,
            path = "/backintime",
        ) {
            session()
        }
    }
}