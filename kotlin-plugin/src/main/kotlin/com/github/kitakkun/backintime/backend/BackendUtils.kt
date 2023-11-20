package com.github.kitakkun.backintime.backend

import org.jetbrains.kotlin.ir.declarations.IrProperty
import org.jetbrains.kotlin.ir.types.IrSimpleType
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.types.classFqName
import org.jetbrains.kotlin.ir.types.typeOrNull

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

/**
 * LiveData<T> のようなジェネリック型のTの部分を取得する
 */
fun IrProperty.getGenericTypes(): List<IrType> {
    return (this.backingField?.type as? IrSimpleType)
        ?.arguments
        ?.mapNotNull { it.typeOrNull }
        .orEmpty()
}
