package com.github.kitakkun.backintime.compiler.backend.utils

import com.github.kitakkun.backintime.compiler.backend.BackInTimePluginContext
import com.github.kitakkun.backintime.compiler.backend.analyzer.ValueContainerStateChangeInsideFunctionAnalyzer
import org.jetbrains.kotlin.backend.jvm.ir.receiverAndArgs
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
    return valueContainerClassInfo.capturedFunctionNames.any { it == callingFunctionName }
}

context(BackInTimePluginContext)
fun IrCall.isIndirectValueContainerSetterCall(): Boolean {
    return ValueContainerStateChangeInsideFunctionAnalyzer.analyzePropertiesShouldBeCaptured(this).isNotEmpty()
}

fun IrCall.isLambdaFunctionRelevantCall(): Boolean {
    return receiverAndArgs().any { expression ->
        when (expression) {
            is IrFunctionExpression -> true

            is IrGetValue -> {
                val referencingVariable = (expression.symbol.owner) as? IrVariable ?: return@any false
                (referencingVariable.initializer as? IrFunctionExpression) != null
            }

            else -> false
        }
    }
}

fun IrCall.getRelevantLambdaExpressions(): Set<IrFunctionExpression> {
    return receiverAndArgs().mapNotNull { expression ->
        when (expression) {
            is IrFunctionExpression -> expression
            is IrGetValue -> (expression.symbol.owner as? IrVariable)?.initializer as? IrFunctionExpression
            else -> null
        }
    }.toSet()
}
