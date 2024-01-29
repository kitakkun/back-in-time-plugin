package com.github.kitakkun.backintime.test.basic

import com.github.kitakkun.backintime.annotations.DebuggableStateHolder
import com.github.kitakkun.backintime.test.base.BackInTimeDebugServiceTest
import org.junit.Assert.assertEquals
import kotlin.test.Test

class RegisterInstanceEventTest : BackInTimeDebugServiceTest() {
    @DebuggableStateHolder
    private class TestStateHolder

    @Test
    fun testRegisterEvent() {
        val instance = TestStateHolder()
        assertEquals(1, registerInstanceEvents.size)
        assertEquals(instance, registerInstanceEvents.single().instance)
    }
}
