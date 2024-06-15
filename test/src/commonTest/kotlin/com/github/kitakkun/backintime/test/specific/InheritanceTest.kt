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
    @BackInTime
    private open class SuperClass {
        var superProperty: String = "super"
        open var overridableProperty: String = "super"
    }

    @BackInTime
    private class SubClass : SuperClass() {
        var subProperty: String = "sub"
        override var overridableProperty: String = "sub"
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
    }
}
