package com.github.kitakkun.backintime.websocket.server

import com.github.kitakkun.backintime.websocket.event.BackInTimeDebugServiceEvent
import io.ktor.client.plugins.websocket.DefaultClientWebSocketSession
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.plugins.websocket.webSocket
import io.ktor.server.application.Application
import io.ktor.server.engine.connector
import io.ktor.server.testing.ApplicationTestBuilder
import io.ktor.server.testing.testApplication
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.first
import kotlin.test.Test
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

            environment {
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
        testApplication {
            val connectedFlow = MutableSharedFlow<Boolean>(replay = 1)

            environment {
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
            install(WebSockets)
        }.webSocket(
            host = host,
            port = port,
            path = "/backintime",
        ) {
            session()
        }
    }
}
