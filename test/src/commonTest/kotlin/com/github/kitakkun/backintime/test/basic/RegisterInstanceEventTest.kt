package com.github.kitakkun.backintime.test.basic

import com.github.kitakkun.backintime.annotations.BackInTime
import com.github.kitakkun.backintime.runtime.BackInTimeDebuggable
import com.github.kitakkun.backintime.test.base.BackInTimeDebugServiceTest
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import kotlin.test.Test
import kotlin.test.assertIs

class RegisterInstanceEventTest : BackInTimeDebugServiceTest() {
    @BackInTime
    private class TestStateHolder

    @Test
    fun testRegisterEvent() = runBlocking {
        val instance = TestStateHolder()
        assertIs<BackInTimeDebuggable>(instance)
        delay(100)

        assertEquals(1, registerInstanceEvents.size)
        assertEquals(instance.backInTimeInstanceUUID, registerInstanceEvents.single().instanceUUID)
    }
}
