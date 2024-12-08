package com.kitakkun.backintime.compiler.backend.api

import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.IrFunctionAccessExpression

interface VersionSpecificAPI {
    companion object {
        lateinit var INSTANCE: VersionSpecificAPI
    }

    fun isReifiable(function: IrFunction): Boolean

    fun getReceiverAndArgs(functionAccessExpression: IrFunctionAccessExpression): List<IrExpression>
}