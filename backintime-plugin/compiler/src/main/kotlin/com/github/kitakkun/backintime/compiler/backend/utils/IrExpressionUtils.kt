package com.github.kitakkun.backintime.compiler.backend.utils

import org.jetbrains.kotlin.ir.declarations.IrProperty
import org.jetbrains.kotlin.ir.declarations.IrVariable
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.IrGetValue

fun IrExpression.getCorrespondingProperty(): IrProperty? {
    return when (this) {
        is IrCall -> this.symbol.owner.correspondingPropertySymbol?.owner
        is IrGetValue -> {
            val variable = this.symbol.owner as? IrVariable
            variable?.initializer?.getCorrespondingProperty()
        }

        else -> null
    }
}
