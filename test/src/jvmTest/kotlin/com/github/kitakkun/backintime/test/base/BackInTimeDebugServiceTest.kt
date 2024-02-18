package com.github.kitakkun.backintime.test.base

import com.github.kitakkun.backintime.runtime.BackInTimeDebugService
import com.github.kitakkun.backintime.runtime.event.DebuggableStateHolderEvent
import io.mockk.every
import io.mockk.mockkObject
import io.mockk.slot
import io.mockk.unmockkAll
import kotlin.test.AfterTest
import kotlin.test.BeforeTest

abstract class BackInTimeDebugServiceTest {
    private lateinit var service: BackInTimeDebugService
    private val eventSlot = slot<DebuggableStateHolderEvent>()
    private val mutableEvents = mutableListOf<DebuggableStateHolderEvent>()
    val events: List<DebuggableStateHolderEvent> get() = mutableEvents

    val registerInstanceEvents get() = events.filterIsInstance<DebuggableStateHolderEvent.RegisterInstance>()
    val propertyValueChangeEvents get() = events.filterIsInstance<DebuggableStateHolderEvent.PropertyValueChange>()
    val methodCallEvents get() = events.filterIsInstance<DebuggableStateHolderEvent.MethodCall>()
    val registerRelationShipEvents get() = events.filterIsInstance<DebuggableStateHolderEvent.RegisterRelationShip>()

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
