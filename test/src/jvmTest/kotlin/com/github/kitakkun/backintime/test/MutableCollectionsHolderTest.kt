package com.github.kitakkun.backintime.test

import com.github.kitakkun.backintime.runtime.BackInTimeDebugService
import io.mockk.mockkObject
import io.mockk.unmockkAll
import io.mockk.verify
import org.junit.After
import org.junit.Before
import kotlin.test.Test

class MutableCollectionsHolderTest {
    @Before
    fun setup() {
        mockkObject(BackInTimeDebugService)
    }

    @After
    fun teardown() {
        unmockkAll()
    }

    @Test
    fun testMutableListCapture() {
        val mutableCollectionsHolder = MutableCollectionsHolder()
        mutableCollectionsHolder.mutableListTest()

        verify(exactly = 5) {
            BackInTimeDebugService.notifyPropertyChanged(any(), "mutableList", any(), any())
        }
    }

    @Test
    fun testMutableSetCapture() {
        val mutableCollectionsHolder = MutableCollectionsHolder()
        mutableCollectionsHolder.mutableSetTest()

        verify(exactly = 4) {
            BackInTimeDebugService.notifyPropertyChanged(any(), "mutableSet", any(), any())
        }
    }

    @Test
    fun testMutableMapCapture() {
        val mutableCollectionsHolder = MutableCollectionsHolder()
        mutableCollectionsHolder.mutableMapTest()

        verify(exactly = 4) {
            BackInTimeDebugService.notifyPropertyChanged(any(), "mutableMap", any(), any())
        }
    }

    @Test
    fun testMutableListForceSet() {
        val mutableCollectionsHolder = MutableCollectionsHolder()
        mutableCollectionsHolder.forceSetValue("mutableList", mutableListOf("Hello", "World", "!"))
        mutableCollectionsHolder.forceSetValue("mutableList", listOf("Hello", "World"))
    }

    @Test
    fun testMutableSetForceSet() {
        val mutableCollectionsHolder = MutableCollectionsHolder()
        mutableCollectionsHolder.forceSetValue("mutableSet", mutableSetOf("Hello", "World", "!"))
        mutableCollectionsHolder.forceSetValue("mutableSet", setOf("Hello", "World"))
    }

    @Test
    fun testMutableMapForceSet() {
        val mutableCollectionsHolder = MutableCollectionsHolder()
        mutableCollectionsHolder.forceSetValue("mutableMap", mutableMapOf("Hello" to "World", "World" to "!"))
        mutableCollectionsHolder.forceSetValue("mutableMap", mapOf("Hello" to "World", "World" to "!"))
    }
}
