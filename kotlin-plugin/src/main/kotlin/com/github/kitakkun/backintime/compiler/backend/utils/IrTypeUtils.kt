package com.github.kitakkun.backintime.compiler.backend.utils

import org.jetbrains.kotlin.ir.types.IrSimpleType
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.types.typeOrNull

fun IrType.getGenericTypes(): List<IrType> {
    return (this as? IrSimpleType)
        ?.arguments
        ?.mapNotNull { it.typeOrNull }
        .orEmpty()
}
