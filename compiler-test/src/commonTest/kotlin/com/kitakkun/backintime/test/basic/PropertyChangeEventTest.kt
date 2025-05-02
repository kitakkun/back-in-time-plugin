package com.kitakkun.backintime.test.basic

import com.kitakkun.backintime.core.annotations.BackInTime
import com.kitakkun.backintime.core.runtime.BackInTimeDebuggable
import com.kitakkun.backintime.test.base.BackInTimeDebugServiceTest
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class PropertyChangeEventTest : BackInTimeDebugServiceTest() {
    @BackInTime
    class TestStateHolder {
        var property: Int = 0
        fun updateProperty(newValue: Int) {
            property = newValue
        }
    }

    @Test
    fun test() = runBlocking {
        val holder = TestStateHolder()
        assertIs<BackInTimeDebuggable>(holder)

        holder.updateProperty(1)
        delay(100)
        assertEquals(expected = 2, actual = notifyValueChangeEvents.size)
        assertEquals(holder.backInTimeInstanceUUID, notifyValueChangeEvents[0].instanceUUID)
        assertEquals(holder.backInTimeInstanceUUID, notifyValueChangeEvents[1].instanceUUID)
        assertEquals(expected = "com/kitakkun/backintime/test/basic/PropertyChangeEventTest.TestStateHolder.property", actual = notifyValueChangeEvents[0].propertySignature)
        assertEquals(expected = "com/kitakkun/backintime/test/basic/PropertyChangeEventTest.TestStateHolder.property", actual = notifyValueChangeEvents[1].propertySignature)
        assertEquals(expected = 0, actual = notifyValueChangeEvents[0].value.toInt())
        assertEquals(expected = 1, actual = notifyValueChangeEvents[1].value.toInt())

        // external access can't be captured
        holder.property = 10
        delay(100)
        assertEquals(2, notifyValueChangeEvents.size)
    }
}
