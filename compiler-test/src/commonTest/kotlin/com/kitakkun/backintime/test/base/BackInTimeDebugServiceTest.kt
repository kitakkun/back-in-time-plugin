package com.kitakkun.backintime.test.base

import com.kitakkun.backintime.core.runtime.BackInTimeDebugService
import com.kitakkun.backintime.core.runtime.getBackInTimeDebugService
import com.kitakkun.backintime.core.runtime.internal.BackInTimeCompilerInternalApi
import com.kitakkun.backintime.core.websocket.event.BackInTimeDebugServiceEvent
import kotlin.test.AfterTest
import kotlin.test.BeforeTest

abstract class BackInTimeDebugServiceTest {
    private val mockConnector: MockConnector = MockConnector()

    @OptIn(BackInTimeCompilerInternalApi::class)
    private val service: BackInTimeDebugService = getBackInTimeDebugService(useInUnitTest = true)

    private val events get() = mockConnector.eventsFromService
    protected val registerInstanceEvents get() = events.filterIsInstance<BackInTimeDebugServiceEvent.RegisterInstance>()
    protected val notifyValueChangeEvents get() = events.filterIsInstance<BackInTimeDebugServiceEvent.NotifyValueChange>()
    protected val notifyMethodCallEvents get() = events.filterIsInstance<BackInTimeDebugServiceEvent.NotifyMethodCall>()
    protected val registerRelationshipEvents get() = events.filterIsInstance<BackInTimeDebugServiceEvent.RegisterRelationship>()
    protected val checkInstanceAliveResultEvents get() = events.filterIsInstance<BackInTimeDebugServiceEvent.CheckInstanceAliveResult>()

    @BeforeTest
    fun setup() {
        service.setConnector(mockConnector)
        service.startService()
    }

    @AfterTest
    fun teardown() {
        service.stopService()
    }
}
