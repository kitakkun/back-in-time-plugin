package com.kitakkun.backintime.test.basic

import com.kitakkun.backintime.core.annotations.BackInTime
import com.kitakkun.backintime.test.base.BackInTimeDebugServiceTest
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

        assertEquals(expected = 4, actual = notifyMethodCallEvents.size)
        assertContentEquals(
            expected = listOf(
                "<init>():com/kitakkun/backintime/test/basic/MethodCallEventTest.TestStateHolder",
                "com/kitakkun/backintime/test/basic/MethodCallEventTest.TestStateHolder.method1():kotlin/Unit",
                "com/kitakkun/backintime/test/basic/MethodCallEventTest.TestStateHolder.method2():kotlin/Unit",
                "com/kitakkun/backintime/test/basic/MethodCallEventTest.TestStateHolder.method3():kotlin/Unit",
            ),
            actual = notifyMethodCallEvents.map { it.methodSignature },
        )
    }
}
