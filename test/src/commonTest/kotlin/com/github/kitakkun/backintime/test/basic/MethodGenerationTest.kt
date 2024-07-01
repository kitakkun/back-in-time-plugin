package com.github.kitakkun.backintime.test.basic

import com.github.kitakkun.backintime.annotations.BackInTime
import com.github.kitakkun.backintime.runtime.BackInTimeDebuggable
import com.github.kitakkun.backintime.runtime.exception.BackInTimeRuntimeException
import com.github.kitakkun.backintime.test.base.BackInTimeDebugServiceTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs

class MethodGenerationTest : BackInTimeDebugServiceTest() {
    companion object {
        private const val PROPERTY_FQ_NAME = "com.github.kitakkun.backintime.test.basic.MethodGenerationTest.TestStateHolder.property"
    }

    @BackInTime
    private class TestStateHolder {
        var property: Int = 0
    }

    @Test
    fun test() {
        val holder = TestStateHolder()

        assertIs<BackInTimeDebuggable>(holder)
        assertEquals("10", holder.serializeValue(PROPERTY_FQ_NAME, 10))
        assertEquals(10, holder.deserializeValue(PROPERTY_FQ_NAME, "10"))

        holder.forceSetValue(PROPERTY_FQ_NAME, 10)
        assertEquals(10, holder.property)

        assertFailsWith(BackInTimeRuntimeException.TypeMismatchException::class) {
            holder.forceSetValue(PROPERTY_FQ_NAME, "0")
        }
    }
}
