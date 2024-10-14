package io.github.kitakkun.backintime.test.exception

import io.github.kitakkun.backintime.annotations.BackInTime
import io.github.kitakkun.backintime.annotations.Capture
import io.github.kitakkun.backintime.annotations.Getter
import io.github.kitakkun.backintime.annotations.Setter
import io.github.kitakkun.backintime.annotations.ValueContainer
import io.github.kitakkun.backintime.runtime.BackInTimeDebuggable
import io.github.kitakkun.backintime.runtime.exception.BackInTimeRuntimeException
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertIs

class TypeMismatchExceptionTest {
    companion object {
        private const val CLASS_FQ_NAME = "io.github.kitakkun.backintime.test.exception.TypeMismatchExceptionTest.TestStateHolder"
        private const val PROPERTY_NAME = "property"
        private const val VALUE_CONTAINER_PROPERTY_NAME = "valueContainerProperty"
    }

    @ValueContainer
    private class AnnotationConfiguredValueContainer<T>(
        @Getter @Setter @Capture var value: T,
    )

    @BackInTime
    private class TestStateHolder {
        val property: String = ""
        val valueContainerProperty = AnnotationConfiguredValueContainer(0)
    }

    @Test
    fun testNormalProperty() {
        val holder = TestStateHolder()
        assertIs<BackInTimeDebuggable>(holder)
        assertFailsWith(BackInTimeRuntimeException.TypeMismatchException::class) {
            holder.forceSetValue(propertyOwnerClassFqName = CLASS_FQ_NAME, propertyName = PROPERTY_NAME, value = 1)
        }
    }

    @Test
    fun testValueContainerProperty() {
        val holder = TestStateHolder()
        assertIs<BackInTimeDebuggable>(holder)
        assertFailsWith(BackInTimeRuntimeException.TypeMismatchException::class) {
            holder.forceSetValue(propertyOwnerClassFqName = CLASS_FQ_NAME, propertyName = VALUE_CONTAINER_PROPERTY_NAME, value = "test")
        }
    }
}
