package com.github.kitakkun.backintime.test.specific

import com.github.kitakkun.backintime.annotations.DebuggableStateHolder
import com.github.kitakkun.backintime.runtime.BackInTimeDebuggable
import com.github.kitakkun.backintime.test.base.BackInTimeDebugServiceTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class GradleConfiguredValueContainerTest : BackInTimeDebugServiceTest() {
    /**
     * backInTime {
     *     enabled = true
     *     valueContainers {
     *         ...
     *         container {
     *             className = "com/github/kitakkun/backintime/test/specific/GradleConfiguredValueContainerTest.GradleConfiguredValueContainer"
     *             captures = listOf("<set-value>", "update")
     *             getter = "<get-value>"
     *             setter = "<set-value>"
     *         }
     *     }
     * }
     */
    private class GradleConfiguredValueContainer<T>(var value: T) {
        fun update(newValue: T) {
            value = newValue
        }
    }

    @DebuggableStateHolder
    private class ValueContainerHolder {
        val container = GradleConfiguredValueContainer(0)

        fun updateContainerValue(value: Int) {
            container.value = value
        }
    }

    @Test
    fun captureTest() {
        val holder = ValueContainerHolder()
        assertIs<BackInTimeDebuggable>(holder)

        holder.updateContainerValue(10)
        assertEquals(10, holder.container.value)
        assertEquals(1, propertyValueChangeEvents.size)
        assertEquals(holder, propertyValueChangeEvents[0].instance)
        assertEquals("container", propertyValueChangeEvents[0].propertyName)
        assertEquals(10, propertyValueChangeEvents[0].propertyValue)
    }

    @Test
    fun serializeTest() {
        val holder = ValueContainerHolder()
        assertIs<BackInTimeDebuggable>(holder)

        assertEquals("10", holder.serializeValue("container", 10))
        assertEquals(10, holder.deserializeValue("container", "10"))
    }

    @Test
    fun forceSetTest() {
        val holder = ValueContainerHolder()
        assertIs<BackInTimeDebuggable>(holder)

        holder.forceSetValue("container", 10)
        assertEquals(10, holder.container.value)
    }
}
