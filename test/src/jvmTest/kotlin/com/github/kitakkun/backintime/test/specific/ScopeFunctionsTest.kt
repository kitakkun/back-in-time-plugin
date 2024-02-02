package com.github.kitakkun.backintime.test.specific

import com.github.kitakkun.backintime.annotations.Capture
import com.github.kitakkun.backintime.annotations.DebuggableStateHolder
import com.github.kitakkun.backintime.annotations.Getter
import com.github.kitakkun.backintime.annotations.Setter
import com.github.kitakkun.backintime.annotations.ValueContainer
import com.github.kitakkun.backintime.runtime.BackInTimeDebuggable
import com.github.kitakkun.backintime.test.base.BackInTimeDebugServiceTest
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class ScopeFunctionsTest : BackInTimeDebugServiceTest() {
    @ValueContainer
    private class AnnotationConfiguredValueContainer<T>(@Getter @Setter @Capture var value: T)

    @DebuggableStateHolder
    private class ValueContainerHolder {
        val container = AnnotationConfiguredValueContainer(0)

        fun updateByWith(value: Int) {
            with(container) {
                this.value = value
            }
        }

        fun updateByApply(value: Int) {
            container.apply {
                this.value = value
            }
        }

        fun updateByRun(value: Int) {
            container.run {
                this.value = value
            }
        }

        fun updateByLet(value: Int) {
            container.let {
                it.value = value
            }
        }

        fun updateByAlso(value: Int) {
            container.also {
                it.value = value
            }
        }

        fun updateByTakeIf(value: Int) {
            container.takeIf { true }?.value = value
        }

        fun updateByTakeUnless(value: Int) {
            container.takeUnless { false }?.value = value
        }
    }

    @Test
    fun withTest() {
        val holder = ValueContainerHolder()

        holder.updateByWith(10)
        holder.assertRequirementsAreMet()
    }

    @Test
    fun applyTest() {
        val holder = ValueContainerHolder()

        holder.updateByApply(10)
        holder.assertRequirementsAreMet()
    }

    @Test
    fun runTest() {
        val holder = ValueContainerHolder()

        holder.updateByRun(10)
        holder.assertRequirementsAreMet()
    }

    @Test
    fun letTest() {
        val holder = ValueContainerHolder()

        holder.updateByLet(10)
        holder.assertRequirementsAreMet()
    }

    @Test
    fun alsoTest() {
        val holder = ValueContainerHolder()

        holder.updateByAlso(10)
        holder.assertRequirementsAreMet()
    }

    @Ignore("takeIf is not supported yet")
    @Test
    fun takeIfTest() {
        val holder = ValueContainerHolder()

        holder.updateByTakeIf(10)
        holder.assertRequirementsAreMet()
    }

    @Ignore("takeUnless is not supported yet")
    @Test
    fun takeUnlessTest() {
        val holder = ValueContainerHolder()

        holder.updateByTakeUnless(10)
        holder.assertRequirementsAreMet()
    }

    private fun ValueContainerHolder.assertRequirementsAreMet() {
        assertIs<BackInTimeDebuggable>(this)
        assertEquals(10, container.value)
        assertEquals(1, propertyValueChangeEvents.size)
        assertEquals(this, propertyValueChangeEvents[0].instance)
        assertEquals("container", propertyValueChangeEvents[0].propertyName)
        assertEquals(10, propertyValueChangeEvents[0].propertyValue)
    }
}
