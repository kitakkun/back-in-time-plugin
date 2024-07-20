package io.github.kitakkun.backintime.core.websocket.client

import io.github.kitakkun.backintime.core.websocket.event.BackInTimeDebugServiceEvent
import io.github.kitakkun.backintime.core.websocket.event.BackInTimeDebuggerEvent
import io.ktor.server.application.install
import io.ktor.server.testing.ApplicationTestBuilder
import io.ktor.server.testing.testApplication
import io.ktor.server.websocket.DefaultWebSocketServerSession
import io.ktor.server.websocket.WebSockets
import io.ktor.server.websocket.webSocket
import io.ktor.websocket.Frame
import io.ktor.websocket.close
import io.ktor.websocket.readText
import io.ktor.websocket.send
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

// FIXME: This test fails on iOS
class BackInTimeWebSocketClientTest {
    companion object {
        private const val TEST_HOST = "localhost"
        private const val TEST_PORT = 50026
    }

    @Test
    fun `test fails to connect`() = runTest {
        val client = BackInTimeWebSocketClient(
            host = TEST_HOST,
            port = TEST_PORT,
        )

        assertFailsWith(Throwable::class) {
            client.openSession()
        }
    }

    @Test
    fun `test success to connect`() = runTest {
        testApplication {
            configureServer(
                host = TEST_HOST,
                port = TEST_PORT,
                serverSession = {
                    // Do nothing
                },
            )

            val client = configureClient(
                host = TEST_HOST,
                port = TEST_PORT,
            )

            client.openSession()
        }
    }

    @Test
    fun `test success to send event`() = runTest {
        testApplication {
            var receivedEvent: BackInTimeDebugServiceEvent? = null

            configureServer(
                host = TEST_HOST,
                port = TEST_PORT,
                serverSession = {
                    val event = (incoming.receive() as? Frame.Text)?.let {
                        Json.Default.decodeFromString<BackInTimeDebugServiceEvent>(it.readText())
                    } ?: return@configureServer
                    receivedEvent = event
                    close()
                },
            )

            val client = configureClient(host = TEST_HOST, port = TEST_PORT)
            val session = client.openSession()
            session.send(Json.Default.encodeToString<BackInTimeDebugServiceEvent>(BackInTimeDebugServiceEvent.Ping))
            session.closeReason.await()
            assertEquals(expected = BackInTimeDebugServiceEvent.Ping, actual = receivedEvent)
        }
    }

    @Test
    fun `test success to receive event`() = runTest {
        testApplication {
            configureServer(
                host = TEST_HOST,
                port = TEST_PORT,
                serverSession = {
                    delay(1000)
                    send(Json.Default.encodeToString<BackInTimeDebuggerEvent>(BackInTimeDebuggerEvent.Ping))
                    close()
                },
            )

            val client = configureClient(host = TEST_HOST, port = TEST_PORT)
            val session = client.openSession()
            val receivedEventFlow = session.incoming
                .receiveAsFlow()
                .filterIsInstance<Frame.Text>()
                .map { Json.Default.decodeFromString<BackInTimeDebuggerEvent>(it.readText()) }
            session.closeReason.await()
            assertEquals(expected = BackInTimeDebuggerEvent.Ping, actual = receivedEventFlow.firstOrNull())
        }
    }

    private fun ApplicationTestBuilder.configureServer(
        host: String,
        port: Int,
        serverSession: suspend DefaultWebSocketServerSession.() -> Unit,
    ) {
        environment {
            // FIXME: Unresolved reference
//            connector {
//                this.host = host
//                this.port = port
//            }
        }
        application {
            install(WebSockets.Plugin)
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