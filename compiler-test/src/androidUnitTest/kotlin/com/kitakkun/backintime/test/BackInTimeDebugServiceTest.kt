package com.kitakkun.backintime.test

import com.kitakkun.backintime.core.runtime.BackInTimeDebugService
import com.kitakkun.backintime.core.runtime.getBackInTimeDebugService
import com.kitakkun.backintime.core.runtime.internal.BackInTimeCompilerInternalApi
import com.kitakkun.backintime.core.websocket.event.BackInTimeDebugServiceEvent
import com.kitakkun.backintime.test.base.MockConnector
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.test.AfterTest
import kotlin.test.BeforeTest

/**
 * copy of BackInTimeDebugServiceTest.kt in jvmTest
 */
@RunWith(RobolectricTestRunner::class)
abstract class BackInTimeDebugServiceTest {
    @OptIn(BackInTimeCompilerInternalApi::class)
    private val service: BackInTimeDebugService = getBackInTimeDebugService(useInUnitTest = true)
    private val mockConnector = MockConnector()
    private val events: List<BackInTimeDebugServiceEvent> get() = mockConnector.eventsFromService

    val registerInstanceEvents get() = events.filterIsInstance<BackInTimeDebugServiceEvent.RegisterInstance>()
    val propertyValueChangeEvents get() = events.filterIsInstance<BackInTimeDebugServiceEvent.NotifyValueChange>()
    val methodCallEvents get() = events.filterIsInstance<BackInTimeDebugServiceEvent.NotifyMethodCall>()
    val registerRelationShipEvents get() = events.filterIsInstance<BackInTimeDebugServiceEvent.RegisterRelationship>()

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
