package com.github.kitakkun.backintime.test.specific

import com.github.kitakkun.backintime.annotations.DebuggableStateHolder
import com.github.kitakkun.backintime.runtime.BackInTimeDebuggable
import com.github.kitakkun.backintime.test.base.BackInTimeDebugServiceTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class InheritanceTest : BackInTimeDebugServiceTest() {
    @DebuggableStateHolder
    private open class SuperClass {
        var superProperty: String = "super"
        open var overridableProperty: String = "super"
    }

    @DebuggableStateHolder
    private class SubClass : SuperClass() {
        var subProperty: String = "sub"
        override var overridableProperty: String = "sub"
    }

    @Test
    fun testInstanceRegistration() {
        val instance = SubClass()
        assertIs<BackInTimeDebuggable>(instance)

        assertEquals(2, registerInstanceEvents.size)
        assertEquals(instance, registerInstanceEvents[0].instance) // super class
        assertEquals(instance, registerInstanceEvents[1].instance) // sub class
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
    }
}
