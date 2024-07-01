package com.github.kitakkun.backintime.test.specific

import com.github.kitakkun.backintime.annotations.BackInTime
import com.github.kitakkun.backintime.runtime.BackInTimeDebuggable
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class SelfSerializableValueHolderTest {
    companion object {
        private const val MUTABLE_LIST_PROPERTY_FQ_NAME = "com.github.kitakkun.backintime.test.specific.SelfSerializableValueHolderTest.TestStateHolder.mutableList"
    }

    @BackInTime
    private class TestStateHolder {
        val mutableList = mutableListOf<String>()
    }

    @Test
    fun serializeTest() {
        val holder = TestStateHolder()
        assertIs<BackInTimeDebuggable>(holder)

        val input = listOf("Hello")
        val serializedValue = holder.serializeValue(MUTABLE_LIST_PROPERTY_FQ_NAME, input)
        assertEquals("[\"Hello\"]", serializedValue)
    }

    @Test
    fun deserializeTest() {
        val holder = TestStateHolder()
        assertIs<BackInTimeDebuggable>(holder)

        val input = "[\"Hello\"]"
        val deserializedValue = holder.deserializeValue(MUTABLE_LIST_PROPERTY_FQ_NAME, input)
        assertEquals(listOf("Hello"), deserializedValue)
    }

    @Test
    fun forceSetTest() {
        val holder = TestStateHolder()
        assertIs<BackInTimeDebuggable>(holder)

        val input = listOf("Hello")
        holder.forceSetValue(MUTABLE_LIST_PROPERTY_FQ_NAME, input)
        assertEquals(listOf("Hello"), holder.mutableList)
    }
}
