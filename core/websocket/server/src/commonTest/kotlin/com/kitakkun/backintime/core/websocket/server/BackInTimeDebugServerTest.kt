package com.kitakkun.backintime.core.websocket.server

import com.kitakkun.backintime.core.websocket.client.BackInTimeWebSocketClient
import com.kitakkun.backintime.core.websocket.client.BackInTimeWebSocketClientEvent
import com.kitakkun.backintime.core.websocket.event.BackInTimeDebugServiceEvent
import com.kitakkun.backintime.core.websocket.event.BackInTimeDebuggerEvent
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

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
        assertTrue { server.isRunning }
    }

    @AfterTest
    fun teardown() {
        server.stop()
    }

    @Test
    fun `test send event from server`() = runTest {
        launch {
            val sessionId = server.connectionEstablishedFlow.first()
            server.send(sessionId, BackInTimeDebuggerEvent.Ping)
        }

        client.openSession()

        assertEquals(
            expected = BackInTimeDebuggerEvent.Ping,
            actual = client.clientEventFlow.filterIsInstance<BackInTimeWebSocketClientEvent.ReceiveDebuggerEvent>().first().debuggerEvent
        )
    }

    @Test
    fun `test receive event from client`() = runTest {
        launch {
            val (_, event) = server.receivedEventFlow.first()
            assertEquals(BackInTimeDebugServiceEvent.Ping, event)
        }

        client.openSession()
        client.queueEvent(BackInTimeDebugServiceEvent.Ping)
    }
}