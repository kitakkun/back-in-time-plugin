package io.github.kitakkun.backintime.compiler.backend.utils

import io.github.kitakkun.backintime.compiler.backend.BackInTimePluginContext
import io.github.kitakkun.backintime.compiler.backend.analyzer.ValueContainerStateChangeInsideFunctionAnalyzer
import org.jetbrains.kotlin.backend.jvm.ir.receiverAndArgs
import org.jetbrains.kotlin.ir.declarations.IrVariable
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.IrFunctionExpression
import org.jetbrains.kotlin.ir.expressions.IrGetValue
import org.jetbrains.kotlin.ir.types.classOrNull

val IrCall.receiver get() = dispatchReceiver ?: extensionReceiver

context(BackInTimePluginContext)
fun IrCall.isValueContainerSetterCall(): Boolean {
    val receiverClassSymbol = this.receiver?.type?.classOrNull ?: return false
    val callingFunctionSymbol = this.symbol
    val valueContainerClassInfo = valueContainerClassInfoList.find { it.classSymbol == receiverClassSymbol } ?: return false
    return valueContainerClassInfo.captureTargetSymbols.any { it.first == callingFunctionSymbol }
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

fun IrExpression.toLambdaExpression(): IrFunctionExpression? {
    return when (this) {
        is IrFunctionExpression -> this
        is IrGetValue -> (this.symbol.owner as? IrVariable)?.initializer as? IrFunctionExpression
        else -> null
    }
}
