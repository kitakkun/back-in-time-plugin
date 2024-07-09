package io.github.kitakkun.backintime.test.specific

import io.github.kitakkun.backintime.annotations.BackInTime
import io.github.kitakkun.backintime.annotations.Capture
import io.github.kitakkun.backintime.annotations.Getter
import io.github.kitakkun.backintime.annotations.Setter
import io.github.kitakkun.backintime.annotations.ValueContainer
import io.github.kitakkun.backintime.runtime.BackInTimeDebuggable
import io.github.kitakkun.backintime.test.base.BackInTimeDebugServiceTest
import kotlinx.coroutines.test.runTest
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class ScopeFunctionsTest : BackInTimeDebugServiceTest() {
    companion object {
        private const val CLASS_FQ_NAME = "io.github.kitakkun.backintime.test.specific.ScopeFunctionsTest.ValueContainerHolder"
        private const val CONTAINER_PROPERTY_NAME = "container"
    }

    @ValueContainer
    private class AnnotationConfiguredValueContainer<T>(
        @Getter @Setter @Capture var value: T,
    )

    @BackInTime
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
    fun withTest() = runTest {
        val holder = ValueContainerHolder()

        holder.updateByWith(10)
        holder.assertRequirementsAreMet()
    }

    @Test
    fun applyTest() = runTest {
        val holder = ValueContainerHolder()

        holder.updateByApply(10)
        holder.assertRequirementsAreMet()
    }

    @Test
    fun runTest() = runTest {
        val holder = ValueContainerHolder()

        holder.updateByRun(10)
        holder.assertRequirementsAreMet()
    }

    @Test
    fun letTest() = runTest {
        val holder = ValueContainerHolder()

        holder.updateByLet(10)
        holder.assertRequirementsAreMet()
    }

    @Test
    fun alsoTest() = runTest {
        val holder = ValueContainerHolder()

        holder.updateByAlso(10)
        holder.assertRequirementsAreMet()
    }

    @Ignore("takeIf is not supported yet")
    @Test
    fun takeIfTest() = runTest {
        val holder = ValueContainerHolder()

        holder.updateByTakeIf(10)
        holder.assertRequirementsAreMet()
    }

    @Ignore("takeUnless is not supported yet")
    @Test
    fun takeUnlessTest() = runTest {
        val holder = ValueContainerHolder()

        holder.updateByTakeUnless(10)
        holder.assertRequirementsAreMet()
    }

    private fun ValueContainerHolder.assertRequirementsAreMet() {
        assertIs<BackInTimeDebuggable>(this)
        assertEquals(10, container.value)
        assertEquals(1, notifyValueChangeEvents.size)
        assertEquals(this.backInTimeInstanceUUID, notifyValueChangeEvents[0].instanceUUID)
        assertEquals(CLASS_FQ_NAME, notifyValueChangeEvents[0].ownerClassFqName)
        assertEquals(CONTAINER_PROPERTY_NAME, notifyValueChangeEvents[0].propertyName)
        assertEquals(10, notifyValueChangeEvents[0].value.toInt())
    }
}
