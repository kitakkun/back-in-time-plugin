package com.github.kitakkun.backintime.backend

import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.types.classFqName

fun IrType.isKotlinPrimitiveType(): Boolean {
    return when (this.classFqName?.asString()) {
        "kotlin.Boolean" -> true
        "kotlin.Byte" -> true
        "kotlin.Char" -> true
        "kotlin.Short" -> true
        "kotlin.Int" -> true
        "kotlin.Long" -> true
        "kotlin.Float" -> true
        "kotlin.Double" -> true
        "kotlin.String" -> true
        else -> false
    }
}
