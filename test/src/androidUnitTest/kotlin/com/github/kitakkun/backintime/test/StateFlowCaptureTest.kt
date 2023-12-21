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
class StateFlowCaptureTest {
    @Before
    fun setup() {
        mockkObject(BackInTimeDebugService)
    }

    @After
    fun teardown() {
        unmockkAll()
    }

    @Test
    fun testCaptureCount() {
        val viewModel = StateFlowCaptureViewModel()

        viewModel.updateValues()

        verify {
            BackInTimeDebugService.notifyPropertyChanged(any(), "mutableStateFlow", "Hoge", any())
            BackInTimeDebugService.notifyPropertyChanged(any(), "mutableStateFlow", "Fuga", any())
            BackInTimeDebugService.notifyPropertyChanged(any(), "mutableStateFlow", "Piyo", any())
            BackInTimeDebugService.notifyPropertyChanged(any(), "mutableStateFlow", "Foo", any())
            BackInTimeDebugService.notifyPropertyChanged(any(), "mutableStateFlow", "Bar", any())
            BackInTimeDebugService.notifyPropertyChanged(any(), "mutableStateFlow", "Baz", any())
        }
    }
}
