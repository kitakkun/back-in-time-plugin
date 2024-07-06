package com.github.kitakkun.backintime.test.specific

import com.github.kitakkun.backintime.annotations.BackInTime
import com.github.kitakkun.backintime.annotations.Capture
import com.github.kitakkun.backintime.annotations.Getter
import com.github.kitakkun.backintime.annotations.Setter
import com.github.kitakkun.backintime.annotations.ValueContainer
import com.github.kitakkun.backintime.runtime.BackInTimeDebuggable
import com.github.kitakkun.backintime.test.base.BackInTimeDebugServiceTest
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class AnnotationConfiguredValueContainerTest : BackInTimeDebugServiceTest() {
    companion object {
        private const val CLASS_FQ_NAME = "com.github.kitakkun.backintime.test.specific.AnnotationConfiguredValueContainerTest.ValueContainerHolder"
        private const val CONTAINER_NAME = "container"
    }

    @ValueContainer
    private class AnnotationConfiguredValueContainer<T>(
        @Getter @Setter @Capture var value: T,
    ) {
        @Capture
        fun update(newValue: T) {
            value = newValue
        }
    }

    @BackInTime
    private class ValueContainerHolder {
        val container = AnnotationConfiguredValueContainer(0)

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

        assertEquals("10", holder.serializeValue(propertyOwnerClassFqName = CLASS_FQ_NAME, propertyName = CONTAINER_NAME, value = 10))
        assertEquals(10, holder.deserializeValue(propertyOwnerClassFqName = CLASS_FQ_NAME, propertyName = CONTAINER_NAME, value = "10"))
    }

    @Test
    fun forceSetTest() {
        val holder = ValueContainerHolder()
        assertIs<BackInTimeDebuggable>(holder)

        holder.forceSetValue(propertyOwnerClassFqName = CLASS_FQ_NAME, propertyName = CONTAINER_NAME, value = 10)
        assertEquals(10, holder.container.value)
    }
}
