package com.github.kitakkun.backintime.websocket.test

import com.github.kitakkun.backintime.websocket.client.BackInTimeWebSocketClient
import com.github.kitakkun.backintime.websocket.event.BackInTimeDebugServiceEvent
import com.github.kitakkun.backintime.websocket.event.BackInTimeDebuggerEvent
import com.github.kitakkun.backintime.websocket.server.BackInTimeWebSocketServer
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class WebSocketCommunicationTest {
    private val server = BackInTimeWebSocketServer()
    private val client = BackInTimeWebSocketClient()

    @BeforeTest
    fun setup() = runBlocking {
        server.start(0)
        assertTrue(server.isRunning)

        val host = server.getActualHost()
        val port = server.getActualPort()
        assertNotNull(host)
        assertNotNull(port)

        client.connectUntilSuccess(host = host, port = port)
        assertTrue(client.connected)
    }

    @AfterTest
    fun tearDown() = runBlocking {
        client.close()
        server.stop()
    }

    @Test
    fun sendEventFromDebuggerToApp() = runBlocking {
        val collectedEvents = mutableListOf<BackInTimeDebuggerEvent>()

        val job = launch { client.receivedEventFlow.toList(collectedEvents) }

        val event = BackInTimeDebuggerEvent.Ping
        server.send(event)
        delay(50) // FIXME: This is a hack to wait for the event to be processed
        job.cancel()

        assertEquals(expected = 1, actual = collectedEvents.size)
        assertEquals(expected = event, actual = collectedEvents[0])
    }

    @Test
    fun sendEventFromAppToDebugger() = runBlocking {
        val collectedEvents = mutableListOf<BackInTimeDebugServiceEvent>()

        val job = launch { server.receivedEventFlow.collect { collectedEvents.add(it.second) } }

        val event = BackInTimeDebugServiceEvent.Ping
        client.send(event)
        delay(50) // FIXME: This is a hack to wait for the event to be processed
        job.cancel()

        assertEquals(expected = 1, actual = collectedEvents.size)
        assertEquals(expected = event, actual = collectedEvents[0])
    }
}
