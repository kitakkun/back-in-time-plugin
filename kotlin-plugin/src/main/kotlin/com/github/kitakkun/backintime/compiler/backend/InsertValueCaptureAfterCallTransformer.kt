package com.github.kitakkun.backintime.compiler.backend

import com.github.kitakkun.backintime.compiler.BackInTimeAnnotations
import com.github.kitakkun.backintime.compiler.BackInTimeConsts
import com.github.kitakkun.backintime.compiler.backend.utils.getPropertyGetterRecursively
import com.github.kitakkun.backintime.compiler.backend.utils.getPropertyName
import com.github.kitakkun.backintime.compiler.backend.utils.getSimpleFunctionRecursively
import com.github.kitakkun.backintime.compiler.backend.utils.isGetterName
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.backend.js.utils.valueArguments
import org.jetbrains.kotlin.ir.builders.IrBlockBodyBuilder
import org.jetbrains.kotlin.ir.builders.IrBlockBuilder
import org.jetbrains.kotlin.ir.builders.Scope
import org.jetbrains.kotlin.ir.builders.irCall
import org.jetbrains.kotlin.ir.builders.irComposite
import org.jetbrains.kotlin.ir.builders.irGet
import org.jetbrains.kotlin.ir.builders.irGetField
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
import org.jetbrains.kotlin.ir.util.hasAnnotation
import org.jetbrains.kotlin.ir.util.isSetter
import org.jetbrains.kotlin.ir.util.parentClassOrNull
import org.jetbrains.kotlin.ir.util.statements
import org.jetbrains.kotlin.ir.visitors.IrElementTransformerVoid

