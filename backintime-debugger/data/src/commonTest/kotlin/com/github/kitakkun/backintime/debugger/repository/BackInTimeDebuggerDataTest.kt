package com.github.kitakkun.backintime.debugger.repository

import org.junit.After
import org.junit.Before
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest

abstract class BackInTimeDebuggerDataTest : KoinTest {
    @Before
    fun setup() {
        startKoin {
            modules(testDataModule)
        }
    }

    @After
    fun tearDown() {
        stopKoin()
    }
}
