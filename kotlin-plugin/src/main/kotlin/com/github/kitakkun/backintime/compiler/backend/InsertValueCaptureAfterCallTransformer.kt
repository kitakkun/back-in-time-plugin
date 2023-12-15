package com.github.kitakkun.backintime.compiler.backend

import com.github.kitakkun.backintime.compiler.BackInTimeAnnotations
import com.github.kitakkun.backintime.compiler.BackInTimeConsts
import com.github.kitakkun.backintime.compiler.backend.utils.getPropertyGetterRecursively
import com.github.kitakkun.backintime.compiler.backend.utils.getPropertyName
import com.github.kitakkun.backintime.compiler.backend.utils.getSimpleFunctionRecursively
import com.github.kitakkun.backintime.compiler.backend.utils.irBlockBodyBuilder
import com.github.kitakkun.backintime.compiler.backend.utils.irBlockBuilder
import com.github.kitakkun.backintime.compiler.backend.utils.isGetterName
import com.github.kitakkun.backintime.compiler.ext.filterKeysNotNull
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.jvm.ir.parentClassId
import org.jetbrains.kotlin.ir.backend.js.utils.valueArguments
import org.jetbrains.kotlin.ir.builders.IrBuilderWithScope
import org.jetbrains.kotlin.ir.builders.irBlockBody
import org.jetbrains.kotlin.ir.builders.irCall
import org.jetbrains.kotlin.ir.builders.irComposite
import org.jetbrains.kotlin.ir.builders.irEquals
import org.jetbrains.kotlin.ir.builders.irGet
import org.jetbrains.kotlin.ir.builders.irGetField
import org.jetbrains.kotlin.ir.builders.irGetObject
import org.jetbrains.kotlin.ir.builders.irIfThen
import org.jetbrains.kotlin.ir.builders.irString
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.declarations.IrValueParameter
import org.jetbrains.kotlin.ir.declarations.IrVariable
import org.jetbrains.kotlin.ir.deepCopyWithVariables
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.IrFunctionExpression
import org.jetbrains.kotlin.ir.expressions.IrGetValue
import org.jetbrains.kotlin.ir.expressions.IrReturn
import org.jetbrains.kotlin.ir.expressions.IrTypeOperatorCall
import org.jetbrains.kotlin.ir.types.classOrNull
import org.jetbrains.kotlin.ir.util.classId
import org.jetbrains.kotlin.ir.util.getSimpleFunction
import org.jetbrains.kotlin.ir.util.hasAnnotation
import org.jetbrains.kotlin.ir.util.isFunction
import org.jetbrains.kotlin.ir.util.isSetter
import org.jetbrains.kotlin.ir.util.parentClassOrNull
import org.jetbrains.kotlin.ir.util.statements
import org.jetbrains.kotlin.ir.visitors.IrElementTransformerVoid

