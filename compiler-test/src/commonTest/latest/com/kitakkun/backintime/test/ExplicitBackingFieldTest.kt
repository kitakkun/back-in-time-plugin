package com.kitakkun.backintime.test

import com.kitakkun.backintime.core.annotations.BackInTime
import com.kitakkun.backintime.test.base.BackInTimeDebugServiceTest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlin.test.Test
import kotlin.test.assertEquals

class ExplicitBackingFieldTest : BackInTimeDebugServiceTest() {
    @BackInTime
    class StateHolderWithExplicitBackingField {
        val stateFlow: StateFlow<Int>
            field = MutableStateFlow(0)

        fun updateState(value: Int) {
            stateFlow.update { value }
        }
    }

    @Test
    fun test() {
        val stateHolder = StateHolderWithExplicitBackingField()
        stateHolder.updateState(42)

        assertEquals(1, this.notifyValueChangeEvents.size)
        assertEquals("42", this.notifyValueChangeEvents.single().value)
    }
}
