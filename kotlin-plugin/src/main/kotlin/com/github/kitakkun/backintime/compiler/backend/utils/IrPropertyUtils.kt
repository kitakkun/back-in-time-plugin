package com.github.kitakkun.backintime.compiler.backend.utils

import org.jetbrains.kotlin.ir.declarations.IrProperty
import org.jetbrains.kotlin.ir.types.IrSimpleType
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.types.typeOrNull

/**
 * LiveData<T> のようなジェネリック型のTの部分を取得する
 */
fun IrProperty.getGenericTypes(): List<IrType> {
    return (this.getter?.returnType as? IrSimpleType)
        ?.arguments
        ?.mapNotNull { it.typeOrNull }
        .orEmpty()
}

fun IrProperty.getValueType(): IrType {
    return this.getGenericTypes().firstOrNull() ?: this.getter?.returnType ?: error("Cannot get value type")
}
