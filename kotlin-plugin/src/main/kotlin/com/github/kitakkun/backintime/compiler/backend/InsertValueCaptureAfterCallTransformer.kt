package com.github.kitakkun.backintime.compiler.backend

import com.github.kitakkun.backintime.compiler.backend.utils.getGenericTypes
import com.github.kitakkun.backintime.compiler.backend.utils.getPropertyGetterRecursively
import com.github.kitakkun.backintime.compiler.backend.utils.getPropertyName
import com.github.kitakkun.backintime.compiler.backend.utils.getSimpleFunctionRecursively
import com.github.kitakkun.backintime.compiler.backend.utils.irNotifyValueChangeCall
import com.github.kitakkun.backintime.compiler.backend.utils.isGetterName
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.builders.IrBlockBuilder
import org.jetbrains.kotlin.ir.builders.Scope
import org.jetbrains.kotlin.ir.builders.irCall
import org.jetbrains.kotlin.ir.builders.irComposite
import org.jetbrains.kotlin.ir.declarations.IrSimpleFunction
import org.jetbrains.kotlin.ir.declarations.IrVariable
import org.jetbrains.kotlin.ir.deepCopyWithVariables
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.IrGetValue
import org.jetbrains.kotlin.ir.types.classFqName
import org.jetbrains.kotlin.ir.types.classOrNull
import org.jetbrains.kotlin.ir.util.fqNameWhenAvailable
import org.jetbrains.kotlin.ir.util.isSetter
import org.jetbrains.kotlin.ir.util.parentClassOrNull
import org.jetbrains.kotlin.ir.visitors.IrElementTransformerVoid
import org.jetbrains.kotlin.name.CallableId

class InsertValueCaptureAfterCallTransformer(
    private val pluginContext: IrPluginContext,
    private val declaration: IrSimpleFunction,
    private val uuidVariable: IrVariable,
    private val capturedCallableIds: Set<CallableId>,
    private val valueGetterCallableIds: Set<CallableId>,
) : IrElementTransformerVoid() {
    override fun visitCall(expression: IrCall): IrExpression {
        val irBlockBuilder = IrBlockBuilder(
            context = pluginContext,
            scope = Scope(expression.symbol),
            startOffset = expression.startOffset,
            endOffset = expression.endOffset,
        )
        val callingFunction = expression.symbol.owner
        val dispatchReceiverForExpression = expression.dispatchReceiver
        // ピュアなvalueセッター
        // ex) this.variable = 1
        if (callingFunction.isSetter && dispatchReceiverForExpression is IrGetValue) {
            val property = callingFunction.correspondingPropertySymbol?.owner ?: return super.visitCall(expression)
            val propertyGetter = property.getter ?: return super.visitCall(expression)
            return irBlockBuilder.irComposite {
                +super.visitCall(expression)
                with(pluginContext) {
                    irNotifyValueChangeCall(
                        parentInstance = declaration.dispatchReceiverParameter ?: return@irComposite,
                        propertyName = property.name.asString(),
                        value = irCall(propertyGetter).apply {
                            this.dispatchReceiver = dispatchReceiverForExpression.deepCopyWithVariables()
                        },
                        propertyTypeClassFqName = property.backingField?.type?.classFqName?.asString() ?: return@irComposite,
                        methodCallUUID = uuidVariable,
                    )?.let { +it }
                }
            }
        }
        // 他のクラスの内側に値を持っている場合
        // ex) liveData.value = 1
        if (
            (callingFunction.fqNameWhenAvailable in capturedCallableIds.map { it.asSingleFqName() })
            && (callingFunction.parentClassOrNull?.fqNameWhenAvailable in valueGetterCallableIds.map { it.className })
            && dispatchReceiverForExpression is IrCall
        ) {
            val property = dispatchReceiverForExpression.symbol.owner.correspondingPropertySymbol?.owner ?: return super.visitCall(expression)
            val propertyClass = property.backingField?.type?.classOrNull?.owner ?: return super.visitCall(expression)
            val valueGetterCallableId = valueGetterCallableIds.firstOrNull { it.className == callingFunction.parentClassOrNull?.fqNameWhenAvailable } ?: return super.visitCall(expression)

            val valueGetter = if (valueGetterCallableId.callableName.isGetterName()) {
                propertyClass.getPropertyGetterRecursively(valueGetterCallableId.callableName.getPropertyName())
            } else {
                val functionName = valueGetterCallableId.callableName.asString()
                propertyClass.getSimpleFunctionRecursively(functionName)
            } ?: return super.visitCall(expression)

            return irBlockBuilder.irComposite {
                +super.visitCall(expression)
                with(pluginContext) {
                    irNotifyValueChangeCall(
                        parentInstance = declaration.dispatchReceiverParameter ?: return@irComposite,
                        propertyName = property.name.asString(),
                        value = irCall(valueGetter).apply { this.dispatchReceiver = dispatchReceiverForExpression.deepCopyWithVariables() },
                        propertyTypeClassFqName = property.getGenericTypes().first().classFqName?.asString() ?: return@irComposite,
                        methodCallUUID = uuidVariable,
                    )?.let { +it }
                }
            }
        }
        return super.visitCall(expression)
    }

}
