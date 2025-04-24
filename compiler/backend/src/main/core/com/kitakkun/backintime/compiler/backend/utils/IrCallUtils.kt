package com.kitakkun.backintime.compiler.backend.utils

import com.kitakkun.backintime.compiler.backend.BackInTimePluginContext
import com.kitakkun.backintime.compiler.backend.analyzer.TrackableStateHolderStateChangeInsideFunctionAnalyzer
import com.kitakkun.backintime.compiler.backend.api.VersionSpecificAPI
import org.jetbrains.kotlin.ir.declarations.IrVariable
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrFunctionExpression
import org.jetbrains.kotlin.ir.expressions.IrGetValue
import org.jetbrains.kotlin.ir.types.classOrNull

val IrCall.receiver get() = dispatchReceiver ?: extensionReceiver

fun IrCall.isTrackableStateHolderSetterCall(
    irContext: BackInTimePluginContext
): Boolean {
    val receiverClassSymbol = this.receiver?.type?.classOrNull ?: return false
    val callingFunctionSymbol = this.symbol
    val trackableStateHolderInfo = irContext.trackableStateHolderClassInfoList.find { it.classSymbol == receiverClassSymbol } ?: return false
    return trackableStateHolderInfo.captureTargetSymbols.any { it.first == callingFunctionSymbol }
}

fun IrCall.isIndirectTrackableStateHolderSetterCall(irContext: BackInTimePluginContext): Boolean {
    return TrackableStateHolderStateChangeInsideFunctionAnalyzer.analyzePropertiesShouldBeCaptured(irContext, this).isNotEmpty()
}

fun IrCall.isLambdaFunctionRelevantCall(): Boolean {
    return VersionSpecificAPI.INSTANCE.getReceiverAndArgs(this).any { expression ->
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
    return VersionSpecificAPI.INSTANCE.getReceiverAndArgs(this).mapNotNull { expression ->
        when (expression) {
            is IrFunctionExpression -> expression
            is IrGetValue -> (expression.symbol.owner as? IrVariable)?.initializer as? IrFunctionExpression
            else -> null
        }
    }.toSet()
}
