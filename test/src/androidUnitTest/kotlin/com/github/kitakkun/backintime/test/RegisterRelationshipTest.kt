package com.github.kitakkun.backintime.test

import com.github.kitakkun.backintime.runtime.BackInTimeDebugService
import io.mockk.mockkObject
import io.mockk.unmockkAll
import io.mockk.verify
import org.junit.After
import org.junit.Before
import org.junit.Test

@Suppress("TYPE_MISMATCH")
class RegisterRelationshipTest {
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
        val parent = Parent()

        verify(exactly = 1) {
            BackInTimeDebugService.registerRelationship(parent, parent.child)
        }

        parent.accessLazyChild()
        parent.accessLazyChild()

        verify(exactly = 1) {
            BackInTimeDebugService.registerRelationship(parent, parent.lazyChild)
        }
    }
}
