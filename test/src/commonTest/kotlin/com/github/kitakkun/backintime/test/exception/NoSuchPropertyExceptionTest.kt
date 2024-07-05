package com.github.kitakkun.backintime.test.exception

import com.github.kitakkun.backintime.annotations.BackInTime
import com.github.kitakkun.backintime.runtime.BackInTimeDebuggable
import com.github.kitakkun.backintime.runtime.exception.BackInTimeRuntimeException
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertIs

class NoSuchPropertyExceptionTest {
    @BackInTime
    private class TestStateHolder {
        val property1: String = "test"
    }

    @Test
    fun testForceSetValue() {
        val holder = TestStateHolder()
        assertIs<BackInTimeDebuggable>(holder)
        assertFailsWith(BackInTimeRuntimeException.NoSuchPropertyException::class) {
            holder.forceSetValue("", "property2", "test")
        }
    }

    @Test
    fun testSerializeValue() {
        val holder = TestStateHolder()
        assertIs<BackInTimeDebuggable>(holder)
        assertFailsWith(BackInTimeRuntimeException.NoSuchPropertyException::class) {
            holder.serializeValue("", "property2", "test")
        }
    }

    @Test
    fun testDeserializeValue() {
        val holder = TestStateHolder()
        assertIs<BackInTimeDebuggable>(holder)
        assertFailsWith(BackInTimeRuntimeException.NoSuchPropertyException::class) {
            holder.deserializeValue("", "property2", "test")
        }
    }
}
