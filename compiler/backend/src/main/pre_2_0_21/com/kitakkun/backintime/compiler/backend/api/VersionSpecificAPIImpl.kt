package com.kitakkun.backintime.compiler.backend.api

import org.jetbrains.kotlin.backend.jvm.ir.isReifiable
import org.jetbrains.kotlin.backend.jvm.ir.receiverAndArgs
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.IrFunctionAccessExpression

object VersionSpecificAPIImpl : VersionSpecificAPI {
    override fun isReifiable(function: IrFunction): Boolean {
        return function.isReifiable()
    }

    override fun getReceiverAndArgs(functionAccessExpression: IrFunctionAccessExpression): List<IrExpression> {
        return functionAccessExpression.receiverAndArgs()
    }
}