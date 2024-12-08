package com.kitakkun.backintime.test

import kotlin.test.Test
import kotlin.test.assertEquals

class LiveDataCaptureTest : BackInTimeDebugServiceTest() {
    val viewModel = MutableLiveDataViewModel()

    @Test
    fun test() {
        viewModel.mutateMutableLiveData()

        assertEquals(3, propertyValueChangeEvents.size)
        assertEquals("1", propertyValueChangeEvents[0].value)
        assertEquals("2", propertyValueChangeEvents[1].value)
        assertEquals("3", propertyValueChangeEvents[2].value)
    }
}
