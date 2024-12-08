package com.kitakkun.backintime.test

import kotlin.test.Test
import kotlin.test.assertEquals

class MutableStateViewModelTest : BackInTimeDebugServiceTest() {
    private val viewModel = MutableStateViewModel()

    @Test
    fun mutateMutableIntStateTest() {
        viewModel.mutateMutableIntState()

        assertEquals(2, propertyValueChangeEvents.size)
    }

    @Test
    fun mutateMutableDoubleStateTest() {
        viewModel.mutateMutableDoubleState()

        assertEquals(2, propertyValueChangeEvents.size)
    }

    @Test
    fun mutateMutableFloatStateTest() {
        viewModel.mutateMutableFloatState()

        assertEquals(2, propertyValueChangeEvents.size)
    }

    @Test
    fun mutateMutableLongStateTest() {
        viewModel.mutateMutableLongState()

        assertEquals(2, propertyValueChangeEvents.size)
    }

    @Test
    fun mutateMutableStateListTest() {
        viewModel.mutateMutableStateList()

        assertEquals(1, propertyValueChangeEvents.size)
    }

    @Test
    fun mutateMutableStateMapTest() {
        viewModel.mutateMutableStateMap()

        assertEquals(1, propertyValueChangeEvents.size)
    }
}
