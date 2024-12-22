package com.kitakkun.backintime.core.websocket.client

import com.kitakkun.backintime.core.websocket.event.BackInTimeDebugServiceEvent
import com.kitakkun.backintime.core.websocket.event.BackInTimeDebuggerEvent
import io.ktor.serialization.kotlinx.KotlinxWebsocketSerializationConverter
import io.ktor.server.application.install
import io.ktor.server.engine.connector
import io.ktor.server.testing.ApplicationTestBuilder
import io.ktor.server.testing.testApplication
import io.ktor.server.websocket.DefaultWebSocketServerSession
import io.ktor.server.websocket.WebSockets
import io.ktor.server.websocket.receiveDeserialized
import io.ktor.server.websocket.sendSerialized
import io.ktor.server.websocket.webSocket
import io.ktor.websocket.close
import kotlinx.coroutines.delay
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

// FIXME: This test fails for native targets
class BackInTimeWebSocketClientTest {
    companion object {
        private const val TEST_HOST = "localhost"
        private const val TEST_PORT = 50026
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
            install(WebSockets) {
                contentConverter = KotlinxWebsocketSerializationConverter(Json)
            }
        }
        routing {
            webSocket(
                path = "/backintime",
                handler = serverSession,
            )
        }
    }

    @Test
    fun `test fails to connect`() = testApplication {
        val client = BackInTimeWebSocketClient(
            host = TEST_HOST,
            port = TEST_PORT,
            client = client,
        )

        assertFailsWith(Throwable::class) {
            client.openSession()
            client.awaitClose()
        }
    }

    @Test
    fun `test success to connect`() = testApplication {
        configureServer(
            host = TEST_HOST,
            port = TEST_PORT,
            serverSession = { /* Do nothing */ },
        )

        val client = BackInTimeWebSocketClient(
            host = TEST_HOST,
            port = TEST_PORT,
            client = client,
        )

        client.openSession()
        client.awaitClose()
    }

    @Test
    fun `test success to send event`() = testApplication {
        var serverReceivedEvent: BackInTimeDebugServiceEvent? = null

        configureServer(
            host = TEST_HOST,
            port = TEST_PORT,
            serverSession = {
                serverReceivedEvent = receiveDeserialized<BackInTimeDebugServiceEvent>()
                close()
            },
        )

        val client = BackInTimeWebSocketClient(
            host = TEST_HOST,
            port = TEST_PORT,
            client = client,
        )

        client.openSession()
        client.queueEvent(BackInTimeDebugServiceEvent.Ping)
        client.awaitClose()

        assertEquals(expected = BackInTimeDebugServiceEvent.Ping, actual = serverReceivedEvent)
    }


    @Test
    fun `test success to receive event`() = testApplication {
        var clientReceivedEvent: BackInTimeDebuggerEvent? = null

        configureServer(
            host = TEST_HOST,
            port = TEST_PORT,
            serverSession = {
                sendSerialized<BackInTimeDebuggerEvent>(BackInTimeDebuggerEvent.Ping)
                delay(100) // need this to pass the test
                close()
            },
        )

        val client = BackInTimeWebSocketClient(
            host = TEST_HOST,
            port = TEST_PORT,
            client = client,
        )

        client.addListener(object : BackInTimeWebSocketClientListener {
            override fun onReceive(event: BackInTimeDebuggerEvent) {
                clientReceivedEvent = event
            }
        })

        client.openSession()
        client.awaitClose()

        assertEquals(expected = BackInTimeDebuggerEvent.Ping, actual = clientReceivedEvent)
    }
}