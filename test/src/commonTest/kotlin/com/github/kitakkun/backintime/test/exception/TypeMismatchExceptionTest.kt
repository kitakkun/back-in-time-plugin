package com.github.kitakkun.backintime.test.exception

import com.github.kitakkun.backintime.annotations.Capture
import com.github.kitakkun.backintime.annotations.DebuggableStateHolder
import com.github.kitakkun.backintime.annotations.Getter
import com.github.kitakkun.backintime.annotations.Setter
import com.github.kitakkun.backintime.annotations.ValueContainer
import com.github.kitakkun.backintime.runtime.BackInTimeDebuggable
import com.github.kitakkun.backintime.runtime.exception.BackInTimeRuntimeException
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertIs

class TypeMismatchExceptionTest {
    @ValueContainer
    private class AnnotationConfiguredValueContainer<T>(
        @Getter @Setter @Capture var value: T,
    )

    @DebuggableStateHolder
    private class TestStateHolder {
        val property: String = ""
        val valueContainerProperty = AnnotationConfiguredValueContainer(0)
    }

    @Test
    fun testNormalProperty() {
        val holder = TestStateHolder()
        assertIs<BackInTimeDebuggable>(holder)
        assertFailsWith(BackInTimeRuntimeException.TypeMismatchException::class) {
            holder.forceSetValue("property", 1)
        }
    }

    @Test
    fun testValueContainerProperty() {
        val holder = TestStateHolder()
        assertIs<BackInTimeDebuggable>(holder)
        assertFailsWith(BackInTimeRuntimeException.TypeMismatchException::class) {
            holder.forceSetValue("valueContainerProperty", "test")
        }
    }
}
