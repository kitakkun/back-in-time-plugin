package com.kitakkun.backintime.compiler.backend.utils

import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.declarations.IrParameterKind
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.IrFunctionAccessExpression

fun IrFunctionAccessExpression.getRegularArgument(index: Int): IrExpression? {
    val firstRegularParameterIndex = this.symbol.owner.parameters.first { it.kind == IrParameterKind.Regular }.indexInParameters
    return this.arguments[firstRegularParameterIndex + index]
}

fun IrFunctionAccessExpression.putRegularArgument(index: Int, expression: IrExpression) {
    val firstRegularParameterIndex = this.symbol.owner.parameters.first { it.kind == IrParameterKind.Regular }.indexInParameters
    this.arguments[firstRegularParameterIndex + index] = expression
}

fun IrFunctionAccessExpression.putExtensionReceiver(expression: IrExpression) {
    val extensionReceiverParameter = this.symbol.owner.extensionReceiverParameter() ?: return
    this.arguments[extensionReceiverParameter.indexInParameters] = expression
}

fun IrFunctionAccessExpression.extensionReceiver(): IrExpression? {
    val extensionReceiverParameter = this.symbol.owner.extensionReceiverParameter() ?: return null
    return this.arguments[extensionReceiverParameter.indexInParameters]
}

fun IrFunction.valueParameters() = this.parameters.filter { it.kind == IrParameterKind.Regular }

fun IrFunction.extensionReceiverParameter() = this.parameters.find { it.kind == IrParameterKind.ExtensionReceiver }
