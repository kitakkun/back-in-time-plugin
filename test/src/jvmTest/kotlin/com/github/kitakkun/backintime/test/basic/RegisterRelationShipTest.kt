package com.github.kitakkun.backintime.test.basic

import com.github.kitakkun.backintime.annotations.DebuggableStateHolder
import com.github.kitakkun.backintime.test.base.BackInTimeDebugServiceTest
import org.junit.Assert.assertEquals
import kotlin.test.Test

/**
 * Checks if relationship between state holders are captured as expected.
 */
class RegisterRelationShipTest : BackInTimeDebugServiceTest() {
    @DebuggableStateHolder
    private class ParentTestStateHolderWithNormalChild {
        val child = ChildTestStateHolder()
    }

    @DebuggableStateHolder
    private class ParentTestStateHolderWithLazyChild {
        val lazyChild by lazy { ChildTestStateHolder() }
        fun accessLazyChild() = lazyChild
    }

    @DebuggableStateHolder
    private class ChildTestStateHolder

    @Test
    fun testNormalChild() {
        val parent = ParentTestStateHolderWithNormalChild()

        assertEquals(1, registerRelationShipEvents.size)
        assertEquals(parent, registerRelationShipEvents[0].parentInstance)
        assertEquals(parent.child, registerRelationShipEvents[0].childInstance)
    }

    @Test
    fun testLazyChild() {
        val parent = ParentTestStateHolderWithLazyChild()
        assertEquals(1, registerInstanceEvents.size)

        // access from external scope can't trigger relationship event
        parent.lazyChild
        assertEquals(2, registerInstanceEvents.size)
        assertEquals(0, registerRelationShipEvents.size)

        // access internally can trigger relationship event
        parent.accessLazyChild()
        assertEquals(1, registerRelationShipEvents.size)
        assertEquals(parent, registerRelationShipEvents[0].parentInstance)
        assertEquals(parent.lazyChild, registerRelationShipEvents[0].childInstance)

        // access again, but never trigger relationship event
        parent.accessLazyChild()
        assertEquals(1, registerRelationShipEvents.size)
    }
}
