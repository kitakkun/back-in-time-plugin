package io.github.kitakkun.backintime.test.specific

import io.github.kitakkun.backintime.annotations.BackInTime
import io.github.kitakkun.backintime.runtime.BackInTimeDebuggable
import io.github.kitakkun.backintime.test.base.BackInTimeDebugServiceTest
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class InheritanceTest : BackInTimeDebugServiceTest() {
    companion object {
        private const val SUPER_CLASS_FQ_NAME = "io.github.kitakkun.backintime.test.specific.InheritanceTest.SuperClass"
        private const val SUPER_PROPERTY_NAME = "superProperty"
        private const val OVERRIDABLE_PROPERTY_NAME = "overridableProperty"
        private const val PRIVATE_SUPER_PROPERTY_NAME = "privateSuperProperty"
        private const val SUPER_CLASS_CONFLICTED_PRIVATE_PROPERTY_NAME = "conflictedPrivateProperty"
        private const val SUB_CLASS_FQ_NAME = "io.github.kitakkun.backintime.test.specific.InheritanceTest.SubClass"
        private const val SUB_PROPERTY_NAME = "subProperty"
        private const val SUB_CLASS_CONFLICTED_PRIVATE_PROPERTY_NAME = "conflictedPrivateProperty"
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
        assertEquals("io.github.kitakkun.backintime.test.specific.InheritanceTest.SuperClass", registerInstanceEvents[0].className) // super class
        assertEquals(instance.backInTimeInstanceUUID, registerInstanceEvents[1].instanceUUID) // sub class
        assertEquals("io.github.kitakkun.backintime.test.specific.InheritanceTest.SubClass", registerInstanceEvents[1].className) // super class
    }

    @Test
    fun testForceSetPropertyValue() {
        val instance = SubClass()
        assertIs<BackInTimeDebuggable>(instance)

        // super class
        instance.forceSetValue(SUPER_CLASS_FQ_NAME, SUPER_PROPERTY_NAME, "super(modified)")
        assertEquals("super(modified)", instance.superProperty)

        // overriding property
        instance.forceSetValue(SUPER_CLASS_FQ_NAME, OVERRIDABLE_PROPERTY_NAME, "overridable(modified)")
        assertEquals("overridable(modified)", instance.overridableProperty)

        // sub class
        instance.forceSetValue(SUB_CLASS_FQ_NAME, SUB_PROPERTY_NAME, "sub(modified)")
        assertEquals("sub(modified)", instance.subProperty)

        // private property of super class
        instance.forceSetValue(SUPER_CLASS_FQ_NAME, PRIVATE_SUPER_PROPERTY_NAME, "private-super(modified)")
        assertEquals("private-super(modified)", instance.getPrivateSuperProperty())
    }

    @Test
    fun testSerializeValue() {
        val instance = SubClass()
        assertIs<BackInTimeDebuggable>(instance)

        // super class
        assertEquals("\"string\"", instance.serializeValue(SUPER_CLASS_FQ_NAME, SUPER_PROPERTY_NAME, "string"))

        // overriding property
        assertEquals("\"string\"", instance.serializeValue(SUPER_CLASS_FQ_NAME, OVERRIDABLE_PROPERTY_NAME, "string"))

        // sub class
        assertEquals("\"string\"", instance.serializeValue(SUB_CLASS_FQ_NAME, SUB_PROPERTY_NAME, "string"))

        // private property of super class
        assertEquals("\"string\"", instance.serializeValue(SUPER_CLASS_FQ_NAME, PRIVATE_SUPER_PROPERTY_NAME, "string"))
    }

    @Test
    fun testDeserializeValue() {
        val instance = SubClass()
        assertIs<BackInTimeDebuggable>(instance)

        // super class
        assertEquals("string", instance.deserializeValue(SUPER_CLASS_FQ_NAME, SUPER_PROPERTY_NAME, "\"string\""))

        // overriding property
        assertEquals("string", instance.deserializeValue(SUPER_CLASS_FQ_NAME, OVERRIDABLE_PROPERTY_NAME, "\"string\""))

        // sub class
        assertEquals("string", instance.deserializeValue(SUB_CLASS_FQ_NAME, SUB_PROPERTY_NAME, "\"string\""))

        // private property of super class
        assertEquals("string", instance.deserializeValue(SUPER_CLASS_FQ_NAME, PRIVATE_SUPER_PROPERTY_NAME, "\"string\""))
    }

    @Test
    fun conflictedPrivatePropertyTest() {
        val instance = SubClass()
        assertIs<BackInTimeDebuggable>(instance)

        instance.forceSetValue(SUB_CLASS_FQ_NAME, SUB_CLASS_CONFLICTED_PRIVATE_PROPERTY_NAME, "conflict(update-sub)")
        assertEquals(expected = "conflict(update-sub)", actual = instance.getSubPrivateConflictedProperty())
        assertEquals(expected = "conflict", actual = instance.getSuperPrivateConflictedProperty())

        instance.forceSetValue(SUPER_CLASS_FQ_NAME, SUPER_CLASS_CONFLICTED_PRIVATE_PROPERTY_NAME, "conflict(update-super)")
        assertEquals(expected = "conflict(update-sub)", actual = instance.getSubPrivateConflictedProperty())
        assertEquals(expected = "conflict(update-super)", actual = instance.getSuperPrivateConflictedProperty())
    }
}
