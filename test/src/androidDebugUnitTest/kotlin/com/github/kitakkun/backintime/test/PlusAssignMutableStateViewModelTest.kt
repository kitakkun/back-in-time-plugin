package com.github.kitakkun.backintime.test

import org.junit.Test
import kotlin.test.assertEquals

class PlusAssignMutableStateViewModelTest : BackInTimeDebugServiceTest() {
    @Test
    fun test() {
        val viewModel = PlusAssignMutableStateViewModel()
        viewModel.test()

        assertEquals(1, propertyValueChangeEvents.size)
    }
}
