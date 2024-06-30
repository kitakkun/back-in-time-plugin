package com.github.kitakkun.backintime.test.basic

import com.github.kitakkun.backintime.annotations.BackInTime
import com.github.kitakkun.backintime.test.base.BackInTimeDebugServiceTest
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

/**
 * Checks if method calls are captured correctly.
 */
class MethodCallEventTest : BackInTimeDebugServiceTest() {
    @BackInTime
    private class TestStateHolder {
        fun method1() {}
        fun method2() {}
        fun method3() {}
    }

    @Test
    fun test() = runBlocking {
        val holder = TestStateHolder()

        holder.method1()
        holder.method2()
        holder.method3()

        delay(100)

        assertEquals(expected = 3, actual = notifyMethodCallEvents.size)
        assertContentEquals(
            expected = listOf("method1", "method2", "method3"),
            actual = notifyMethodCallEvents.map { it.methodName },
        )
    }
}
