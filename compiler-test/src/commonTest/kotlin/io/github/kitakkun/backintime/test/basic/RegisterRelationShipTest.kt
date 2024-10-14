package io.github.kitakkun.backintime.test.basic

import io.github.kitakkun.backintime.annotations.BackInTime
import io.github.kitakkun.backintime.runtime.BackInTimeDebuggable
import io.github.kitakkun.backintime.test.base.BackInTimeDebugServiceTest
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

/**
 * Checks if relationship between state holders are captured as expected.
 */
class RegisterRelationShipTest : BackInTimeDebugServiceTest() {
    @BackInTime
    private class ParentTestStateHolderWithNormalChild {
        val child = ChildTestStateHolder()
    }

    @BackInTime
    private class ParentTestStateHolderWithLazyChild {
        val lazyChild by lazy { ChildTestStateHolder() }
        fun accessLazyChild() = lazyChild
    }

    @BackInTime
    private class ChildTestStateHolder

    @Test
    fun testNormalChild() = runBlocking {
        val parent = ParentTestStateHolderWithNormalChild()
        assertIs<BackInTimeDebuggable>(parent)
        assertIs<BackInTimeDebuggable>(parent.child)

        delay(100)

        assertEquals(expected = 1, actual = registerRelationshipEvents.size)
        assertEquals(expected = parent.backInTimeInstanceUUID, actual = registerRelationshipEvents[0].parentUUID)
        assertEquals(expected = parent.child.backInTimeInstanceUUID, actual = registerRelationshipEvents[0].childUUID)
    }

    @Test
    fun testLazyChildExternalAccess() = runBlocking {
        val parent = ParentTestStateHolderWithLazyChild()
        assertIs<BackInTimeDebuggable>(parent)

        delay(100)
        assertEquals(0, registerRelationshipEvents.size)

        // access from external scope can trigger relationship event
        parent.lazyChild
        delay(100)
        assertEquals(1, registerRelationshipEvents.size)
    }

    @Test
    fun testLazyChildInternalAccess() = runBlocking {
        val parent = ParentTestStateHolderWithLazyChild()
        assertIs<BackInTimeDebuggable>(parent)

        delay(100)
        assertEquals(0, registerRelationshipEvents.size)

        // access internally can trigger relationship event
        parent.accessLazyChild()
        delay(100)
        assertEquals(expected = 1, actual = registerRelationshipEvents.size)
        assertEquals(expected = parent.backInTimeInstanceUUID, actual = registerRelationshipEvents[0].parentUUID)
        val child = parent.lazyChild
        assertIs<BackInTimeDebuggable>(child)
        assertEquals(expected = child.backInTimeInstanceUUID, actual = registerRelationshipEvents[0].childUUID)

        // access again, but never trigger relationship event
        parent.accessLazyChild()
        delay(100)
        assertEquals(1, registerRelationshipEvents.size)
    }
}
