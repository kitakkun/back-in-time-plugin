package com.github.kitakkun.backintime.compiler.backend.utils

import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.symbols.IrSimpleFunctionSymbol
import org.jetbrains.kotlin.ir.types.classOrNull
import org.jetbrains.kotlin.ir.util.getPropertyGetter
import org.jetbrains.kotlin.ir.util.getSimpleFunction

fun IrClass.getSimpleFunctionRecursively(name: String): IrSimpleFunctionSymbol? {
    return getSimpleFunction(name)
        ?: superTypes
            .mapNotNull { it.classOrNull }
            .firstNotNullOfOrNull { it.getSimpleFunction(name) }
}

fun IrClass.getPropertyGetterRecursively(name: String): IrSimpleFunctionSymbol? {
    return getPropertyGetter(name)
        ?: superTypes
            .mapNotNull { it.classOrNull }
            .firstNotNullOfOrNull { it.getPropertyGetter(name) }
}
