package com.github.kitakkun.backintime.compiler.backend.utils

import com.github.kitakkun.backintime.compiler.BackInTimeConsts
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.builders.IrBlockBodyBuilder
import org.jetbrains.kotlin.ir.builders.irCall
import org.jetbrains.kotlin.ir.builders.irTemporary
import org.jetbrains.kotlin.ir.declarations.IrDeclarationOrigin
import org.jetbrains.kotlin.ir.declarations.IrVariable
import org.jetbrains.kotlin.ir.util.getSimpleFunction

context(IrPluginContext)
fun IrBlockBodyBuilder.generateUUIDVariable(): IrVariable? {
    val uuidClass = referenceClass(BackInTimeConsts.UUIDClassId) ?: return null
    val randomUUIDFunction = uuidClass.getSimpleFunction(BackInTimeConsts.randomUUIDFunctionName) ?: return null
    val toStringFunction = uuidClass.getSimpleFunction("toString") ?: return null
    return irTemporary(
        value = irCall(toStringFunction).apply {
            dispatchReceiver = irCall(randomUUIDFunction)
        },
        irType = irBuiltIns.stringType,
        origin = IrDeclarationOrigin.DEFINED,
    )
}
