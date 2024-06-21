package com.github.kitakkun.backintime.compiler.backend.utils

import com.github.kitakkun.backintime.compiler.backend.ValueContainerClassInfo
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.builders.IrBuilderWithScope
import org.jetbrains.kotlin.ir.builders.irCall
import org.jetbrains.kotlin.ir.builders.irComposite
import org.jetbrains.kotlin.ir.builders.irGet
import org.jetbrains.kotlin.ir.declarations.IrSimpleFunction
import org.jetbrains.kotlin.ir.declarations.IrValueParameter
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.types.classOrNull

context(IrPluginContext)
fun IrBuilderWithScope.irPropertySetterCall(
    propertySetter: IrSimpleFunction,
    dispatchReceiverParameter: IrValueParameter,
    valueParameter: IrValueParameter,
) = irCall(propertySetter).apply {
    this.dispatchReceiver = irGet(dispatchReceiverParameter)
    putValueArgument(0, irGet(valueParameter))
}

fun IrBuilderWithScope.irPropertyGetterCall(
    propertyGetter: IrSimpleFunction,
    dispatchReceiverParameter: IrValueParameter,
) = irCall(propertyGetter).apply {
    this.dispatchReceiver = irGet(dispatchReceiverParameter)
}

context(IrPluginContext)
fun IrBuilderWithScope.irValueContainerPropertySetterCall(
    propertyGetter: IrSimpleFunction,
    dispatchReceiverParameter: IrValueParameter,
    valueParameter: IrValueParameter,
    valueContainerClassInfo: ValueContainerClassInfo,
): IrExpression? {
    val propertyGetterCall = irPropertyGetterCall(propertyGetter, dispatchReceiverParameter)

    val klass = propertyGetter.returnType.classOrNull?.owner ?: return null
    val preSetterCallSymbols = valueContainerClassInfo.preSetterFunctionNames
        .mapNotNull { klass.getSimpleFunctionsRecursively(it).firstOrNull { it.owner.valueParameters.isEmpty() } }
    val setterCallSymbol = klass.getSimpleFunctionsRecursively(valueContainerClassInfo.setterFunctionName)
        .firstOrNull { it.owner.valueParameters.size == 1 } ?: return null

    // 一部関数のシンボルが欠落している場合は処理継続不可
    if (preSetterCallSymbols.size != valueContainerClassInfo.preSetterFunctionNames.size) return null

    val preSetterCalls = preSetterCallSymbols.map { irCall(it).apply { dispatchReceiver = propertyGetterCall } }
    val setterCall = irCall(setterCallSymbol).apply {
        dispatchReceiver = propertyGetterCall
        putValueArgument(0, irGet(valueParameter))
    }

    return irComposite {
        +preSetterCalls
        +setterCall
    }
}
