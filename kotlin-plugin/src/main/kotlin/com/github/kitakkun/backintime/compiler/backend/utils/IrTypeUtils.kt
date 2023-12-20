package com.github.kitakkun.backintime.compiler.backend.utils

import org.jetbrains.kotlin.ir.types.IrSimpleType
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.types.classFqName
import org.jetbrains.kotlin.ir.types.typeOrNull

fun IrType.getGenericTypes(): List<IrType> {
    return (this as? IrSimpleType)
        ?.arguments
        ?.mapNotNull { it.typeOrNull }
        .orEmpty()
}

fun IrSimpleType.getCompletedName(): String? {
    if (this.arguments.isEmpty()) {
        return this.classFqName?.asString()
    } else {
        val typeArgumentNames = this.arguments.map { (it.typeOrNull as? IrSimpleType)?.getCompletedName() }
        if (typeArgumentNames.any { it == null }) return null
        return this.classFqName?.asString() + typeArgumentNames.joinToString(prefix = "<", postfix = ">") { it!! }
    }
}
