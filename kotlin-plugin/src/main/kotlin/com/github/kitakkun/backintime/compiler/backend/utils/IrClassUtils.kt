package com.github.kitakkun.backintime.compiler.backend.utils

import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.symbols.IrSimpleFunctionSymbol
import org.jetbrains.kotlin.ir.types.classOrNull
import org.jetbrains.kotlin.ir.util.getPropertyGetter
import org.jetbrains.kotlin.ir.util.getSimpleFunction
import org.jetbrains.kotlin.synthetic.isVisibleOutside

fun IrClass.getSimpleFunctionRecursively(name: String): IrSimpleFunctionSymbol? {
    return getSimpleFunction(name)
        ?: superTypes
            .mapNotNull { it.classOrNull?.owner?.getSimpleFunctionRecursively(name) }
            .firstOrNull { it.owner.visibility.isVisibleOutside() }
}

fun IrClass.getPropertyGetterRecursively(name: String): IrSimpleFunctionSymbol? {
    return getPropertyGetter(name)
        ?: superTypes
            .mapNotNull { it.classOrNull?.owner?.getSimpleFunctionRecursively(name) }
            .firstOrNull { it.owner.visibility.isVisibleOutside() }
}
