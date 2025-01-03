package com.kitakkun.backintime.test.specific

import com.kitakkun.backintime.core.annotations.BackInTime
import com.kitakkun.backintime.core.annotations.Capture
import com.kitakkun.backintime.core.annotations.Getter
import com.kitakkun.backintime.core.annotations.Setter
import com.kitakkun.backintime.core.annotations.ValueContainer
import com.kitakkun.backintime.core.runtime.BackInTimeDebuggable
import com.kitakkun.backintime.test.base.BackInTimeDebugServiceTest
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class AnnotationConfiguredValueContainerTest : BackInTimeDebugServiceTest() {
    companion object {
        private const val CLASS_FQ_NAME = "com.kitakkun.backintime.test.specific.AnnotationConfiguredValueContainerTest.ValueContainerHolder"
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
    fun forceSetTest() {
        val holder = ValueContainerHolder()
        assertIs<BackInTimeDebuggable>(holder)

        holder.forceSetValue(propertyOwnerClassFqName = CLASS_FQ_NAME, propertyName = CONTAINER_NAME, value = "10")
        assertEquals(10, holder.container.value)
    }
}
