package com.github.kitakkun.backintime.test.basic

import com.github.kitakkun.backintime.annotations.DebuggableStateHolder
import com.github.kitakkun.backintime.test.base.BackInTimeDebugServiceTest
import org.junit.Assert.assertEquals
import kotlin.test.Test

class PropertyChangeEventTest : BackInTimeDebugServiceTest() {
    @DebuggableStateHolder
    class TestStateHolder {
        var property: Int = 0
        fun updateProperty(newValue: Int) {
            property = newValue
        }
    }

    @Test
    fun test() {
        val holder = TestStateHolder()

        holder.updateProperty(1)
        assertEquals(1, propertyValueChangeEvents.size)
        assertEquals(holder, propertyValueChangeEvents[0].instance)
        assertEquals("property", propertyValueChangeEvents[0].propertyName)
        assertEquals(1, propertyValueChangeEvents[0].propertyValue)

        // external access can't be captured
        holder.property = 10
        assertEquals(1, propertyValueChangeEvents.size)
    }
}
