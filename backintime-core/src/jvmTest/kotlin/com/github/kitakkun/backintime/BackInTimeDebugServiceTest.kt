package com.github.kitakkun.backintime

import com.github.kitakkun.backintime.runtime.BackInTimeDebugService
import com.github.kitakkun.backintime.runtime.BackInTimeDebuggable
import com.github.kitakkun.backintime.runtime.InstanceInfo
import org.junit.Test
import java.lang.Thread.sleep
import kotlin.test.assertEquals

class BackInTimeDebugServiceTest {
    class TestClass : BackInTimeDebuggable {
        override fun deserializeValue(propertyName: String, value: String): Any? {
            TODO("Not yet implemented")
        }

        override fun forceSetValue(propertyName: String, value: Any?) {
            TODO("Not yet implemented")
        }

        override fun serializeValue(propertyName: String, value: Any?): String {
            TODO("Not yet implemented")
        }
    }

    @Test
    fun testIfInstanceAutomaticallyUnregistered() {
        val service = BackInTimeDebugService
        var instance: TestClass? = TestClass()
        service.register(instance!!, InstanceInfo("test", emptyList(), "test"))
        assertEquals(expected = 1, actual = service.instances.size)
        instance = null
        System.gc()
        sleep(1000)
        assertEquals(expected = 0, actual = service.instances.size)
    }
}
