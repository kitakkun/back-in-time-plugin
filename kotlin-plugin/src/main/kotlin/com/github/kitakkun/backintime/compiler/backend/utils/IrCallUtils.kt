package com.github.kitakkun.backintime.compiler.backend.utils

import com.github.kitakkun.backintime.compiler.backend.BackInTimePluginContext
import com.github.kitakkun.backintime.compiler.backend.analyzer.ValueContainerStateChangeInsideFunctionAnalyzer
import org.jetbrains.kotlin.ir.declarations.IrSimpleFunction
import org.jetbrains.kotlin.ir.declarations.IrVariable
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrFunctionExpression
import org.jetbrains.kotlin.ir.expressions.IrGetValue
import org.jetbrains.kotlin.ir.types.classOrNull
import org.jetbrains.kotlin.ir.util.classId

val IrCall.receiver get() = dispatchReceiver ?: extensionReceiver

context(BackInTimePluginContext)
fun IrCall.isValueContainerSetterCall(): Boolean {
    val receiverClassId = this.receiver?.type?.classOrNull?.owner?.classId ?: return false
    val callingFunctionName = this.symbol.owner.name
    val valueContainerClassInfo = valueContainerClassInfoList.find { it.classId == receiverClassId } ?: return false
    return valueContainerClassInfo.capturedCallableIds.any { it.callableName == callingFunctionName }
}

context(BackInTimePluginContext)
fun IrCall.isIndirectValueContainerSetterCall(): Boolean {
    return ValueContainerStateChangeInsideFunctionAnalyzer.analyzePropertiesShouldBeCaptured(this).isNotEmpty()
}


fun IrCall.getInvokedLambdaFunction(): IrSimpleFunction? {
    return when (receiver) {
        is IrGetValue -> {
            val invokableVariable = ((receiver as IrGetValue).symbol.owner) as? IrVariable ?: return null
            (invokableVariable.initializer as? IrFunctionExpression)?.function
        }

        is IrFunctionExpression -> {
            (receiver as IrFunctionExpression).function
        }

        else -> {
            null
        }
    }
}
