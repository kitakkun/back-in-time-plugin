package com.github.kitakkun.backintime.test

import com.github.kitakkun.backintime.runtime.BackInTimeDebugService
import io.mockk.mockkObject
import io.mockk.unmockkAll
import io.mockk.verify
import org.junit.After
import org.junit.Before
import org.junit.Test

class PlusAssignMutableStateViewModelTest {
    @Before
    fun setup() {
        mockkObject(BackInTimeDebugService)
    }

    @After
    fun teardown() {
        unmockkAll()
    }

    @Test
    fun test() {
        val viewModel = PlusAssignMutableStateViewModel()
        viewModel.test()

        verify {
            BackInTimeDebugService.notifyPropertyChanged(any(), "mutableStrings", any(), any())
        }
    }
}
