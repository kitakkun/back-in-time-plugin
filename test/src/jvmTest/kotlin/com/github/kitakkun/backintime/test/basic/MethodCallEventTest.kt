package com.github.kitakkun.backintime.test.basic

import com.github.kitakkun.backintime.annotations.DebuggableStateHolder
import com.github.kitakkun.backintime.runtime.BackInTimeDebuggable
import com.github.kitakkun.backintime.test.base.BackInTimeDebugServiceTest
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertTrue

/**
 * Checks if method calls are captured correctly.
 */
@Suppress("CAST_NEVER_SUCCEEDS")
class MethodCallEventTest : BackInTimeDebugServiceTest() {
    @DebuggableStateHolder
    private class TestStateHolder {
        fun method1() {}
        fun method2() {}
        fun method3() {}
    }

    @Test
    fun test() = runTest {
        val holder = TestStateHolder()

        holder.method1()
        holder.method2()
        holder.method3()

        assertTrue { methodCallEvents.all { it.instance == holder as BackInTimeDebuggable } }
        assertContentEquals(methodCallEvents.map { it.methodName }, listOf("method1", "method2", "method3"))
    }
}
