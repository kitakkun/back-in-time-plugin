package com.kitakkun.backintime.test.basic

import com.kitakkun.backintime.core.annotations.BackInTime
import com.kitakkun.backintime.core.runtime.BackInTimeDebuggable
import com.kitakkun.backintime.test.base.BackInTimeDebugServiceTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class MethodGenerationTest : BackInTimeDebugServiceTest() {
    companion object {
        private const val CLASS_FQ_NAME = "com.kitakkun.backintime.test.basic.MethodGenerationTest.TestStateHolder"
        private const val PROPERTY_NAME = "property"
    }

    @BackInTime
    private class TestStateHolder {
        var property: Int = 0
    }

    @Test
    fun test() {
        val holder = TestStateHolder()

        assertIs<BackInTimeDebuggable>(holder)

        holder.forceSetValue(propertyOwnerClassFqName = CLASS_FQ_NAME, propertyName = PROPERTY_NAME, value = "10")
        assertEquals(10, holder.property)
    }
}
