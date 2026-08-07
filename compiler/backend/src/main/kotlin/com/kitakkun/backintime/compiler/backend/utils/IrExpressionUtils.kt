package com.kitakkun.backintime.compiler.backend.utils

import org.jetbrains.kotlin.ir.declarations.IrProperty
import org.jetbrains.kotlin.ir.declarations.IrVariable
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.IrGetValue
import org.jetbrains.kotlin.ir.expressions.IrTypeOperator
import org.jetbrains.kotlin.ir.expressions.IrTypeOperatorCall

fun IrExpression.getCorrespondingProperty(): IrProperty? {
    return when (this) {
        // Since Kotlin 2.3 a property read whose static type is narrower than the getter's return type
        // - which is exactly what an explicit backing field looks like from inside its own class -
        // arrives wrapped in a compiler-inserted implicit cast instead of an already-narrowed getter call.
        is IrTypeOperatorCall -> if (operator in implicitTypeOperators) argument.getCorrespondingProperty() else null
        is IrCall -> this.symbol.owner.correspondingPropertySymbol?.owner
        is IrGetValue -> {
            val variable = this.symbol.owner as? IrVariable
            variable?.initializer?.getCorrespondingProperty()
        }

        else -> null
    }
}

private val implicitTypeOperators = setOf(
    IrTypeOperator.IMPLICIT_CAST,
    IrTypeOperator.IMPLICIT_NOTNULL,
    IrTypeOperator.IMPLICIT_DYNAMIC_CAST,
    IrTypeOperator.REINTERPRET_CAST,
)
