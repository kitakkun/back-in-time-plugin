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
    companion object {
        private const val CLASS_FQ_NAME = "com.kitakkun.backintime.test.basic.PropertyChangeEventTest.TestStateHolder"
        private const val PROPERTY_NAME = "property"
    }

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
        assertEquals(expected = CLASS_FQ_NAME, actual = notifyValueChangeEvents[0].ownerClassFqName)
        assertEquals(expected = PROPERTY_NAME, actual = notifyValueChangeEvents[0].propertyName)
        assertEquals(expected = 1, actual = notifyValueChangeEvents[0].value.toInt())

        // external access can't be captured
        holder.property = 10
        delay(100)
        assertEquals(1, notifyValueChangeEvents.size)
    }
}
