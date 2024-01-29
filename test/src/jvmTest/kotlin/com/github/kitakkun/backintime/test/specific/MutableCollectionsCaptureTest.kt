package com.github.kitakkun.backintime.test.specific

import com.github.kitakkun.backintime.annotations.DebuggableStateHolder
import com.github.kitakkun.backintime.runtime.BackInTimeDebuggable
import com.github.kitakkun.backintime.test.base.BackInTimeDebugServiceTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class MutableCollectionsCaptureTest : BackInTimeDebugServiceTest() {
    @DebuggableStateHolder
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
    fun testMutableListCapture() {
        val holder = MutableCollectionsHolder()
        holder.mutableListTest()
        assertIs<BackInTimeDebuggable>(holder)

        assertEquals(6, propertyValueChangeEvents.size)
    }

    @Test
    fun testMutableSetCapture() {
        val holder = MutableCollectionsHolder()
        holder.mutableSetTest()
        assertIs<BackInTimeDebuggable>(holder)

        assertEquals(4, propertyValueChangeEvents.size)
    }

    @Test
    fun testMutableMapCapture() {
        val holder = MutableCollectionsHolder()
        holder.mutableMapTest()

        assertIs<BackInTimeDebuggable>(holder)
        assertEquals(6, propertyValueChangeEvents.size)
    }
}
