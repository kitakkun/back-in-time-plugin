package com.kitakkun.backintime.test.specific

import com.kitakkun.backintime.core.annotations.BackInTime
import com.kitakkun.backintime.core.runtime.BackInTimeDebuggable
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class SelfSerializableValueHolderTest {
    companion object {
        private const val CLASS_FQ_NAME = "com.kitakkun.backintime.test.specific.SelfSerializableValueHolderTest.TestStateHolder"
        private const val MUTABLE_LIST_PROPERTY_NAME = "mutableList"
    }

    @BackInTime
    private class TestStateHolder {
        val mutableList = mutableListOf<String>()
    }

    @Test
    fun forceSetTest() {
        val holder = TestStateHolder()
        assertIs<BackInTimeDebuggable>(holder)

        holder.forceSetValue(CLASS_FQ_NAME, MUTABLE_LIST_PROPERTY_NAME, "[\"Hello\"]")
        assertEquals(listOf("Hello"), holder.mutableList)
    }
}
