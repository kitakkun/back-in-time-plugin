package com.github.kitakkun.backintime.test

import org.junit.Test
import kotlin.test.assertEquals

class StateFlowCaptureTest : BackInTimeDebugServiceTest() {
    @Test
    fun testCaptureCount() {
        val viewModel = StateFlowCaptureViewModel()

        viewModel.updateValues()

        val values = propertyValueChangeEvents.map { it.propertyValue }
        val expectedValues = listOf("Hoge", "Fuga", "Piyo", "Foo", "Bar", "Baz")
        assertEquals(expectedValues, values)
    }
}
