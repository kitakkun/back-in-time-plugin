package com.kitakkun.backintime.test.specific

import com.kitakkun.backintime.core.annotations.BackInTime
import com.kitakkun.backintime.core.runtime.BackInTimeDebuggable
import com.kitakkun.backintime.test.base.BackInTimeDebugServiceTest
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class MutableCollectionsCaptureTest : BackInTimeDebugServiceTest() {
    @BackInTime
    private class MutableCollectionsHolder {
        private val mutableList = mutableListOf<String>()
        private val mutableSet = mutableSetOf<String>()
        private val mutableMap = mutableMapOf<String, String>()

        fun mutableListTest() {
            mutableList.add("Hello")
            mutableList.addAll(listOf("World", "!"))
            mutableList.replaceAll { it }
            mutableList.removeAt(1)
            mutableList.remove("!")
            mutableList.clear()
        }

        fun mutableSetTest() {
            mutableSet.add("Hello")
            mutableSet.addAll(listOf("World", "!"))
            mutableSet.remove("!")
            mutableSet.clear()
        }

        fun mutableMapTest() {
            mutableMap["Hello"] = "World"
            mutableMap["World"] = "!"
            mutableMap.replace("World", "!")
            mutableMap.replaceAll { _, value -> value }
            mutableMap.remove("!")
            mutableMap.clear()
        }
    }

    @Test
    fun testMutableListCapture() = runBlocking {
        val holder = MutableCollectionsHolder()
        assertIs<BackInTimeDebuggable>(holder)

        holder.mutableListTest()
        delay(100)

        assertEquals(6, notifyValueChangeEvents.size)
    }

    @Test
    fun testMutableSetCapture() = runBlocking {
        val holder = MutableCollectionsHolder()
        assertIs<BackInTimeDebuggable>(holder)

        holder.mutableSetTest()
        delay(100)

        assertEquals(4, notifyValueChangeEvents.size)
    }

    @Test
    fun testMutableMapCapture() = runBlocking {
        val holder = MutableCollectionsHolder()
        assertIs<BackInTimeDebuggable>(holder)

        holder.mutableMapTest()
        delay(100)

        assertEquals(6, notifyValueChangeEvents.size)
    }
}
