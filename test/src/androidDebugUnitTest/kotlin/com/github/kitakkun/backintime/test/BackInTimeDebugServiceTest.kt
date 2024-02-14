package com.github.kitakkun.backintime.test

import com.github.kitakkun.backintime.runtime.BackInTimeDebugService
import com.github.kitakkun.backintime.runtime.BackInTimeDebugServiceEvent
import io.mockk.every
import io.mockk.mockkObject
import io.mockk.slot
import io.mockk.unmockkAll
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.test.AfterTest
import kotlin.test.BeforeTest

/**
 * copy of BackInTimeDebugServiceTest.kt in jvmTest
 */
@RunWith(RobolectricTestRunner::class)
abstract class BackInTimeDebugServiceTest {
    private lateinit var service: BackInTimeDebugService
    private val eventSlot = slot<BackInTimeDebugServiceEvent>()
    private val mutableEvents = mutableListOf<BackInTimeDebugServiceEvent>()
    val events: List<BackInTimeDebugServiceEvent> get() = mutableEvents

    val registerInstanceEvents get() = events.filterIsInstance<BackInTimeDebugServiceEvent.RegisterInstance>()
    val propertyValueChangeEvents get() = events.filterIsInstance<BackInTimeDebugServiceEvent.PropertyValueChange>()
    val methodCallEvents get() = events.filterIsInstance<BackInTimeDebugServiceEvent.MethodCall>()
    val registerRelationShipEvents get() = events.filterIsInstance<BackInTimeDebugServiceEvent.RegisterRelationShip>()

    @BeforeTest
    fun setup() {
        mockkObject(BackInTimeDebugService)
        service = BackInTimeDebugService
        service.start()
        every {
            service.emitEvent(capture(eventSlot))
        } answers {
            mutableEvents.add(eventSlot.captured)
        }
    }

    @AfterTest
    fun teardown() {
        service.suspend()
        unmockkAll()
    }
}
