package com.kitakkun.backintime.compiler.backend.utils

import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.declarations.IrProperty
import org.jetbrains.kotlin.ir.types.classOrNull
import org.jetbrains.kotlin.ir.util.classId
import org.jetbrains.kotlin.ir.util.parentClassOrNull

/**
 * ```kotlin
 * package com.example
 *
 * class A {
 *     val prop: Int = 10
 * }
 * ```
 *
 * The signature for `prop` will be: com/example/A.prop
 * Note that extension properties are not supported because of no-need for debugging(We can't define no-backing-field extension properties for now).
 */
fun IrProperty.signatureForBackInTimeDebugger(): String {
    return StringBuilder().apply {
        parentClassOrNull?.classId?.asString()?.let {
            this.append(it)
            this.append(".")
        }
        append(this@signatureForBackInTimeDebugger.name.asString())
    }.toString()
}

/**
 * ```kotlin
 * package com.example
 *
 * class A {
 *     fun member(a: Int): String {
 *         ...
 *     }
 * }
 * ```
 *
 * The signature for `member` will be: "com/example/A.member(kotlin/Int):kotlin/String"
 *
 * Note:
 * If the function has an extensionReceiver, the signature above will follow after the signature for the extensionReceiver class.
 *
 * ```kotlin
 * package com.example
 *
 * class A {
 *     fun B.member(a: Int): String {
 *         ...
 *     }
 * }
 *
 * class B
 * ```
 *
 * The signature for `member` will be: "com/example/B com/example/A.member(kotlin/Int):kotlin/String"
 */
fun IrFunction.signatureForBackInTimeDebugger(): String {
    val extensionReceiverSignature = extensionReceiverParameter?.type?.classOrNull?.owner?.signatureForBackInTimeDebugger()
    val dispatchReceiverSignature = dispatchReceiverParameter?.type?.classOrNull?.owner?.signatureForBackInTimeDebugger()
    val valueParametersSignature = valueParameters
        .map { it.type.classOrNull?.owner?.signatureForBackInTimeDebugger()!! }
        .joinToString(",")
    val returnTypeSignature = returnType.classOrNull?.owner?.signatureForBackInTimeDebugger()!!

    return StringBuilder().apply {
        extensionReceiverSignature?.let {
            append(it)
            append(" ")
        }
        dispatchReceiverSignature?.let {
            append(it)
            append(".")
        }
        append(name.asString())
        append("(")
        append(valueParametersSignature)
        append(")")
        append(":")
        append(returnTypeSignature)
    }.toString()
}

/**
 *```kotlin
 * package com.example
 *
 * class A {
 *     class B
 * }
 * ```
 *
 * The signature for `B` will be: com/example/A.B
 */
fun IrClass.signatureForBackInTimeDebugger(): String {
    return classId?.asString() ?: "unknown"
}
