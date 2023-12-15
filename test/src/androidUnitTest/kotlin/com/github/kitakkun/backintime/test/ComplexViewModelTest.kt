package com.github.kitakkun.backintime.test

import com.github.kitakkun.backintime.runtime.BackInTimeDebugService
import io.mockk.coVerifyOrder
import io.mockk.mockkObject
import io.mockk.spyk
import io.mockk.unmockkAll
import io.mockk.verify
import org.junit.After
import org.junit.Before
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.test.Test

@RunWith(RobolectricTestRunner::class)
class ComplexViewModelTest {
    private val viewModel = ComplexViewModel()

    @Before
    fun setup() {
        mockkObject(BackInTimeDebugService)
    }

    @After
    fun teardown() {
        unmockkAll()
    }

    @Test
    fun mutateLiveDataViaWith() {
        viewModel.mutateLiveDataViaWith()

        verify(exactly = 3) {
            BackInTimeDebugService.notifyPropertyChanged(any(), "mutableLiveData", any(), any())
        }
    }

    @Test
    fun mutateStateFlowViaWith() {
        viewModel.mutateStateFlowViaWith()

        verify(exactly = 4) {
            BackInTimeDebugService.notifyPropertyChanged(any(), "mutableStateFlow", any(), any())
        }
    }

    @Test
    fun mutateStateViaWith() {
        viewModel.mutateStateViaWith()

        verify(exactly = 1) {
            BackInTimeDebugService.notifyPropertyChanged(any(), "mutableState", any(), any())
        }
    }

    @Test
    fun mutateLiveDataViaApply() {
        viewModel.mutateLiveDataViaApply()

        verify(exactly = 3) {
            BackInTimeDebugService.notifyPropertyChanged(any(), "mutableLiveData", any(), any())
        }
    }

    @Test
    fun mutateStateFlowViaApply() {
        viewModel.mutateStateFlowViaApply()

        verify(exactly = 4) {
            BackInTimeDebugService.notifyPropertyChanged(any(), "mutableStateFlow", any(), any())
        }
    }

    @Test
    fun mutateStateViaApply() {
        viewModel.mutateStateViaApply()

        verify(exactly = 1) {
            BackInTimeDebugService.notifyPropertyChanged(any(), "mutableState", any(), any())
        }
    }
}
