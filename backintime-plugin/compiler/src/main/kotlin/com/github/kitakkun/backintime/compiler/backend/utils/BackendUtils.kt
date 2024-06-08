package com.github.kitakkun.backintime.compiler.backend.utils

import com.github.kitakkun.backintime.compiler.backend.BackInTimePluginContext
import org.jetbrains.kotlin.ir.builders.IrBuilderWithScope
import org.jetbrains.kotlin.ir.builders.irCall
import org.jetbrains.kotlin.ir.builders.parent
import org.jetbrains.kotlin.ir.declarations.IrDeclarationOrigin
import org.jetbrains.kotlin.ir.declarations.IrVariable
import org.jetbrains.kotlin.ir.declarations.impl.IrVariableImpl
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.symbols.impl.IrVariableSymbolImpl
import org.jetbrains.kotlin.name.Name

context(BackInTimePluginContext)
fun IrBuilderWithScope.generateUUIDVariable(): IrVariable? {
    return IrVariableImpl(
        startOffset = this.startOffset,
        endOffset = this.endOffset,
        origin = IrDeclarationOrigin.DEFINED,
        symbol = IrVariableSymbolImpl(),
        name = Name.identifier("backInTimeUUID"),
        type = irBuiltIns.stringType,
        isVar = false,
        isConst = false,
        isLateinit = false,
    ).apply {
        this.initializer = generateUUIDStringCall()
        this.parent = this@generateUUIDVariable.parent
    }
}

context(BackInTimePluginContext)
fun IrBuilderWithScope.generateUUIDStringCall(): IrCall {
    return irCall(toStringFunction).apply {
        dispatchReceiver = irCall(randomUUIDFunction)
    }
}
