package com.github.kitakkun.backintime.compiler.backend

import com.github.kitakkun.backintime.compiler.BackInTimeConsts
import com.github.kitakkun.backintime.compiler.backend.utils.getPropertyGetterRecursively
import com.github.kitakkun.backintime.compiler.backend.utils.getPropertyName
import com.github.kitakkun.backintime.compiler.backend.utils.getSimpleFunctionRecursively
import com.github.kitakkun.backintime.compiler.backend.utils.isGetterName
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.builders.IrBlockBuilder
import org.jetbrains.kotlin.ir.builders.Scope
import org.jetbrains.kotlin.ir.builders.irCall
import org.jetbrains.kotlin.ir.builders.irComposite
import org.jetbrains.kotlin.ir.builders.irGet
import org.jetbrains.kotlin.ir.builders.irGetObject
import org.jetbrains.kotlin.ir.builders.irString
import org.jetbrains.kotlin.ir.declarations.IrSimpleFunction
import org.jetbrains.kotlin.ir.declarations.IrVariable
import org.jetbrains.kotlin.ir.deepCopyWithVariables
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.IrGetValue
import org.jetbrains.kotlin.ir.types.classOrNull
import org.jetbrains.kotlin.ir.util.classId
import org.jetbrains.kotlin.ir.util.getSimpleFunction
import org.jetbrains.kotlin.ir.util.isSetter
import org.jetbrains.kotlin.ir.visitors.IrElementTransformerVoid

class InsertValueCaptureAfterCallTransformer(
    private val pluginContext: IrPluginContext,
    private val declaration: IrSimpleFunction,
    private val uuidVariable: IrVariable,
    private val valueContainerClassInfoList: List<ValueContainerClassInfo>,
) : IrElementTransformerVoid() {
    private val debugServiceClass = pluginContext.referenceClass(BackInTimeConsts.backInTimeDebugServiceClassId)!!
    private val notifyValueChangeFunction = debugServiceClass.getSimpleFunction(BackInTimeConsts.notifyPropertyChanged)!!

    override fun visitCall(expression: IrCall): IrExpression {
        val irBlockBuilder = IrBlockBuilder(
            context = pluginContext,
            scope = Scope(expression.symbol),
            startOffset = expression.startOffset,
            endOffset = expression.endOffset,
        )

        // ピュアなvalueセッター
        // ex) this.variable = 1
        if (expression.isPureSetterCall()) {
            val parentReceiver = declaration.dispatchReceiverParameter ?: return super.visitCall(expression)
            val property = expression.symbol.owner.correspondingPropertySymbol?.owner ?: return super.visitCall(expression)
            val propertyGetter = property.getter ?: return super.visitCall(expression)
            with(irBlockBuilder) {
                return irComposite {
                    +super.visitCall(expression)
                    +irCall(notifyValueChangeFunction).apply {
                        dispatchReceiver = irGetObject(debugServiceClass)
                        putValueArgument(0, irGet(parentReceiver))
                        putValueArgument(1, irString(property.name.asString()))
                        putValueArgument(2, irCall(propertyGetter).apply {
                            this.dispatchReceiver = expression.dispatchReceiver!!.deepCopyWithVariables()
                        })
                        putValueArgument(3, irGet(uuidVariable))
                    }
                }
            }
        }

        // 他のクラスの内側に値を持っている場合
        // ex) liveData.value = 1
        if (expression.isValueContainerSetterCall()) {
            val property = (expression.dispatchReceiver as? IrCall)?.symbol?.owner?.correspondingPropertySymbol?.owner ?: return super.visitCall(expression)
            val propertyClass = property.backingField?.type?.classOrNull?.owner ?: return super.visitCall(expression)
            val valueGetterCallableId = valueContainerClassInfoList.find { it.classId == propertyClass.classId }?.valueGetter ?: return super.visitCall(expression)
            val valueGetter = if (valueGetterCallableId.callableName.isGetterName()) {
                propertyClass.getPropertyGetterRecursively(valueGetterCallableId.callableName.getPropertyName())
            } else {
                val functionName = valueGetterCallableId.callableName.asString()
                propertyClass.getSimpleFunctionRecursively(functionName)
            } ?: return super.visitCall(expression)
            with(irBlockBuilder) {
                return irComposite {
                    +super.visitCall(expression)
                    +irCall(notifyValueChangeFunction).apply {
                        dispatchReceiver = irGetObject(debugServiceClass)
                        putValueArgument(0, irGet(declaration.dispatchReceiverParameter!!))
                        putValueArgument(1, irString(property.name.asString()))
                        putValueArgument(2, irCall(valueGetter).apply {
                            this.dispatchReceiver = expression.dispatchReceiver!!.deepCopyWithVariables()
                        })
                        putValueArgument(3, irGet(uuidVariable))
                    }
                }
            }
        }

        return super.visitCall(expression)
    }

    private fun IrCall.isPureSetterCall(): Boolean {
        return this.symbol.owner.isSetter && this.dispatchReceiver is IrGetValue
    }

    private fun IrCall.isValueContainerSetterCall(): Boolean {
        val property = (this.dispatchReceiver as? IrCall)?.symbol?.owner?.correspondingPropertySymbol?.owner ?: return false
        val propertyClass = property.backingField?.type?.classOrNull?.owner ?: return false
        val callingFunction = this.symbol.owner
        return valueContainerClassInfoList.any { it.classId == propertyClass.classId && it.capturedCallableIds.any { it.callableName == callingFunction.name } }
    }
}
