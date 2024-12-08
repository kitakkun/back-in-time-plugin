package com.kitakkun.backintime.compiler.backend.valuecontainer.filter.type

import org.jetbrains.kotlin.ir.symbols.IrTypeParameterSymbol
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.types.classifierOrNull
import org.jetbrains.kotlin.ir.util.isTypeParameter

class TypeParameterMatcher(private val index: Int) : TypeMatcher() {
    override fun matches(type: IrType): Boolean {
        if (!type.isTypeParameter()) return false
        return (type.classifierOrNull as IrTypeParameterSymbol).owner.index == index
    }
}
