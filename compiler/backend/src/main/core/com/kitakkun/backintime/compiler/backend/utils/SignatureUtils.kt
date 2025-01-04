package com.kitakkun.backintime.compiler.backend.utils

import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.declarations.IrProperty
import org.jetbrains.kotlin.ir.symbols.IrClassSymbol
import org.jetbrains.kotlin.ir.symbols.IrTypeParameterSymbol
import org.jetbrains.kotlin.ir.types.IrSimpleType
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.types.classifierOrNull
import org.jetbrains.kotlin.ir.types.typeOrNull
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
        parentClassOrNull?.signatureForBackInTimeDebugger()?.let {
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
    val extensionReceiverSignature = extensionReceiverParameter?.type?.signatureForBackInTimeDebugger()
    val dispatchReceiverSignature = dispatchReceiverParameter?.type?.signatureForBackInTimeDebugger()
    val valueParametersSignature = valueParameters
        .map { it.type.signatureForBackInTimeDebugger() }
        .joinToString(",")
    val returnTypeSignature = returnType.signatureForBackInTimeDebugger()

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

fun IrType.signatureForBackInTimeDebugger(): String {
    val typeArguments = (this as? IrSimpleType)?.arguments.orEmpty().mapNotNull {
        it.typeOrNull?.signatureForBackInTimeDebugger()
    }

    return StringBuilder().apply {
        append(
            when (val classifierOrNull = classifierOrNull) {
                is IrTypeParameterSymbol -> classifierOrNull.owner.name.asString()
                is IrClassSymbol -> classifierOrNull.owner.signatureForBackInTimeDebugger()
                else -> "unknown"
            }
        )
        if (typeArguments.isNotEmpty()) {
            append("<")
            append(typeArguments.joinToString(","))
            append(">")
        }
    }.toString()
}