package com.kitakkun.backintime.test.exception

import com.kitakkun.backintime.core.annotations.BackInTime
import com.kitakkun.backintime.core.runtime.BackInTimeDebuggable
import com.kitakkun.backintime.core.runtime.exception.BackInTimeRuntimeException
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
            holder.forceSetValue("com/kitakkun/backintime/test/exception/NoSuchPropertyExceptionTest/TestStateHolder.property2", "test")
        }
    }
}
