package com.kitakkun.backintime.test.specific

import com.kitakkun.backintime.core.annotations.BackInTime
import com.kitakkun.backintime.core.annotations.Capture
import com.kitakkun.backintime.core.annotations.Getter
import com.kitakkun.backintime.core.annotations.Setter
import com.kitakkun.backintime.core.annotations.TrackableStateHolder
import com.kitakkun.backintime.core.runtime.BackInTimeDebuggable
import com.kitakkun.backintime.test.base.BackInTimeDebugServiceTest
import kotlinx.coroutines.test.runTest
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class ScopeFunctionsTest : BackInTimeDebugServiceTest() {
    @TrackableStateHolder
    private class AnnotationConfiguredTrackableStateHolder<T>(
        @Getter @Setter @Capture var value: T,
    )

    @BackInTime
    private class TrackableStateHolderOwner {
        val container = AnnotationConfiguredTrackableStateHolder(0)

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
        val owner = TrackableStateHolderOwner()

        owner.updateByWith(10)
        owner.assertRequirementsAreMet()
    }

    @Test
    fun applyTest() = runTest {
        val owner = TrackableStateHolderOwner()

        owner.updateByApply(10)
        owner.assertRequirementsAreMet()
    }

    @Test
    fun runTest() = runTest {
        val owner = TrackableStateHolderOwner()

        owner.updateByRun(10)
        owner.assertRequirementsAreMet()
    }

    @Test
    fun letTest() = runTest {
        val owner = TrackableStateHolderOwner()

        owner.updateByLet(10)
        owner.assertRequirementsAreMet()
    }

    @Test
    fun alsoTest() = runTest {
        val owner = TrackableStateHolderOwner()

        owner.updateByAlso(10)
        owner.assertRequirementsAreMet()
    }

    @Ignore("takeIf is not supported yet")
    @Test
    fun takeIfTest() = runTest {
        val owner = TrackableStateHolderOwner()

        owner.updateByTakeIf(10)
        owner.assertRequirementsAreMet()
    }

    @Ignore("takeUnless is not supported yet")
    @Test
    fun takeUnlessTest() = runTest {
        val owner = TrackableStateHolderOwner()

        owner.updateByTakeUnless(10)
        owner.assertRequirementsAreMet()
    }

    private fun TrackableStateHolderOwner.assertRequirementsAreMet() {
        assertIs<BackInTimeDebuggable>(this)
        assertEquals(10, container.value)
        assertEquals(2, notifyValueChangeEvents.size)
        assertEquals(this.backInTimeInstanceUUID, notifyValueChangeEvents[0].instanceUUID)
        assertEquals(this.backInTimeInstanceUUID, notifyValueChangeEvents[1].instanceUUID)
        assertEquals("com/kitakkun/backintime/test/specific/ScopeFunctionsTest.TrackableStateHolderOwner.container", notifyValueChangeEvents[0].propertySignature)
        assertEquals("com/kitakkun/backintime/test/specific/ScopeFunctionsTest.TrackableStateHolderOwner.container", notifyValueChangeEvents[1].propertySignature)
        assertEquals(0, notifyValueChangeEvents[0].value.toInt())
        assertEquals(10, notifyValueChangeEvents[1].value.toInt())
    }
}
