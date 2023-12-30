package com.github.kitakkun.backintime.compiler.backend.transformer

import com.github.kitakkun.backintime.compiler.backend.BackInTimePluginContext
import com.github.kitakkun.backintime.compiler.backend.utils.generateCaptureValueCallForValueContainer
import com.github.kitakkun.backintime.compiler.backend.utils.irBlockBodyBuilder
import com.github.kitakkun.backintime.compiler.backend.utils.receiver
import org.jetbrains.kotlin.ir.IrElement
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
import org.jetbrains.kotlin.ir.visitors.IrElementTransformerVoid
import org.jetbrains.kotlin.ir.visitors.transformChildrenVoid

context(BackInTimePluginContext)
class LambdaArgumentBodyTransformer(
    private val passedProperties: Set<IrProperty>,
    private val classDispatchReceiverParameter: IrValueParameter,
    private val uuidVariable: IrVariable,
) : IrElementTransformerVoid() {
    override fun visitElement(element: IrElement): IrElement {
        element.transformChildrenVoid(this)
        return element
    }

    override fun visitCall(expression: IrCall): IrExpression {
        val receiver = expression.receiver ?: return expression
        val receiverClassId = receiver.type.classOrNull?.owner?.classId
        val callingFunctionName = expression.symbol.owner.name

        val valueContainerClassInfo = valueContainerClassInfoList.find { it.classId == receiverClassId }
        val captureTargetFunctionNames = valueContainerClassInfo?.capturedCallableIds?.map { it.callableName }.orEmpty()
        if (callingFunctionName !in captureTargetFunctionNames) return expression

        val possibleReceiverProperties = passedProperties.filter { it.getter?.returnType?.classOrNull?.owner?.classId == receiverClassId }
        val irBuilder = expression.irBlockBodyBuilder(pluginContext)
        val captureCalls = possibleReceiverProperties.mapNotNull { property ->
            with(irBuilder) {
                val propertyGetter = property.getter ?: return@mapNotNull null
                val captureCall = property.generateCaptureValueCallForValueContainer(
                    instanceParameter = classDispatchReceiverParameter,
                    uuidVariable = uuidVariable,
                ) ?: return@mapNotNull null
                val getPropertyInstanceCall = irCall(propertyGetter).apply {
                    dispatchReceiver = irGet(classDispatchReceiverParameter)
                }
                irIfThen(
                    condition = irEquals(receiver, getPropertyInstanceCall),
                    thenPart = captureCall,
                    type = pluginContext.irBuiltIns.unitType,
                )
            }
        }

        if (captureCalls.isEmpty()) return expression

        return irBuilder.irComposite {
            +expression
            +captureCalls
        }
    }
}
