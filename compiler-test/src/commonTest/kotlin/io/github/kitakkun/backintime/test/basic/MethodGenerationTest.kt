package io.github.kitakkun.backintime.test.basic

import io.github.kitakkun.backintime.annotations.BackInTime
import io.github.kitakkun.backintime.runtime.BackInTimeDebuggable
import io.github.kitakkun.backintime.runtime.exception.BackInTimeRuntimeException
import io.github.kitakkun.backintime.test.base.BackInTimeDebugServiceTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs

class MethodGenerationTest : BackInTimeDebugServiceTest() {
    companion object {
        private const val CLASS_FQ_NAME = "io.github.kitakkun.backintime.test.basic.MethodGenerationTest.TestStateHolder"
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
        assertEquals("10", holder.serializeValue(propertyOwnerClassFqName = CLASS_FQ_NAME, propertyName = PROPERTY_NAME, value = 10))
        assertEquals(10, holder.deserializeValue(propertyOwnerClassFqName = CLASS_FQ_NAME, propertyName = PROPERTY_NAME, value = "10"))

        holder.forceSetValue(propertyOwnerClassFqName = CLASS_FQ_NAME, propertyName = PROPERTY_NAME, value = 10)
        assertEquals(10, holder.property)

        assertFailsWith(BackInTimeRuntimeException.TypeMismatchException::class) {
            holder.forceSetValue(propertyOwnerClassFqName = CLASS_FQ_NAME, propertyName = PROPERTY_NAME, value = "0")
        }
    }
}
