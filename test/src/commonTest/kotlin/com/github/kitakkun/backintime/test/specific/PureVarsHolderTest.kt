package com.github.kitakkun.backintime.test.specific

import com.github.kitakkun.backintime.annotations.BackInTime
import com.github.kitakkun.backintime.runtime.BackInTimeDebuggable
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class PureVarsHolderTest {
    companion object {
        private const val HOLDER_CLASS_FQ_NAME = "com.github.kitakkun.backintime.test.specific.PureVarsHolderTest.PureVarsHolder"
        private fun String.toFQN() = "$HOLDER_CLASS_FQ_NAME.$this"
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
    fun serializeNonNullFields() {
        val holder = PureVarsHolder()
        assertIs<BackInTimeDebuggable>(holder)

        assertEquals("\"hogehoge\"", holder.serializeValue("string".toFQN(), "hogehoge"))
        assertEquals("1000", holder.serializeValue("int".toFQN(), 1000))
        assertEquals("10000000000", holder.serializeValue("long".toFQN(), 10000000000L))
        assertEquals("0.1", holder.serializeValue("float".toFQN(), 0.1f))
        assertEquals("0.001", holder.serializeValue("double".toFQN(), 0.001))
        assertEquals("true", holder.serializeValue("boolean".toFQN(), true))
        assertEquals("\"b\"", holder.serializeValue("char".toFQN(), 'b'))
        assertEquals("100", holder.serializeValue("short".toFQN(), 100.toShort()))
        assertEquals("100", holder.serializeValue("byte".toFQN(), 100.toByte()))
    }

    @Test
    fun serializeNonNullCollectionFields() {
        val holder = PureVarsHolder()
        assertIs<BackInTimeDebuggable>(holder)

        assertEquals("[\"string value\"]", holder.serializeValue("stringList".toFQN(), listOf("string value")))
        assertEquals("[0]", holder.serializeValue("intList".toFQN(), listOf(0)))
        assertEquals("[0]", holder.serializeValue("longList".toFQN(), listOf(0L)))
        assertEquals("[0.0]", holder.serializeValue("floatList".toFQN(), listOf(0f)))
        assertEquals("[0.0]", holder.serializeValue("doubleList".toFQN(), listOf(0.0)))
        assertEquals("[false]", holder.serializeValue("booleanList".toFQN(), listOf(false)))
    }

    @Test
    fun serializeNullableFields() {
        val holder = PureVarsHolder()
        assertIs<BackInTimeDebuggable>(holder)

        assertEquals("null", holder.serializeValue("nullableString".toFQN(), null))
        assertEquals("null", holder.serializeValue("nullableInt".toFQN(), null))
        assertEquals("null", holder.serializeValue("nullableLong".toFQN(), null))
        assertEquals("null", holder.serializeValue("nullableFloat".toFQN(), null))
        assertEquals("null", holder.serializeValue("nullableDouble".toFQN(), null))
        assertEquals("null", holder.serializeValue("nullableBoolean".toFQN(), null))
        assertEquals("null", holder.serializeValue("nullableChar".toFQN(), null))
        assertEquals("null", holder.serializeValue("nullableShort".toFQN(), null))
        assertEquals("null", holder.serializeValue("nullableByte".toFQN(), null))
    }

    @Test
    fun deserializeNonNullFields() {
        val holder = PureVarsHolder()
        assertIs<BackInTimeDebuggable>(holder)

        assertEquals("hogehoge", holder.deserializeValue("string".toFQN(), "\"hogehoge\""))
        assertEquals(0, holder.deserializeValue("int".toFQN(), "0"))
        assertEquals(0L, holder.deserializeValue("long".toFQN(), "0"))
        assertEquals(0f, holder.deserializeValue("float".toFQN(), "0.0"))
        assertEquals(0.0, holder.deserializeValue("double".toFQN(), "0.0"))
        assertEquals(false, holder.deserializeValue("boolean".toFQN(), "false"))
        assertEquals('a', holder.deserializeValue("char".toFQN(), "\"a\""))
        assertEquals(0.toShort(), holder.deserializeValue("short".toFQN(), "0"))
        assertEquals(0.toByte(), holder.deserializeValue("byte".toFQN(), "0"))
    }

    @Test
    fun deserializeNonNullCollectionFields() {
        val holder = PureVarsHolder()
        assertIs<BackInTimeDebuggable>(holder)
        assertEquals(listOf("string value"), holder.deserializeValue("stringList".toFQN(), "[\"string value\"]"))
        assertEquals(listOf(0), holder.deserializeValue("intList".toFQN(), "[0]"))
        assertEquals(listOf(0L), holder.deserializeValue("longList".toFQN(), "[0]"))
        assertEquals(listOf(0f), holder.deserializeValue("floatList".toFQN(), "[0.0]"))
        assertEquals(listOf(0.0), holder.deserializeValue("doubleList".toFQN(), "[0.0]"))
        assertEquals(listOf(false), holder.deserializeValue("booleanList".toFQN(), "[false]"))
    }

    @Test
    fun deserializeNullableFields() {
        val holder = PureVarsHolder()
        assertIs<BackInTimeDebuggable>(holder)
        assertEquals(null, holder.deserializeValue("nullableString".toFQN(), "null"))
        assertEquals(null, holder.deserializeValue("nullableInt".toFQN(), "null"))
        assertEquals(null, holder.deserializeValue("nullableLong".toFQN(), "null"))
        assertEquals(null, holder.deserializeValue("nullableFloat".toFQN(), "null"))
        assertEquals(null, holder.deserializeValue("nullableDouble".toFQN(), "null"))
        assertEquals(null, holder.deserializeValue("nullableBoolean".toFQN(), "null"))
        assertEquals(null, holder.deserializeValue("nullableChar".toFQN(), "null"))
        assertEquals(null, holder.deserializeValue("nullableShort".toFQN(), "null"))
        assertEquals(null, holder.deserializeValue("nullableByte".toFQN(), "null"))
    }

    @Test
    fun forceSet() {
        val holder = PureVarsHolder()
        assertIs<BackInTimeDebuggable>(holder)

        holder.forceSetValue("string".toFQN(), "hogehoge")
        assertEquals("hogehoge", holder.string)
        holder.forceSetValue("int".toFQN(), 0)
        assertEquals(0, holder.int)
        holder.forceSetValue("long".toFQN(), 0L)
        assertEquals(0L, holder.long)
        holder.forceSetValue("float".toFQN(), 0f)
        assertEquals(0f, holder.float)
        holder.forceSetValue("double".toFQN(), 0.0)
        assertEquals(0.0, holder.double)
        holder.forceSetValue("boolean".toFQN(), false)
        assertEquals(false, holder.boolean)
        holder.forceSetValue("char".toFQN(), 'a')
        assertEquals('a', holder.char)
        holder.forceSetValue("short".toFQN(), 0.toShort())
        assertEquals(0.toShort(), holder.short)
        holder.forceSetValue("byte".toFQN(), 0.toByte())
        assertEquals(0.toByte(), holder.byte)
        holder.forceSetValue("nullableString".toFQN(), null)
        assertEquals(null, holder.nullableString)
        holder.forceSetValue("nullableInt".toFQN(), null)
        assertEquals(null, holder.nullableInt)
        holder.forceSetValue("nullableLong".toFQN(), null)
        assertEquals(null, holder.nullableLong)
        holder.forceSetValue("nullableFloat".toFQN(), null)
        assertEquals(null, holder.nullableFloat)
        holder.forceSetValue("nullableDouble".toFQN(), null)
        assertEquals(null, holder.nullableDouble)
        holder.forceSetValue("nullableBoolean".toFQN(), null)
        assertEquals(null, holder.nullableBoolean)
        holder.forceSetValue("nullableChar".toFQN(), null)
        assertEquals(null, holder.nullableChar)
        holder.forceSetValue("nullableShort".toFQN(), null)
        assertEquals(null, holder.nullableShort)
        holder.forceSetValue("nullableByte".toFQN(), null)
        assertEquals(null, holder.nullableByte)
    }
}
