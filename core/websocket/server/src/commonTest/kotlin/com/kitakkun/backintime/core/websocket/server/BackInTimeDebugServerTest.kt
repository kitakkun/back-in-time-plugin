package com.kitakkun.backintime.core.websocket.server

import com.kitakkun.backintime.core.websocket.client.BackInTimeWebSocketClient
import com.kitakkun.backintime.core.websocket.event.BackInTimeDebugServiceEvent
import com.kitakkun.backintime.core.websocket.event.BackInTimeDebuggerEvent
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

// FIXME: this test fails for native target
class BackInTimeDebugServerTest {
    companion object {
        private const val TEST_HOST = "localhost"
        private const val TEST_PORT = 50026
    }

    private lateinit var server: BackInTimeWebSocketServer
    private lateinit var client: BackInTimeWebSocketClient

    @BeforeTest
    fun setup() {
        server = BackInTimeWebSocketServer()
        client = BackInTimeWebSocketClient(TEST_HOST, TEST_PORT)
        server.start(TEST_HOST, TEST_PORT)
        runBlocking {
            assertEquals(
                expected = BackInTimeWebSocketServerState.Started(TEST_HOST, TEST_PORT, emptyList()),
                actual = server.stateFlow.filterIsInstance<BackInTimeWebSocketServerState.Started>().first()
            )
        }
    }

    @AfterTest
    fun teardown() {
        server.stop()
    }

    @Test
    fun `test send event from server`() = runTest {
        var clientReceivedEvent: BackInTimeDebuggerEvent? = null

        val connectedThenSendEventJob = launch {
            val sessionInfo = server.stateFlow
                .filterIsInstance<BackInTimeWebSocketServerState.Started>()
                .filter { it.sessions.isNotEmpty() }
                .first()
                .sessions.first()
            server.send(sessionInfo.id, BackInTimeDebuggerEvent.Ping(0))
        }

        val receiveServerEventJob = launch {
            clientReceivedEvent = client.receivedDebuggerEventFlow.first()
        }

        client.openSession()
        connectedThenSendEventJob.join()
        receiveServerEventJob.join()

        assertEquals(
            expected = BackInTimeDebuggerEvent.Ping(0),
            actual = clientReceivedEvent,
        )
    }

    @Test
    fun `test receive event from client`() = runTest {
        client.openSession()
        client.queueEvent(BackInTimeDebugServiceEvent.Ping(0))
        val (_, event) = server.eventFromClientFlow.first()
        assertEquals(BackInTimeDebugServiceEvent.Ping(0), event)
    }
}