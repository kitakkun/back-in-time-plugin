package com.github.kitakkun.backintime

import com.github.kitakkun.backintime.runtime.BackInTimeDebugService
import org.junit.Test
import java.lang.Thread.sleep
import kotlin.test.assertEquals

class BackInTimeDebugServiceTest {
    class TestClass

    @Test
    fun testIfInstanceAutomaticallyUnregistered() {
        val service = BackInTimeDebugService
        var instance: TestClass? = TestClass()
        service.register(instance!!)
        assertEquals(expected = 1, actual = service.instances.size)
        instance = null
        System.gc()
        sleep(1000)
        assertEquals(expected = 0, actual = service.instances.size)
    }
}
