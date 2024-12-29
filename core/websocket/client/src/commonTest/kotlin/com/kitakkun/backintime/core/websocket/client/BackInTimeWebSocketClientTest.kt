package com.kitakkun.backintime.core.websocket.client

import com.kitakkun.backintime.core.websocket.event.BackInTimeDebugServiceEvent
import com.kitakkun.backintime.core.websocket.event.BackInTimeDebuggerEvent
import com.kitakkun.backintime.core.websocket.event.BackInTimeSessionNegotiationEvent
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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

// FIXME: This test fails for native targets
class BackInTimeWebSocketClientTest {
    companion object {
        private const val TEST_HOST = "localhost"
        private const val TEST_PORT = 50026
    }

    @OptIn(ExperimentalUuidApi::class)
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
                handler = {
                    println("New websocket session established!")
                    println("waiting sessionId negotiation request from the client...")

                    val requestedSessionId = receiveDeserialized<BackInTimeSessionNegotiationEvent.Request>().sessionId
                    println("starting sessionId negotiation...")

                    if (requestedSessionId == null) {
                        println("requested sessionId is null. generating new sessionId...")

                        val sessionId = Uuid.random().toString()
                        println("generated new sessionId: $sessionId")

                        sendSerialized(BackInTimeSessionNegotiationEvent.Accept(sessionId))
                    } else {
                        sendSerialized(BackInTimeSessionNegotiationEvent.Accept(requestedSessionId))
                    }

                    println("sessionId negotiation completed!")
                    println("start server session...")

                    serverSession()

                    println("keeping session active...")
                    this.closeReason.await()
                }
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
        }
    }

    @Test
    fun `test success to connect`() = testApplication {
        configureServer(
            host = TEST_HOST,
            port = TEST_PORT,
            serverSession = { close() },
        )

        val client = BackInTimeWebSocketClient(
            host = TEST_HOST,
            port = TEST_PORT,
            client = client,
        )

        client.openSession()
        client.close()
    }

    @Test
    fun `test success to send event`() = testApplication {
        var serverReceivedEvent: BackInTimeDebugServiceEvent? = null

        configureServer(
            host = TEST_HOST,
            port = TEST_PORT,
            serverSession = {
                serverReceivedEvent = receiveDeserialized()
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
            serverSession = { sendSerialized<BackInTimeDebuggerEvent>(BackInTimeDebuggerEvent.Ping) },
        )

        val client = BackInTimeWebSocketClient(
            host = TEST_HOST,
            port = TEST_PORT,
            client = client,
        )

        runTest {
            launch {
                clientReceivedEvent = (client.clientEventFlow.first() as BackInTimeWebSocketClientEvent.ReceiveDebuggerEvent).debuggerEvent
                client.close()
            }

            client.openSession()
            client.awaitClose()

            assertEquals(expected = BackInTimeDebuggerEvent.Ping, actual = clientReceivedEvent)
        }
    }
}