package com.kitakkun.backintime.compiler.backend.transformer.capture

import com.kitakkun.backintime.compiler.backend.BackInTimePluginContext
import com.kitakkun.backintime.compiler.backend.utils.generateCaptureValueCallForValueContainer
import com.kitakkun.backintime.compiler.backend.utils.receiver
import org.jetbrains.kotlin.backend.common.IrElementTransformerVoidWithContext
import org.jetbrains.kotlin.backend.common.lower.createIrBuilder
import org.jetbrains.kotlin.ir.builders.irCall
import org.jetbrains.kotlin.ir.builders.irComposite
import org.jetbrains.kotlin.ir.builders.irEquals
import org.jetbrains.kotlin.ir.builders.irGet
import org.jetbrains.kotlin.ir.builders.irIfThen
import org.jetbrains.kotlin.ir.declarations.IrProperty
import org.jetbrains.kotlin.ir.declarations.IrValueParameter
import org.jetbrains.kotlin.ir.declarations.IrVariable
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.types.classOrNull
import org.jetbrains.kotlin.ir.util.classId

class LambdaArgumentBodyTransformer(
    private val irContext: BackInTimePluginContext,
    private val passedProperties: Set<IrProperty>,
    private val classDispatchReceiverParameter: IrValueParameter,
    private val uuidVariable: IrVariable,
) : IrElementTransformerVoidWithContext() {
    override fun visitCall(expression: IrCall): IrExpression {
        val receiver = expression.receiver ?: return expression
        val receiverClassSymbol = receiver.type.classOrNull
        val receiverClassId = receiver.type.classOrNull?.owner?.classId
        val callingFunction = expression.symbol

        val valueContainerClassInfo = irContext.valueContainerClassInfoList.find { it.classSymbol == receiverClassSymbol } ?: return expression
        if (callingFunction !in valueContainerClassInfo.captureTargetSymbols.map { it.first }) return expression

        val possibleReceiverProperties = passedProperties.filter {
            val propertyClassId = it.getter?.returnType?.classOrNull?.owner?.classId
            propertyClassId == receiverClassId
        }

        with(irContext.irBuiltIns.createIrBuilder(expression.symbol)) {
            val captureCalls = possibleReceiverProperties.mapNotNull { property ->
                val propertyGetter = property.getter ?: return@mapNotNull null
                val captureCall = property.generateCaptureValueCallForValueContainer(
                    irContext = irContext,
                    irBuilder = this,
                    instanceParameter = classDispatchReceiverParameter,
                    uuidVariable = uuidVariable,
                ) ?: return@mapNotNull null
                val getPropertyInstanceCall = irCall(propertyGetter).apply {
                    dispatchReceiver = irGet(classDispatchReceiverParameter)
                }
                irIfThen(
                    condition = irEquals(receiver, getPropertyInstanceCall),
                    thenPart = captureCall,
                    type = irContext.irBuiltIns.unitType,
                )
            }

            if (captureCalls.isEmpty()) return expression

            return irComposite {
                +expression
                +captureCalls
            }
        }
    }
}
