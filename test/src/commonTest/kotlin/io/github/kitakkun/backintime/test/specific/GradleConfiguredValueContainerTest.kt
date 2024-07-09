package io.github.kitakkun.backintime.test.specific

import io.github.kitakkun.backintime.annotations.BackInTime
import io.github.kitakkun.backintime.runtime.BackInTimeDebuggable
import io.github.kitakkun.backintime.test.base.BackInTimeDebugServiceTest
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class GradleConfiguredValueContainerTest : BackInTimeDebugServiceTest() {
    companion object {
        private const val CLASS_FQ_NAME = "io.github.kitakkun.backintime.test.specific.GradleConfiguredValueContainerTest.ValueContainerHolder"
        private const val CONTAINER_NAME = "container"
    }

    /**
     * backInTime {
     *     enabled = true
     *     valueContainers {
     *         ...
     *         container {
     *             className = "io/github/kitakkun/backintime/test/specific/GradleConfiguredValueContainerTest.GradleConfiguredValueContainer"
     *             captures = listOf("<set-value>", "update")
     *             getter = "<get-value>"
     *             setter = "<set-value>"
     *         }
     *     }
     * }
     */
    private class GradleConfiguredValueContainer<T>(
        var value: T,
    ) {
        fun update(newValue: T) {
            value = newValue
        }
    }

    @BackInTime
    private class ValueContainerHolder {
        val container = GradleConfiguredValueContainer(0)

        fun updateContainerValue(value: Int) {
            container.value = value
        }
    }

    @Test
    fun captureTest() = runBlocking {
        val holder = ValueContainerHolder()
        assertIs<BackInTimeDebuggable>(holder)

        holder.updateContainerValue(10)

        delay(100)

        assertEquals(10, holder.container.value)
        assertEquals(1, notifyValueChangeEvents.size)
        assertEquals(holder.backInTimeInstanceUUID, notifyValueChangeEvents[0].instanceUUID)
        assertEquals(CONTAINER_NAME, notifyValueChangeEvents[0].propertyName)
        assertEquals(10, notifyValueChangeEvents[0].value.toInt())
    }

    @Test
    fun serializeTest() {
        val holder = ValueContainerHolder()
        assertIs<BackInTimeDebuggable>(holder)

        assertEquals("10", holder.serializeValue(CLASS_FQ_NAME, CONTAINER_NAME, 10))
        assertEquals(10, holder.deserializeValue(CLASS_FQ_NAME, CONTAINER_NAME, "10"))
    }

    @Test
    fun forceSetTest() {
        val holder = ValueContainerHolder()
        assertIs<BackInTimeDebuggable>(holder)

        holder.forceSetValue(CLASS_FQ_NAME, CONTAINER_NAME, 10)
        assertEquals(10, holder.container.value)
    }
}
