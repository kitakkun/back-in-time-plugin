package com.github.kitakkun.backintime.test

import com.github.kitakkun.backintime.runtime.BackInTimeDebugService
import io.mockk.mockkObject
import io.mockk.unmockkAll
import io.mockk.verify
import org.junit.After
import org.junit.Before
import kotlin.test.Test

class MutableStateViewModelTest {
    private val viewModel = MutableStateViewModel()

    @Before
    fun setup() {
        mockkObject(BackInTimeDebugService)
    }

    @After
    fun teardown() {
        unmockkAll()
    }

    @Test
    fun mutateMutableIntStateTest() {
        viewModel.mutateMutableIntState()

        verify(exactly = 2) {
            BackInTimeDebugService.notifyPropertyChanged(any(), "mutableIntState", any(), any())
        }
    }

    @Test
    fun mutateMutableDoubleStateTest() {
        viewModel.mutateMutableDoubleState()

        verify(exactly = 2) {
            BackInTimeDebugService.notifyPropertyChanged(any(), "mutableDoubleState", any(), any())
        }
    }

    @Test
    fun mutateMutableFloatStateTest() {
        viewModel.mutateMutableFloatState()

        verify(exactly = 2) {
            BackInTimeDebugService.notifyPropertyChanged(any(), "mutableFloatState", any(), any())
        }
    }

    @Test
    fun mutateMutableLongStateTest() {
        viewModel.mutateMutableLongState()

        verify(exactly = 2) {
            BackInTimeDebugService.notifyPropertyChanged(any(), "mutableLongState", any(), any())
        }
    }

    @Test
    fun mutateMutableStateListTest() {
        viewModel.mutateMutableStateList()

        verify(exactly = 1) {
            BackInTimeDebugService.notifyPropertyChanged(any(), "mutableStateList", any(), any())
        }
    }

    @Test
    fun mutateMutableStateMapTest() {
        viewModel.mutateMutableStateMap()

        verify(exactly = 1) {
            BackInTimeDebugService.notifyPropertyChanged(any(), "mutableStateMap", any(), any())
        }
    }
}
