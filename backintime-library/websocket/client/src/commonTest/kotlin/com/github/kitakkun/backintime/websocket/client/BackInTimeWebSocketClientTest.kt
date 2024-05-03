package com.github.kitakkun.backintime.websocket.client

import com.github.kitakkun.backintime.websocket.event.BackInTimeDebugServiceEvent
import com.github.kitakkun.backintime.websocket.event.BackInTimeDebuggerEvent
import io.ktor.server.application.install
import io.ktor.server.engine.connector
import io.ktor.server.testing.ApplicationTestBuilder
import io.ktor.server.testing.testApplication
import io.ktor.server.websocket.DefaultWebSocketServerSession
import io.ktor.server.websocket.WebSockets
import io.ktor.server.websocket.webSocket
import io.ktor.websocket.Frame
import io.ktor.websocket.readText
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

// FIXME: This test fails on iOS
class BackInTimeWebSocketClientTest {
    companion object {
        private const val TEST_HOST = "localhost"
        private const val TEST_PORT = 50026
    }

    @Test
    fun `test fails to connect`() {
        val client = BackInTimeWebSocketClient(
            host = TEST_HOST,
            port = TEST_PORT,
        )

        runBlocking {
            val result = client.connect()
            assertTrue(result.isFailure)
        }
    }

    @Test
    fun `test success to connect`() {
        testApplication {
            configureServer(
                host = TEST_HOST,
                port = TEST_PORT,
                serverSession = {
                    send(Frame.Text(Json.encodeToString<BackInTimeDebuggerEvent>(BackInTimeDebuggerEvent.SessionOpened("session_id"))))
                },
            )

            val client = configureClient(
                host = TEST_HOST,
                port = TEST_PORT,
            )

            val result = client.connect()
            assertTrue(result.isSuccess)
            client.close()
        }
    }

    @Test
    fun `test success to send event`() {
        testApplication {
            val serverReceiveFlow = MutableSharedFlow<BackInTimeDebugServiceEvent>(replay = 1)

            configureServer(
                host = TEST_HOST,
                port = TEST_PORT,
                serverSession = {
                    send(Frame.Text(Json.encodeToString<BackInTimeDebuggerEvent>(BackInTimeDebuggerEvent.SessionOpened("session_id"))))
                    val event = (incoming.receive() as? Frame.Text)?.let {
                        Json.decodeFromString<BackInTimeDebugServiceEvent>(it.readText())
                    } ?: return@configureServer
                    serverReceiveFlow.emit(event)
                },
            )

            val client = configureClient(host = TEST_HOST, port = TEST_PORT)

            val result = client.connect()
            assertTrue(result.isSuccess)

            client.send(BackInTimeDebugServiceEvent.Ping)

            val serverReceivedEvent = serverReceiveFlow.first()
            assertEquals(expected = BackInTimeDebugServiceEvent.Ping, actual = serverReceivedEvent)
            client.close()
        }
    }

    @Test
    fun `test success to receive event`() {
        testApplication {
            configureServer(
                host = TEST_HOST,
                port = TEST_PORT,
                serverSession = {
                    send(Frame.Text(Json.encodeToString<BackInTimeDebuggerEvent>(BackInTimeDebuggerEvent.SessionOpened("session_id"))))
                    delay(1000)
                    send(Frame.Text(Json.encodeToString<BackInTimeDebuggerEvent>(BackInTimeDebuggerEvent.Ping)))
                },
            )

            val client = configureClient(host = TEST_HOST, port = TEST_PORT)

            val result = client.connect()
            assertTrue(result.isSuccess)

            val receivedEvent = client.receiveEventAsFlow().first()
            assertEquals(expected = BackInTimeDebuggerEvent.Ping, actual = receivedEvent)
            client.close()
        }
    }

    private fun ApplicationTestBuilder.configureServer(
        host: String,
        port: Int,
        serverSession: suspend DefaultWebSocketServerSession.() -> Unit,
    ) {
        environment {
            connector {
                this.host = host
                this.port = port
            }
        }
        application {
            install(WebSockets)
        }
        routing {
            webSocket("/backintime") {
                serverSession()
            }
        }
    }

    private fun ApplicationTestBuilder.configureClient(
        host: String,
        port: Int,
    ): BackInTimeWebSocketClient {
        val client = BackInTimeWebSocketClient(
            host = host,
            port = port,
            client = createClient {
                install(io.ktor.client.plugins.websocket.WebSockets)
            },
        )
        return client
    }
}
