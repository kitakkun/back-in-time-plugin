package com.kitakkun.backintime.test

import org.junit.Test
import kotlin.test.assertEquals

class PlusAssignMutableStateViewModelTest : BackInTimeDebugServiceTest() {
    @Test
    fun test() {
        val viewModel = PlusAssignMutableStateViewModel()
        viewModel.test()

        assertEquals(2, propertyValueChangeEvents.size)
    }
}
