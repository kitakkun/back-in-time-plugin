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
        assertEquals(expected = 1, actual = notifyValueChangeEvents.size)
        assertEquals(holder.backInTimeInstanceUUID, notifyValueChangeEvents[0].instanceUUID)
        assertEquals(expected = "com/kitakkun/backintime/test/basic/PropertyChangeEventTest.TestStateHolder.property", actual = notifyValueChangeEvents[0].propertySignature)
        assertEquals(expected = 1, actual = notifyValueChangeEvents[0].value.toInt())

        // external access can't be captured
        holder.property = 10
        delay(100)
        assertEquals(1, notifyValueChangeEvents.size)
    }
}
