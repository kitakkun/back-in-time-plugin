package com.kitakkun.backintime.compiler.backend.utils

import com.kitakkun.backintime.compiler.backend.valuecontainer.ResolvedValueContainer
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.builders.IrBuilderWithScope
import org.jetbrains.kotlin.ir.builders.irCall
import org.jetbrains.kotlin.ir.builders.irComposite
import org.jetbrains.kotlin.ir.builders.irGet
import org.jetbrains.kotlin.ir.declarations.IrSimpleFunction
import org.jetbrains.kotlin.ir.declarations.IrValueParameter
import org.jetbrains.kotlin.ir.expressions.IrExpression

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
    valueContainerClassInfo: ResolvedValueContainer,
): IrExpression {
    val propertyGetterCall = irPropertyGetterCall(propertyGetter, dispatchReceiverParameter)

    val preSetterCallSymbols = valueContainerClassInfo.setterSymbols.dropLast(1)
    val setterCallSymbol = valueContainerClassInfo.setterSymbols.last()

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
