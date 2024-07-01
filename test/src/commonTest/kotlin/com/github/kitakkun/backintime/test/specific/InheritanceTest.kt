package com.github.kitakkun.backintime.test.specific

import com.github.kitakkun.backintime.annotations.BackInTime
import com.github.kitakkun.backintime.runtime.BackInTimeDebuggable
import com.github.kitakkun.backintime.test.base.BackInTimeDebugServiceTest
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue
import kotlin.test.expect

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
        assertEquals("com.github.kitakkun.backintime.test.specific.InheritanceTest.SuperClass", registerInstanceEvents[0].className) // super class
        assertEquals(instance.backInTimeInstanceUUID, registerInstanceEvents[1].instanceUUID) // sub class
        assertEquals("com.github.kitakkun.backintime.test.specific.InheritanceTest.SubClass", registerInstanceEvents[1].className) // super class
    }

    @Test
    fun testForceSetPropertyValue() {
        val instance = SubClass()
        assertIs<BackInTimeDebuggable>(instance)

        // super class
        instance.forceSetValue("superProperty", "super(modified)")
        assertEquals("super(modified)", instance.superProperty)

        // overriding property
        instance.forceSetValue("overridableProperty", "overridable(modified)")
        assertEquals("overridable(modified)", instance.overridableProperty)

        // sub class
        instance.forceSetValue("subProperty", "sub(modified)")
        assertEquals("sub(modified)", instance.subProperty)

        // private property of super class
        instance.forceSetValue("privateSuperProperty", "private-super(modified)")
        assertEquals("private-super(modified)", instance.getPrivateSuperProperty())
    }

    @Test
    fun testSerializeValue() {
        val instance = SubClass()
        assertIs<BackInTimeDebuggable>(instance)

        // super class
        assertEquals("\"string\"", instance.serializeValue("superProperty", "string"))

        // overriding property
        assertEquals("\"string\"", instance.serializeValue("overridableProperty", "string"))

        // sub class
        assertEquals("\"string\"", instance.serializeValue("subProperty", "string"))

        // private property of super class
        assertEquals("\"string\"", instance.serializeValue("privateSuperProperty", "string"))
    }

    @Test
    fun testDeserializeValue() {
        val instance = SubClass()
        assertIs<BackInTimeDebuggable>(instance)

        // super class
        assertEquals("string", instance.deserializeValue("superProperty", "\"string\""))

        // overriding property
        assertEquals("string", instance.deserializeValue("overridableProperty", "\"string\""))

        // sub class
        assertEquals("string", instance.deserializeValue("subProperty", "\"string\""))

        // private property of super class
        assertEquals("string", instance.deserializeValue("privateSuperProperty", "\"string\""))
    }

    @Test
    fun conflictedPrivatePropertyTest() {
        val instance = SubClass()
        assertIs<BackInTimeDebuggable>(instance)

        // FIXME: This test should pass, but it's not an intended behavior.
        //  If the names have conflicts, superClass one is no longer debuggable.
        instance.forceSetValue("conflictedPrivateProperty", "conflict(update)")
        assertEquals(
            expected = "conflict(update)",
            actual = instance.getSubPrivateConflictedProperty(),
        )
        assertEquals(
            expected = "conflict",
            actual = instance.getSuperPrivateConflictedProperty(),
        )
    }
}