class InsertValueCaptureAfterCallTransformer(
    private val pluginContext: IrPluginContext,
    private val classDispatchReceiverParameter: IrValueParameter,
    private val uuidVariable: IrVariable,
    private val valueContainerClassInfoList: List<ValueContainerClassInfo>,
) : IrElementTransformerVoid() {
    companion object {
        private const val EXTENSION_RECEIVER_PARAMETER_INDEX = -1
    }

    private val debugServiceClass = pluginContext.referenceClass(BackInTimeConsts.backInTimeDebugServiceClassId)!!
    private val notifyValueChangeFunction = debugServiceClass.getSimpleFunction(BackInTimeConsts.notifyPropertyChanged)!!

    override fun visitExpression(expression: IrExpression): IrExpression {
        when (expression) {
            is IrCall -> {
                expression.dispatchReceiver?.let { visitExpression(it) }
                expression.extensionReceiver?.let { visitExpression(it) }
            }

            is IrTypeOperatorCall -> {
                visitExpression(expression.argument)
            }

            is IrReturn -> {
                visitExpression(expression.value)
            }
        }
        return super.visitExpression(expression)
    }

    override fun visitCall(expression: IrCall): IrExpression {
        return when {
            expression.isPureSetterCall() -> expression.transformPureSetterCall()
            expression.isValueContainerSetterCall() -> expression.transformValueContainerSetterCall()
            expression.symbol.owner.isInline -> expression.transformComplexReceiverCallInline()
            else -> expression.transformComplexReceiverCall()
        } ?: super.visitCall(expression)
    }

    /**
     * ex)
     * var hoge: Int = 0
     * hoge = 1
     */
    private fun IrCall.transformPureSetterCall(): IrExpression? {
        val property = this.symbol.owner.correspondingPropertySymbol?.owner ?: return null
        val propertyGetter = property.getter ?: return null
        val dispatchReceiver = this.dispatchReceiver?.deepCopyWithVariables() ?: return null
        return irBlockBodyBuilder(pluginContext).irComposite {
            +this@transformPureSetterCall
            +generateNotifyValueChangeCall(
                propertyName = property.name.asString(),
                getValueCall = irCall(propertyGetter).apply { this.dispatchReceiver = dispatchReceiver }
            )
        }
    }

    /**
     * ex)
     * var hoge: MutableLiveData<Int> = MutableLiveData(0)
     * hoge.value = 1
     */
    private fun IrCall.transformValueContainerSetterCall(): IrExpression? {
        val property = (dispatchReceiver as? IrCall)?.symbol?.owner?.correspondingPropertySymbol?.owner
            ?: (extensionReceiver as? IrCall)?.symbol?.owner?.correspondingPropertySymbol?.owner
            ?: return null
        val propertyClass = property.backingField?.type?.classOrNull?.owner ?: return null
        val valueGetterCallableId = valueContainerClassInfoList.find { it.classId == propertyClass.classId }?.valueGetter ?: return null
        val valueGetter = if (valueGetterCallableId.callableName.isGetterName()) {
            propertyClass.getPropertyGetterRecursively(valueGetterCallableId.callableName.getPropertyName())
        } else {
            val functionName = valueGetterCallableId.callableName.asString()
            propertyClass.getSimpleFunctionRecursively(functionName)
        } ?: return null

        val propertyGetter = property.getter ?: return null

        return irBlockBodyBuilder(pluginContext).irComposite {
            +this@transformValueContainerSetterCall
            +generateNotifyValueChangeCall(
                propertyName = property.name.asString(),
                getValueCall = irCall(valueGetter).apply {
                    this.dispatchReceiver = irCall(propertyGetter).apply {
                        this.dispatchReceiver = irGet(classDispatchReceiverParameter)
                    }
                }
            )
        }
    }

    /**
     * インライン関数にのみ適用可能
     */
    private fun IrCall.transformComplexReceiverCallInline(): IrExpression {
        val irCalls = this.valueArguments.filterIsInstance<IrCall>()
            .plus(this.extensionReceiver as? IrCall)
            .filterNotNull()
        val lambdas = this.valueArguments.filterIsInstance<IrFunctionExpression>()

        lambdas.forEach { expression ->
            val callsInsideLambda = expression.function.body?.statements.orEmpty()

            expression.function.body = expression.function.irBlockBuilder(pluginContext).irBlockBody {
                callsInsideLambda.forEach callsInsideLambdaForEach@{ call ->
                    +call

                    if (call !is IrCall) return@callsInsideLambdaForEach

                    val callClassId = call.symbol.owner.parentClassId
                    val valueContainerInfo = valueContainerClassInfoList.find { it.classId == callClassId } ?: return@callsInsideLambdaForEach
                    val callDispatchReceiver = call.dispatchReceiver ?: return@callsInsideLambdaForEach
                    // FIXME: 本当はircall全てについて判定が必要
                    val getPropertyCall = irCalls.firstOrNull() ?: return@callsInsideLambdaForEach
                    val property = getPropertyCall.symbol.owner.correspondingPropertySymbol?.owner ?: return@callsInsideLambdaForEach
                    if (call.symbol.owner.name !in valueContainerInfo.capturedCallableIds.map { it.callableName }) return@callsInsideLambdaForEach

                    val propertyClass = property.backingField?.type?.classOrNull?.owner ?: return@callsInsideLambdaForEach
                    val valueGetterCallableId = valueContainerClassInfoList.find { it.classId == propertyClass.classId }?.valueGetter ?: return@callsInsideLambdaForEach
                    val valueGetter = if (valueGetterCallableId.callableName.isGetterName()) {
                        propertyClass.getPropertyGetterRecursively(valueGetterCallableId.callableName.getPropertyName())
                    } else {
                        val functionName = valueGetterCallableId.callableName.asString()
                        propertyClass.getSimpleFunctionRecursively(functionName)
                    } ?: return@callsInsideLambdaForEach

                    +irIfThen(
                        condition = irEquals(callDispatchReceiver, getPropertyCall.deepCopyWithVariables()),
                        thenPart = generateNotifyValueChangeCall(
                            propertyName = property.name.asString(),
                            getValueCall = irCall(valueGetter).apply {
                                this.dispatchReceiver = irGetField(
                                    receiver = irGet(classDispatchReceiverParameter),
                                    field = property.backingField!!,
                                )
                            }
                        ),
                        type = pluginContext.irBuiltIns.unitType,
                    )
                }
            }
        }
        return this
    }


    /**
     * non-inline な関数にのみ適用可能
     */
    private fun IrCall.transformComplexReceiverCall(): IrExpression {
        val mapping = this.getExpressionToLambdaMapping()
            .mapNotNull { (value, lambda) ->
                val valueExpression = value as? IrCall ?: return@mapNotNull null
                val lambdaExpression = lambda.filterIsInstance<IrFunctionExpression>()
                if (lambdaExpression.isEmpty()) return@mapNotNull null
                valueExpression to lambdaExpression
            }.toMap()

        mapping.forEach { (callExpression, lambdaExpressions) ->
            val property = callExpression.symbol.owner.correspondingPropertySymbol?.owner ?: return@forEach

            lambdaExpressions.forEach { lambdaExpression ->
                val callsInsideLambda = lambdaExpression.function.body?.statements.orEmpty().filterIsInstance<IrCall>()

                lambdaExpression.function.body = lambdaExpression.function.irBlockBuilder(pluginContext).irBlockBody {
                    callsInsideLambda.forEach callsInsideLambda@{ call ->
                        +call

                        val dispatchReceiver = call.dispatchReceiver as? IrGetValue
                        val dispatchReceiverClass = dispatchReceiver?.symbol?.owner?.parentClassOrNull

                        if (dispatchReceiverClass?.classId != property.getter?.parentClassId) return@callsInsideLambda

                        val valueContainerInfo = valueContainerClassInfoList.find { it.classId == property.getter?.parentClassId } ?: return@callsInsideLambda

                        val shouldBeCaptured = valueContainerInfo.capturedCallableIds.any { it.callableName == call.symbol.owner.name }

                        if (!shouldBeCaptured) return@callsInsideLambda

                        val valueGetterCallableId = valueContainerInfo.valueGetter
                        val valueGetter = if (valueGetterCallableId.callableName.isGetterName()) {
                            property.getter?.parentClassOrNull?.getPropertyGetterRecursively(valueGetterCallableId.callableName.getPropertyName())
                        } else {
                            val functionName = valueGetterCallableId.callableName.asString()
                            property.getter?.parentClassOrNull?.getSimpleFunctionRecursively(functionName)
                        } ?: return@callsInsideLambda

                        +generateNotifyValueChangeCall(
                            propertyName = property.name.asString(),
                            getValueCall = irCall(valueGetter).apply {
                                this.dispatchReceiver = irGetField(
                                    receiver = irGet(classDispatchReceiverParameter),
                                    field = property.backingField!!,
                                )
                            },
                        )
                    }
                }
            }
        }

        val extensionReceiverAsProperty = (this.extensionReceiver as? IrCall)?.symbol?.owner?.correspondingPropertySymbol?.owner
        val changedPropertiesOnValueArguments = valueArgumentValueHasChangedInternally().filterValues { it }.keys
            .mapNotNull { irCall -> irCall.symbol.owner.correspondingPropertySymbol?.owner }

        val propertiesShouldBeCapturedAfterCall = (changedPropertiesOnValueArguments + extensionReceiverAsProperty).distinct()
            .filterNotNull()
            .filter { it.parentClassOrNull?.hasAnnotation(BackInTimeAnnotations.debuggableStateHolderAnnotationFqName) == true }

        val irBuilder = irBlockBuilder(pluginContext)
        val propertiesCaptureCode = with(irBuilder) {
            propertiesShouldBeCapturedAfterCall.mapNotNull { property ->
                val propertyClass = property.backingField?.type?.classOrNull?.owner ?: return@mapNotNull null
                val valueGetterCallableId = valueContainerClassInfoList.find { it.classId == propertyClass.classId }?.valueGetter ?: return@mapNotNull null
                val valueGetter = if (valueGetterCallableId.callableName.isGetterName()) {
                    propertyClass.getPropertyGetterRecursively(valueGetterCallableId.callableName.getPropertyName())
                } else {
                    val functionName = valueGetterCallableId.callableName.asString()
                    propertyClass.getSimpleFunctionRecursively(functionName)
                } ?: return@mapNotNull null
                generateNotifyValueChangeCall(
                    propertyName = property.name.asString(),
                    getValueCall = irCall(valueGetter).apply {
                        this.dispatchReceiver = irGetField(irGet(classDispatchReceiverParameter), property.backingField!!)
                    }
                )
            }
        }

        if (propertiesCaptureCode.isEmpty()) return this

        return irBuilder.irComposite {
            +this@transformComplexReceiverCall
            +propertiesCaptureCode
        }
    }

    /**
     * extensionReceiverあるいはvalueArgumentsとして渡された変数に対して，同様に渡されたラムダ式のうちどれが適用されているかのマッピング情報を返す
     *
     * 補足情報: extensionReceiverのindexは-1
     * ex:
     * fun hoge(valueParameter1: Int, valueParameter2: Int, lambda1: (Int) -> Unit, lambda2: (Int) -> Unit) {
     *   lambda1(valueParameter1)
     *   lambda2(valueParameter1)
     *   lambda2(valueParameter2)
     * }
     *
     * output:
     * valueParameter1 -> [lambda1, lambda2]
     * valueParameter2 -> [lambda2]
     */
    private fun IrFunction.getValueParameterToLambdaParametersMapping(): Map<IrValueParameter, List<IrValueParameter>> {
        val allParameters = this.valueParameters
            .plus(this.extensionReceiverParameter)
            .filterNotNull()

        val valueParameters = allParameters.filter { !it.type.isFunction() }
        val lambdaParameters = allParameters.filter { it.type.isFunction() }

        val lambdaCallsInBody = this.body?.statements.orEmpty()
            .filterIsInstance<IrCall>()
            .filter { (it.dispatchReceiver as? IrGetValue)?.symbol?.owner in lambdaParameters }

        return valueParameters.associateWith { valueParameter ->
            // ラムダ引数は内部ではクラスに変換され，invokeメソッドが呼ばれるようになるので，
            // extensionReceiverとして渡ってきた値もinvokeメソッドの引数として渡ってくる
            lambdaCallsInBody
                .filter { call -> call.valueArguments.filterIsInstance<IrGetValue>().any { it.symbol == valueParameter.symbol } }
                .mapNotNull { it.dispatchReceiver as? IrGetValue }
                .mapNotNull { lambdaParameters.find { lambdaParameter -> it.symbol.owner == lambdaParameter } }
        }
    }

    private fun IrCall.getExpressionToLambdaMapping(): Map<IrExpression, List<IrExpression>> {
        return this.symbol.owner.getValueParameterToLambdaParametersMapping()
            .mapKeys { (valueParameter, _) ->
                // FIXME: dispatchReceiverはどうしよう
                val index = valueParameter.index
                when (index) {
                    EXTENSION_RECEIVER_PARAMETER_INDEX -> this.extensionReceiver
                    else -> this.valueArguments.getOrNull(index)
                }
            }
            .mapValues { (_, lambdaParameters) ->
                lambdaParameters.mapNotNull { lambdaParameter ->
                    val index = lambdaParameter.index
                    when (index) {
                        EXTENSION_RECEIVER_PARAMETER_INDEX -> this.extensionReceiver
                        else -> this.valueArguments.getOrNull(index)
                    }
                }
            }
            .filterKeysNotNull()
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
        return this.symbol.owner.isSetter && (this.dispatchReceiver as? IrGetValue)?.symbol == classDispatchReceiverParameter.symbol
    }

    private fun IrCall.isValueContainerSetterCall(): Boolean {
        val receiver = (this.extensionReceiver as? IrCall) ?: (this.dispatchReceiver as? IrCall) ?: return false
        val property = receiver.symbol.owner.correspondingPropertySymbol?.owner ?: return false
        val propertyClass = property.backingField?.type?.classOrNull?.owner ?: return false
        val callingFunction = this.symbol.owner
        return valueContainerClassInfoList.any { it.classId == propertyClass.classId && it.capturedCallableIds.any { it.callableName == callingFunction.name } }
    }

    private fun IrBuilderWithScope.generateNotifyValueChangeCall(
        propertyName: String,
        getValueCall: IrExpression,
    ): IrCall {
        return irCall(notifyValueChangeFunction).apply {
            dispatchReceiver = irGetObject(debugServiceClass)
            putValueArgument(0, irGet(classDispatchReceiverParameter))
            putValueArgument(1, irString(propertyName))
            putValueArgument(2, getValueCall)
            putValueArgument(3, irGet(uuidVariable))
        }
    }
}
