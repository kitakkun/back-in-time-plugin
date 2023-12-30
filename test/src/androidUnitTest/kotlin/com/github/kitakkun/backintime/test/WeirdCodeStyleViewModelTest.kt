package com.github.kitakkun.backintime.test

import com.github.kitakkun.backintime.runtime.BackInTimeDebugService
import io.mockk.mockkObject
import io.mockk.unmockkAll
import io.mockk.verify
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class WeirdCodeStyleViewModelTest {
    private val viewModel = WeirdCodeStyleViewModel()

    @Before
    fun setup() {
        mockkObject(BackInTimeDebugService)
    }

    @After
    fun teardown() {
        unmockkAll()
    }

    @Test
    fun mutateLiveData() {
        viewModel.mutateLiveData()

        verify(exactly = 7) {
            BackInTimeDebugService.notifyPropertyChanged(any(), "mutableLiveData1", any(), any())
        }
        verify(exactly = 1) {
            BackInTimeDebugService.notifyPropertyChanged(any(), "mutableLiveData2", any(), any())
        }
    }

    @Test
    fun mutateStateFlow() {
        viewModel.mutateStateFlow()

        verify(exactly = 2) {
            BackInTimeDebugService.notifyPropertyChanged(any(), "mutableStateFlow", any(), any())
        }
    }

    @Test
    fun mutateState() {
        viewModel.mutateState()

        verify(exactly = 1) {
            BackInTimeDebugService.notifyPropertyChanged(any(), "mutableState", any(), any())
        }
    }
}
