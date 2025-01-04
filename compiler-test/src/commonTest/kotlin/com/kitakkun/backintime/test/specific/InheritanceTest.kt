package com.kitakkun.backintime.test.specific

import com.kitakkun.backintime.core.annotations.BackInTime
import com.kitakkun.backintime.core.runtime.BackInTimeDebuggable
import com.kitakkun.backintime.test.base.BackInTimeDebugServiceTest
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class InheritanceTest : BackInTimeDebugServiceTest() {
    @BackInTime
    private open class SuperClass {
        var superProperty: String = "super"
        open var overridableProperty: String = "super"

        private var privateSuperProperty = "private-super"
        private var conflictedPrivateProperty = "conflict"
        fun getPrivateSuperProperty() = privateSuperProperty
        fun getSuperPrivateConflictedProperty() = conflictedPrivateProperty
    }

    @BackInTime
    private class SubClass : SuperClass() {
        var subProperty: String = "sub"
        override var overridableProperty: String = "sub"
        private var conflictedPrivateProperty = "conflict"
        fun getSubPrivateConflictedProperty() = conflictedPrivateProperty
    }

    @Test
    fun testInstanceRegistration() = runBlocking {
        val instance = SubClass()
        assertIs<BackInTimeDebuggable>(instance)

        delay(100)

        assertEquals(2, registerInstanceEvents.size)
        assertEquals(instance.backInTimeInstanceUUID, registerInstanceEvents[0].instanceUUID) // super class
        assertEquals("com/kitakkun/backintime/test/specific/InheritanceTest.SuperClass", registerInstanceEvents[0].classSignature) // super class
        assertEquals(instance.backInTimeInstanceUUID, registerInstanceEvents[1].instanceUUID) // sub class
        assertEquals("com/kitakkun/backintime/test/specific/InheritanceTest.SubClass", registerInstanceEvents[1].classSignature) // super class
    }

    @Test
    fun testForceSetPropertyValue() {
        val instance = SubClass()
        assertIs<BackInTimeDebuggable>(instance)

        // super class
        instance.forceSetValue("com/kitakkun/backintime/test/specific/InheritanceTest.SuperClass.superProperty", "\"super(modified)\"")
        assertEquals("super(modified)", instance.superProperty)

        // overriding property
        instance.forceSetValue("com/kitakkun/backintime/test/specific/InheritanceTest.SuperClass.overridableProperty", "\"overridable(modified)\"")
        assertEquals("overridable(modified)", instance.overridableProperty)

        // sub class
        instance.forceSetValue("com/kitakkun/backintime/test/specific/InheritanceTest.SubClass.subProperty", "\"sub(modified)\"")
        assertEquals("sub(modified)", instance.subProperty)

        // private property of super class
        instance.forceSetValue("com/kitakkun/backintime/test/specific/InheritanceTest.SuperClass.privateSuperProperty", "\"private-super(modified)\"")
        assertEquals("private-super(modified)", instance.getPrivateSuperProperty())
    }

    @Test
    fun conflictedPrivatePropertyTest() {
        val instance = SubClass()
        assertIs<BackInTimeDebuggable>(instance)

        instance.forceSetValue("com/kitakkun/backintime/test/specific/InheritanceTest.SubClass.conflictedPrivateProperty", "\"conflict(update-sub)\"")
        assertEquals(expected = "conflict(update-sub)", actual = instance.getSubPrivateConflictedProperty())
        assertEquals(expected = "conflict", actual = instance.getSuperPrivateConflictedProperty())

        instance.forceSetValue("com/kitakkun/backintime/test/specific/InheritanceTest.SuperClass.conflictedPrivateProperty", "\"conflict(update-super)\"")
        assertEquals(expected = "conflict(update-sub)", actual = instance.getSubPrivateConflictedProperty())
        assertEquals(expected = "conflict(update-super)", actual = instance.getSuperPrivateConflictedProperty())
    }
}
