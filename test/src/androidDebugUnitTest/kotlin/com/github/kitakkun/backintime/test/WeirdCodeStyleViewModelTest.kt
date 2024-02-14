package com.github.kitakkun.backintime.test

import org.junit.Test
import kotlin.test.assertEquals

class WeirdCodeStyleViewModelTest : BackInTimeDebugServiceTest() {
    private val viewModel = WeirdCodeStyleViewModel()

    @Test
    fun mutateLiveData() {
        viewModel.mutateLiveData()

        assertEquals(7, propertyValueChangeEvents.filter { it.propertyName == "mutableLiveData1" }.size)
        assertEquals(1, propertyValueChangeEvents.filter { it.propertyName == "mutableLiveData2" }.size)
    }

    @Test
    fun mutateStateFlow() {
        viewModel.mutateStateFlow()

        assertEquals(2, propertyValueChangeEvents.filter { it.propertyName == "mutableStateFlow" }.size)
    }

    @Test
    fun mutateState() {
        viewModel.mutateState()

        assertEquals(1, propertyValueChangeEvents.filter { it.propertyName == "mutableState" }.size)
    }
}
