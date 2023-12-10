package com.github.kitakkun.backintime.test

import org.junit.Test
import kotlin.test.assertEquals

class PureVarsHolderTest {
    private val pureVarsHolder = PureVarsHolder()

    @Test
    fun serialize() {
        assertEquals("\"hogehoge\"", pureVarsHolder.serializeValue("string", "hogehoge"))
        assertEquals("1000", pureVarsHolder.serializeValue("int", 1000))
        assertEquals("10000000000", pureVarsHolder.serializeValue("long", 10000000000L))
        assertEquals("0.1", pureVarsHolder.serializeValue("float", 0.1f))
        assertEquals("0.001", pureVarsHolder.serializeValue("double", 0.001))
        assertEquals("true", pureVarsHolder.serializeValue("boolean", true))
        assertEquals("\"b\"", pureVarsHolder.serializeValue("char", 'b'))
        assertEquals("100", pureVarsHolder.serializeValue("short", 100.toShort()))
        assertEquals("100", pureVarsHolder.serializeValue("byte", 100.toByte()))
    }

    @Test
    fun deserialize() {
        assertEquals("hogehoge", pureVarsHolder.deserializeValue("string", "\"hogehoge\""))
        assertEquals(0, pureVarsHolder.deserializeValue("int", "0"))
        assertEquals(0L, pureVarsHolder.deserializeValue("long", "0"))
        assertEquals(0f, pureVarsHolder.deserializeValue("float", "0.0"))
        assertEquals(0.0, pureVarsHolder.deserializeValue("double", "0.0"))
        assertEquals(false, pureVarsHolder.deserializeValue("boolean", "false"))
        assertEquals('a', pureVarsHolder.deserializeValue("char", "\"a\""))
        assertEquals(0.toShort(), pureVarsHolder.deserializeValue("short", "0"))
        assertEquals(0.toByte(), pureVarsHolder.deserializeValue("byte", "0"))
    }

    @Test
    fun forceSet() {
        pureVarsHolder.forceSetValue("string", "hogehoge")
        assertEquals("hogehoge", pureVarsHolder.string)
        pureVarsHolder.forceSetValue("int", 0)
        assertEquals(0, pureVarsHolder.int)
        pureVarsHolder.forceSetValue("long", 0L)
        assertEquals(0L, pureVarsHolder.long)
        pureVarsHolder.forceSetValue("float", 0f)
        assertEquals(0f, pureVarsHolder.float)
        pureVarsHolder.forceSetValue("double", 0.0)
        assertEquals(0.0, pureVarsHolder.double)
        pureVarsHolder.forceSetValue("boolean", false)
        assertEquals(false, pureVarsHolder.boolean)
        pureVarsHolder.forceSetValue("char", 'a')
        assertEquals('a', pureVarsHolder.char)
        pureVarsHolder.forceSetValue("short", 0.toShort())
        assertEquals(0.toShort(), pureVarsHolder.short)
        pureVarsHolder.forceSetValue("byte", 0.toByte())
        assertEquals(0.toByte(), pureVarsHolder.byte)
    }
}
