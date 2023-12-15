package com.github.kitakkun.backintime.compiler.backend.utils

import com.github.kitakkun.backintime.compiler.BackInTimeConsts
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.builders.IrBlockBodyBuilder
import org.jetbrains.kotlin.ir.builders.IrBuilderWithScope
import org.jetbrains.kotlin.ir.builders.irCall
import org.jetbrains.kotlin.ir.builders.irTemporary
import org.jetbrains.kotlin.ir.declarations.IrDeclarationOrigin
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.declarations.IrVariable
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.types.classFqName
import org.jetbrains.kotlin.ir.util.getSimpleFunction

context(IrPluginContext)
@Suppress("UNUSED")
fun IrBuilderWithScope.generateDebugPrintCall(argument: IrExpression): IrCall {
    val printlnFunction = referenceFunctions(BackInTimeConsts.printlnCallableId).first {
        it.owner.valueParameters.size == 1 && it.owner.valueParameters.first().type == irBuiltIns.anyNType
    }
    return irCall(printlnFunction).apply {
        putValueArgument(0, argument)
    }
}

@Suppress("UNUSED")
fun IrFunction.generateSignature(): String {
    return "${this.name}(${this.valueParameters.joinToString { "${it.name}: ${it.type.classFqName}" }}): ${this.returnType.classFqName}"
}

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