class InsertValueCaptureAfterCallTransformer(
    private val pluginContext: IrPluginContext,
    private val parentMethodDeclaration: IrSimpleFunction,
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

        // 次の条件を満たすときはインラインのブロック内で値変更が呼ばれる可能性がある
        // (T) -> ? または T.() -> ? を引数にもつ 拡張関数 T.hoge()
        // withのように，receiver: T と block: T.() -> ? がある場合は，receiverの値が変更される可能性がある
        // with のようなパターンの可能性
        // 関数にメンバが渡っている場合その内部で値が変更される可能性がある

        // ピュアなvalueセッター
        // ex) this.variable = 1
        if (expression.isPureSetterCall()) {
            val parentReceiver = parentMethodDeclaration.dispatchReceiverParameter ?: return super.visitCall(expression)
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
            val property = (expression.dispatchReceiver as? IrCall)?.symbol?.owner?.correspondingPropertySymbol?.owner
                ?: (expression.extensionReceiver as? IrCall)?.symbol?.owner?.correspondingPropertySymbol?.owner ?: return super.visitCall(expression)
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
                        putValueArgument(0, irGet(parentMethodDeclaration.dispatchReceiverParameter!!))
                        putValueArgument(1, irString(property.name.asString()))
                        putValueArgument(2, irCall(valueGetter).apply {
                            this.dispatchReceiver = expression.dispatchReceiver!!.deepCopyWithVariables()
                        })
                        putValueArgument(3, irGet(uuidVariable))
                    }
                }
            }
        }

        return expression.transformComplexReceiverCall()
    }

    private fun IrCall.transformComplexReceiverCall(): IrExpression {
        val irBuilder = IrBlockBodyBuilder(
            pluginContext,
            Scope(this.symbol),
            this.startOffset,
            this.endOffset,
        )

        val extensionReceiverAsProperty = (this.extensionReceiver as? IrCall)?.symbol?.owner?.correspondingPropertySymbol?.owner
        val changedPropertiesOnValueArguments = valueArgumentValueHasChangedInternally().filterValues { it }.keys
            .mapNotNull { irCall -> irCall.symbol.owner.correspondingPropertySymbol?.owner }

        val propertiesShouldBeCapturedAfterCall = (changedPropertiesOnValueArguments + extensionReceiverAsProperty).distinct()
            .filterNotNull()
            .filter { it.parentClassOrNull?.hasAnnotation(BackInTimeAnnotations.debuggableStateHolderAnnotationFqName) == true }

        return irBuilder.irComposite {
            +this@transformComplexReceiverCall
            // FIXME: 重複コード
            +propertiesShouldBeCapturedAfterCall.mapNotNull { property ->
                val propertyClass = property.backingField?.type?.classOrNull?.owner ?: return@mapNotNull null
                val valueGetterCallableId = valueContainerClassInfoList.find { it.classId == propertyClass.classId }?.valueGetter ?: return@mapNotNull null
                val valueGetter = if (valueGetterCallableId.callableName.isGetterName()) {
                    propertyClass.getPropertyGetterRecursively(valueGetterCallableId.callableName.getPropertyName())
                } else {
                    val functionName = valueGetterCallableId.callableName.asString()
                    propertyClass.getSimpleFunctionRecursively(functionName)
                } ?: return@mapNotNull null
                irCall(notifyValueChangeFunction).apply {
                    dispatchReceiver = irGetObject(debugServiceClass)
                    putValueArgument(0, irGet(parentMethodDeclaration.dispatchReceiverParameter!!))
                    putValueArgument(1, irString(property.name.asString()))
                    putValueArgument(2, irCall(valueGetter).apply {
                        this.dispatchReceiver = irGetField(irGet(parentMethodDeclaration.dispatchReceiverParameter!!), property.backingField!!)
                    })
                    putValueArgument(3, irGet(uuidVariable))
                }
            }
        }
    }

    /**
     * 拡張関数の内部でReceiverに対して値の変更が発生するかどうかをチェックする
     * ex) 次の例では true が返る
     * fun MutableLiveData<Int>.hoge() {
     *   value = 1
     * }
     */
    private fun IrCall.extensionReceiverValueHasChangedInternally(): Boolean {
        val callingFunction = this.symbol.owner
        val extensionReceiverClass = callingFunction.extensionReceiverParameter?.type?.classOrNull?.owner

        callingFunction.body?.statements.orEmpty()
            .filterIsInstance<IrCall>()
            .forEach { call ->
                // extensionReceiverとして渡ってきた変数に対するIrCallであるか
                val toExtensionReceiverIrCall = (call.dispatchReceiver as? IrGetValue)?.symbol == callingFunction.extensionReceiverParameter?.symbol

                if (!toExtensionReceiverIrCall) return@forEach

                return valueContainerClassInfoList.firstOrNull { it.classId == extensionReceiverClass?.classId }
                    ?.capturedCallableIds.orEmpty().any { it.callableName == call.symbol.owner.name }
            }

        return false
    }

    /**
     * 引数で渡った変数が内部で変更されるかどうかをチェックする
     * ex) 次の例では true が返る
     * fun hoge(liveData: MutableLiveData<Int>) {
     *   liveData.value = 1
     * }
     * @return IrCall is valueArgument
     */
    private fun IrCall.valueArgumentValueHasChangedInternally(): Map<IrCall, Boolean> {
        val callingFunction = this.symbol.owner

        val calls = callingFunction.body?.statements.orEmpty().filterIsInstance<IrCall>()

        return this.valueArguments
            .associateWith { callingFunction.valueParameters.getOrNull(valueArguments.indexOf(it)) }
            .mapNotNull { (expression, valueParameter) ->
                val irCall = expression as? IrCall ?: return@mapNotNull null
                val parameter = valueParameter ?: return@mapNotNull null

                val valueParameterClass = parameter.type.classOrNull?.owner
                irCall to calls.any { call ->
                    // 内部でさらに拡張関数が呼び出される場合
                    val usedAsExtensionReceiver = (call.extensionReceiver as? IrGetValue)?.symbol == parameter.symbol
                    if (usedAsExtensionReceiver) {
                        return@any call.extensionReceiverValueHasChangedInternally()
                    }

                    val usedAsDispatchReceiver = (call.dispatchReceiver as? IrGetValue)?.symbol == parameter.symbol
                    if (!usedAsDispatchReceiver) return@any false
                    valueContainerClassInfoList.firstOrNull { it.classId == valueParameterClass?.classId }
                        ?.capturedCallableIds.orEmpty().any { it.callableName == call.symbol.owner.name }
                }
            }.toMap()
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
