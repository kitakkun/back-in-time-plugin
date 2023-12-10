package com.github.kitakkun.backintime.compiler.backend.utils

import com.github.kitakkun.backintime.compiler.BackInTimeConsts
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.builders.IrBlockBuilder
import org.jetbrains.kotlin.ir.builders.IrBuilderWithScope
import org.jetbrains.kotlin.ir.builders.irCall
import org.jetbrains.kotlin.ir.builders.irGet
import org.jetbrains.kotlin.ir.builders.irGetObject
import org.jetbrains.kotlin.ir.builders.irString
import org.jetbrains.kotlin.ir.builders.irTemporary
import org.jetbrains.kotlin.ir.declarations.IrDeclarationOrigin
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.declarations.IrValueParameter
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

context(IrBuilderWithScope, IrPluginContext)
fun irNotifyValueChangeCall(
    parentInstance: IrValueParameter,
    propertyName: String,
    propertyTypeClassFqName: String,
    value: IrExpression,
    methodCallUUID: IrVariable,
): IrExpression? {
    val debugServiceClass = referenceClass(BackInTimeConsts.backInTimeDebugServiceClassId) ?: return null
    val notifyValueChangeFunction = debugServiceClass.getSimpleFunction(BackInTimeConsts.notifyPropertyChanged) ?: return null
    return irCall(notifyValueChangeFunction).apply {
        dispatchReceiver = irGetObject(debugServiceClass)
        putValueArgument(0, irGet(parentInstance))
        putValueArgument(1, irString(propertyName))
        putValueArgument(2, value)
        putValueArgument(3, irString(propertyTypeClassFqName))
        putValueArgument(4, irGet(methodCallUUID))
    }
}

context(IrPluginContext)
fun IrBlockBuilder.generateUUIDVariable(): IrVariable? {
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
