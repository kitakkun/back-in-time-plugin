package com.kitakkun.backintime.compiler.backend.valuecontainer.filter.type

import org.jetbrains.kotlin.ir.types.IrType

sealed class TypeMatcher {
    abstract fun matches(type: IrType): Boolean
}
