package com.github.kitakkun.backintime.demo

import org.junit.Test
import kotlin.test.assertEquals

class PureVarsHolderTest {
    private val pureVarsHolder = PureVarsHolder()

    @Test
    fun serialize() {
        assertEquals("\"hogehoge\"", pureVarsHolder.serializePropertyValueForBackInTimeDebug("string", "hogehoge"))
        assertEquals("0", pureVarsHolder.serializePropertyValueForBackInTimeDebug("int", 0))
        assertEquals("0", pureVarsHolder.serializePropertyValueForBackInTimeDebug("long", 0L))
        assertEquals("0.0", pureVarsHolder.serializePropertyValueForBackInTimeDebug("float", 0f))
        assertEquals("0.0", pureVarsHolder.serializePropertyValueForBackInTimeDebug("double", 0.0))
        assertEquals("false", pureVarsHolder.serializePropertyValueForBackInTimeDebug("boolean", false))
        assertEquals("\"a\"", pureVarsHolder.serializePropertyValueForBackInTimeDebug("char", 'a'))
        assertEquals("0", pureVarsHolder.serializePropertyValueForBackInTimeDebug("short", 0.toShort()))
        assertEquals("0", pureVarsHolder.serializePropertyValueForBackInTimeDebug("byte", 0.toByte()))
    }

    @Test
    fun deserialize() {
        assertEquals("hogehoge", pureVarsHolder.deserializePropertyValueForBackInTimeDebug("string", "\"hogehoge\""))
        assertEquals(0, pureVarsHolder.deserializePropertyValueForBackInTimeDebug("int", "0"))
        assertEquals(0L, pureVarsHolder.deserializePropertyValueForBackInTimeDebug("long", "0"))
        assertEquals(0f, pureVarsHolder.deserializePropertyValueForBackInTimeDebug("float", "0.0"))
        assertEquals(0.0, pureVarsHolder.deserializePropertyValueForBackInTimeDebug("double", "0.0"))
        assertEquals(false, pureVarsHolder.deserializePropertyValueForBackInTimeDebug("boolean", "false"))
        assertEquals('a', pureVarsHolder.deserializePropertyValueForBackInTimeDebug("char", "\"a\""))
        assertEquals(0.toShort(), pureVarsHolder.deserializePropertyValueForBackInTimeDebug("short", "0"))
        assertEquals(0.toByte(), pureVarsHolder.deserializePropertyValueForBackInTimeDebug("byte", "0"))
    }

    @Test
    fun forceSet() {
        pureVarsHolder.forceSetPropertyValueForBackInTimeDebug("string", "hogehoge")
        assertEquals("hogehoge", pureVarsHolder.string)
        pureVarsHolder.forceSetPropertyValueForBackInTimeDebug("int", 0)
        assertEquals(0, pureVarsHolder.int)
        pureVarsHolder.forceSetPropertyValueForBackInTimeDebug("long", 0L)
        assertEquals(0L, pureVarsHolder.long)
        pureVarsHolder.forceSetPropertyValueForBackInTimeDebug("float", 0f)
        assertEquals(0f, pureVarsHolder.float)
        pureVarsHolder.forceSetPropertyValueForBackInTimeDebug("double", 0.0)
        assertEquals(0.0, pureVarsHolder.double)
        pureVarsHolder.forceSetPropertyValueForBackInTimeDebug("boolean", false)
        assertEquals(false, pureVarsHolder.boolean)
        pureVarsHolder.forceSetPropertyValueForBackInTimeDebug("char", 'a')
        assertEquals('a', pureVarsHolder.char)
        pureVarsHolder.forceSetPropertyValueForBackInTimeDebug("short", 0.toShort())
        assertEquals(0.toShort(), pureVarsHolder.short)
        pureVarsHolder.forceSetPropertyValueForBackInTimeDebug("byte", 0.toByte())
        assertEquals(0.toByte(), pureVarsHolder.byte)
    }
}
