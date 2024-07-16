package io.github.kitakkun.backintime.test

import io.github.kitakkun.backintime.runtime.BackInTimeDebugService
import io.github.kitakkun.backintime.runtime.getBackInTimeDebugService
import io.github.kitakkun.backintime.runtime.internal.BackInTimeCompilerInternalApi
import io.github.kitakkun.backintime.test.base.MockConnector
import io.github.kitakkun.backintime.websocket.event.BackInTimeDebugServiceEvent
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
