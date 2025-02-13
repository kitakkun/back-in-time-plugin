package com.kitakkun.backintime.test.specific

import com.kitakkun.backintime.core.annotations.BackInTime
import com.kitakkun.backintime.core.runtime.BackInTimeDebuggable
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class PureVarsHolderTest {
    companion object {
        private const val HOLDER_CLASS_FQ_NAME = "com/kitakkun/backintime/test/specific/PureVarsHolderTest.PureVarsHolder"
    }

    @BackInTime
    private class PureVarsHolder {
        var string: String = "string value"
        var int: Int = 0
        var long: Long = 0L
        var float: Float = 0f
        var double: Double = 0.0
        var boolean: Boolean = false
        var char: Char = 'a'
        var short: Short = 0
        var byte: Byte = 0
        var nullableString: String? = null
        var nullableInt: Int? = null
        var nullableLong: Long? = null
        var nullableFloat: Float? = null
        var nullableDouble: Double? = null
        var nullableBoolean: Boolean? = null
        var nullableChar: Char? = null
        var nullableShort: Short? = null
        var nullableByte: Byte? = null
        var stringList: List<String> = listOf("string value")
        var intList: List<Int> = listOf(0)
        var longList: List<Long> = listOf(0L)
        var floatList: List<Float> = listOf(0f)
        var doubleList: List<Double> = listOf(0.0)
        var booleanList: List<Boolean> = listOf(false)
    }

    @Test
    fun forceSet() {
        val holder = PureVarsHolder()
        assertIs<BackInTimeDebuggable>(holder)

        holder.forceSetValue("$HOLDER_CLASS_FQ_NAME.string", "\"hogehoge\"")
        assertEquals("hogehoge", holder.string)
        holder.forceSetValue("$HOLDER_CLASS_FQ_NAME.int", "0")
        assertEquals(0, holder.int)
        holder.forceSetValue("$HOLDER_CLASS_FQ_NAME.long", "0")
        assertEquals(0L, holder.long)
        holder.forceSetValue("$HOLDER_CLASS_FQ_NAME.float", "0")
        assertEquals(0f, holder.float)
        holder.forceSetValue("$HOLDER_CLASS_FQ_NAME.double", "0.0")
        assertEquals(0.0, holder.double)
        holder.forceSetValue("$HOLDER_CLASS_FQ_NAME.boolean", "false")
        assertEquals(false, holder.boolean)
        holder.forceSetValue("$HOLDER_CLASS_FQ_NAME.char", "a")
        assertEquals('a', holder.char)
        holder.forceSetValue("$HOLDER_CLASS_FQ_NAME.short", "0")
        assertEquals(0.toShort(), holder.short)
        holder.forceSetValue("$HOLDER_CLASS_FQ_NAME.byte", "0")
        assertEquals(0.toByte(), holder.byte)
        holder.forceSetValue("$HOLDER_CLASS_FQ_NAME.nullableString", "null")
        assertEquals(null, holder.nullableString)
        holder.forceSetValue("$HOLDER_CLASS_FQ_NAME.nullableInt", "null")
        assertEquals(null, holder.nullableInt)
        holder.forceSetValue("$HOLDER_CLASS_FQ_NAME.nullableLong", "null")
        assertEquals(null, holder.nullableLong)
        holder.forceSetValue("$HOLDER_CLASS_FQ_NAME.nullableFloat", "null")
        assertEquals(null, holder.nullableFloat)
        holder.forceSetValue("$HOLDER_CLASS_FQ_NAME.nullableDouble", "null")
        assertEquals(null, holder.nullableDouble)
        holder.forceSetValue("$HOLDER_CLASS_FQ_NAME.nullableBoolean", "null")
        assertEquals(null, holder.nullableBoolean)
        holder.forceSetValue("$HOLDER_CLASS_FQ_NAME.nullableChar", "null")
        assertEquals(null, holder.nullableChar)
        holder.forceSetValue("$HOLDER_CLASS_FQ_NAME.nullableShort", "null")
        assertEquals(null, holder.nullableShort)
        holder.forceSetValue("$HOLDER_CLASS_FQ_NAME.nullableByte", "null")
        assertEquals(null, holder.nullableByte)
    }
}
