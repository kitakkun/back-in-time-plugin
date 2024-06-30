package com.github.kitakkun.backintime.test.basic

import com.github.kitakkun.backintime.annotations.BackInTime
import com.github.kitakkun.backintime.runtime.BackInTimeDebuggable
import com.github.kitakkun.backintime.test.base.BackInTimeDebugServiceTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class MethodGenerationTest : BackInTimeDebugServiceTest() {
    @BackInTime
    private class TestStateHolder {
        var property: Int = 0
    }

    @Test
    fun test() {
        val holder = TestStateHolder()

        assertIs<BackInTimeDebuggable>(holder)
        assertEquals("10", holder.serializeValue("property", 10))
        assertEquals(10, holder.deserializeValue("property", "10"))

        holder.forceSetValue("property", 10)
        assertEquals(10, holder.property)

        // This code should throw an exception but not implemented yet.
//        holder.forceSetValue("property", "0")
    }
}
