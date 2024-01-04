package com.github.kitakkun.backintime.compiler.backend.utils

import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.symbols.IrSimpleFunctionSymbol
import org.jetbrains.kotlin.ir.types.classOrNull
import org.jetbrains.kotlin.ir.util.simpleFunctions
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.synthetic.isVisibleOutside

fun IrClass.getSimpleFunctionsRecursively(name: Name): List<IrSimpleFunctionSymbol> {
    return simpleFunctions().filter { it.name == name }.map { it.symbol }
        .plus(
            superTypes
                .flatMap { it.classOrNull?.owner?.getSimpleFunctionsRecursively(name) ?: emptyList() }
                .filter {
                    it.owner.visibility.isVisibleOutside()
                }
        )
}
