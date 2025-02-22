package com.kitakkun.backintime.feature.inspector.model

import com.kitakkunl.backintime.feature.inspector.model.Signature
import kotlin.test.Test
import kotlin.test.assertEquals

class SignatureTest {
    @Test
    fun testClass() {
        val classSignature = Signature.Class("com/example/MyClass")
        val nestedClassSignature = Signature.Class("com/example/MyClass.Nested")
        assertEquals("com/example", classSignature.packageFqName)
        assertEquals("com/example", nestedClassSignature.packageFqName)
        assertEquals("MyClass", classSignature.className)
        assertEquals("MyClass.Nested", nestedClassSignature.className)
    }

    @Test
    fun testProperty() {
        val propertySignature = Signature.Property("com/example/MyClass.prop1")
        assertEquals("com/example", propertySignature.packageFqName)
        assertEquals("prop1", propertySignature.propertyName)
    }

    @Test
    fun testFunction() {
        val functionSignature = Signature.Function("com/example/MyClass.method1():kotlin/Unit")
        assertEquals("com/example", functionSignature.packageFqName)
        assertEquals("method1", functionSignature.functionName)
        assertEquals("MyClass", functionSignature.parentClassName)
        assertEquals("com/example/MyClass", functionSignature.parentClassFqName)
        assertEquals(null, functionSignature.extensionReceiverClassName)
        assertEquals(null, functionSignature.extensionReceiverClassFqName)

        val extensionFunctionSignature = Signature.Function("com/example/MyClass com/example/ext/extensionMethod1():kotlin/Unit")
        assertEquals("com/example/ext", extensionFunctionSignature.packageFqName)
        assertEquals("extensionMethod1", extensionFunctionSignature.functionName)
        assertEquals(null, extensionFunctionSignature.parentClassName)
        assertEquals(null, extensionFunctionSignature.parentClassFqName)
        assertEquals("MyClass", extensionFunctionSignature.extensionReceiverClassName)
        assertEquals("com/example/MyClass", extensionFunctionSignature.extensionReceiverClassFqName)
    }
}
