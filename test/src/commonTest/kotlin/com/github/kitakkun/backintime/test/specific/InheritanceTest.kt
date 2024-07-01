package com.github.kitakkun.backintime.test.specific

import com.github.kitakkun.backintime.annotations.BackInTime
import com.github.kitakkun.backintime.runtime.BackInTimeDebuggable
import com.github.kitakkun.backintime.test.base.BackInTimeDebugServiceTest
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class InheritanceTest : BackInTimeDebugServiceTest() {
    companion object {
        private const val SUPER_CLASS_SUPER_PROPERTY_FQ_NAME = "com.github.kitakkun.backintime.test.specific.InheritanceTest.SuperClass.superProperty"
        private const val SUPER_CLASS_OVERRIDABLE_PROPERTY_FQ_NAME = "com.github.kitakkun.backintime.test.specific.InheritanceTest.SuperClass.overridableProperty"
        private const val SUPER_CLASS_PRIVATE_SUPER_PROPERTY_FQ_NAME = "com.github.kitakkun.backintime.test.specific.InheritanceTest.SuperClass.privateSuperProperty"
        private const val SUPER_CLASS_CONFLICTED_PRIVATE_PROPERTY_FQ_NAME = "com.github.kitakkun.backintime.test.specific.InheritanceTest.SuperClass.conflictedPrivateProperty"
        private const val SUB_CLASS_SUB_PROPERTY_FQ_NAME = "com.github.kitakkun.backintime.test.specific.InheritanceTest.SubClass.subProperty"
        private const val SUB_CLASS_CONFLICTED_PRIVATE_PROPERTY_FQ_NAME = "com.github.kitakkun.backintime.test.specific.InheritanceTest.SubClass.conflictedPrivateProperty"
    }

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
        instance.forceSetValue(SUPER_CLASS_SUPER_PROPERTY_FQ_NAME, "super(modified)")
        assertEquals("super(modified)", instance.superProperty)

        // overriding property
        instance.forceSetValue(SUPER_CLASS_OVERRIDABLE_PROPERTY_FQ_NAME, "overridable(modified)")
        assertEquals("overridable(modified)", instance.overridableProperty)

        // sub class
        instance.forceSetValue(SUB_CLASS_SUB_PROPERTY_FQ_NAME, "sub(modified)")
        assertEquals("sub(modified)", instance.subProperty)

        // private property of super class
        instance.forceSetValue(SUPER_CLASS_PRIVATE_SUPER_PROPERTY_FQ_NAME, "private-super(modified)")
        assertEquals("private-super(modified)", instance.getPrivateSuperProperty())
    }

    @Test
    fun testSerializeValue() {
        val instance = SubClass()
        assertIs<BackInTimeDebuggable>(instance)

        // super class
        assertEquals("\"string\"", instance.serializeValue(SUPER_CLASS_SUPER_PROPERTY_FQ_NAME, "string"))

        // overriding property
        assertEquals("\"string\"", instance.serializeValue(SUPER_CLASS_OVERRIDABLE_PROPERTY_FQ_NAME, "string"))

        // sub class
        assertEquals("\"string\"", instance.serializeValue(SUB_CLASS_SUB_PROPERTY_FQ_NAME, "string"))

        // private property of super class
        assertEquals("\"string\"", instance.serializeValue(SUPER_CLASS_PRIVATE_SUPER_PROPERTY_FQ_NAME, "string"))
    }

    @Test
    fun testDeserializeValue() {
        val instance = SubClass()
        assertIs<BackInTimeDebuggable>(instance)

        // super class
        assertEquals("string", instance.deserializeValue(SUPER_CLASS_SUPER_PROPERTY_FQ_NAME, "\"string\""))

        // overriding property
        assertEquals("string", instance.deserializeValue(SUPER_CLASS_OVERRIDABLE_PROPERTY_FQ_NAME, "\"string\""))

        // sub class
        assertEquals("string", instance.deserializeValue(SUB_CLASS_SUB_PROPERTY_FQ_NAME, "\"string\""))

        // private property of super class
        assertEquals("string", instance.deserializeValue(SUPER_CLASS_PRIVATE_SUPER_PROPERTY_FQ_NAME, "\"string\""))
    }

    @Test
    fun conflictedPrivatePropertyTest() {
        val instance = SubClass()
        assertIs<BackInTimeDebuggable>(instance)

        instance.forceSetValue(SUB_CLASS_CONFLICTED_PRIVATE_PROPERTY_FQ_NAME, "conflict(update-sub)")
        assertEquals(expected = "conflict(update-sub)", actual = instance.getSubPrivateConflictedProperty())
        assertEquals(expected = "conflict", actual = instance.getSuperPrivateConflictedProperty())

        instance.forceSetValue(SUPER_CLASS_CONFLICTED_PRIVATE_PROPERTY_FQ_NAME, "conflict(update-super)")
        assertEquals(expected = "conflict(update-sub)", actual = instance.getSubPrivateConflictedProperty())
        assertEquals(expected = "conflict(update-super)", actual = instance.getSuperPrivateConflictedProperty())
    }
}
