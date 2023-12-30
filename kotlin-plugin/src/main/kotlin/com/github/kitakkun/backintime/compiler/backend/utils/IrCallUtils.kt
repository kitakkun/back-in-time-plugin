package com.github.kitakkun.backintime.compiler.backend.utils

import com.github.kitakkun.backintime.compiler.backend.BackInTimePluginContext
import com.github.kitakkun.backintime.compiler.backend.analyzer.ValueHolderStateChangeInsideBodyAnalyzer
import org.jetbrains.kotlin.ir.expressions.IrCall
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
    return ValueHolderStateChangeInsideBodyAnalyzer.analyzePropertiesShouldBeCaptured(this).isNotEmpty()
}
