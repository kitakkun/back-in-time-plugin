package com.github.kitakkun.backintime.compiler.backend.transformer

import com.github.kitakkun.backintime.compiler.backend.BackInTimePluginContext
import com.github.kitakkun.backintime.compiler.backend.utils.getPropertyGetterRecursively
import com.github.kitakkun.backintime.compiler.backend.utils.getPropertyName
import com.github.kitakkun.backintime.compiler.backend.utils.getSimpleFunctionRecursively
import com.github.kitakkun.backintime.compiler.backend.utils.irBlockBodyBuilder
import com.github.kitakkun.backintime.compiler.backend.utils.irBlockBuilder
import com.github.kitakkun.backintime.compiler.backend.utils.isGetterName
import com.github.kitakkun.backintime.compiler.ext.filterKeysNotNull
import com.github.kitakkun.backintime.compiler.ext.filterValuesNotNull
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.backend.js.utils.valueArguments
import org.jetbrains.kotlin.ir.builders.IrBuilderWithScope
import org.jetbrains.kotlin.ir.builders.irCall
import org.jetbrains.kotlin.ir.builders.irComposite
import org.jetbrains.kotlin.ir.builders.irEquals
import org.jetbrains.kotlin.ir.builders.irGet
import org.jetbrains.kotlin.ir.builders.irGetObject
import org.jetbrains.kotlin.ir.builders.irIfThen
import org.jetbrains.kotlin.ir.builders.irString
import org.jetbrains.kotlin.ir.declarations.IrProperty
import org.jetbrains.kotlin.ir.declarations.IrValueParameter
import org.jetbrains.kotlin.ir.declarations.IrVariable
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.IrFunctionExpression
import org.jetbrains.kotlin.ir.expressions.IrGetValue
import org.jetbrains.kotlin.ir.expressions.IrReturn
import org.jetbrains.kotlin.ir.expressions.IrTypeOperatorCall
import org.jetbrains.kotlin.ir.symbols.IrSimpleFunctionSymbol
import org.jetbrains.kotlin.ir.types.classOrNull
import org.jetbrains.kotlin.ir.util.classId
import org.jetbrains.kotlin.ir.util.isSetter
import org.jetbrains.kotlin.ir.util.statements
import org.jetbrains.kotlin.ir.visitors.IrElementTransformerVoid
import org.jetbrains.kotlin.ir.visitors.transformChildrenVoid

context(BackInTimePluginContext)
class InsertValueCaptureAfterCallTransformer(
    private val classDispatchReceiverParameter: IrValueParameter,
    private val uuidVariable: IrVariable,
) : IrElementTransformerVoid() {
    override fun visitElement(element: IrElement): IrElement {
        element.transformChildrenVoid(this)
        return element
    }

    override fun visitCall(expression: IrCall): IrExpression {
        expression.transformChildrenVoid(this)

        return when {
            expression.isPureSetterCall() -> expression.transformPureSetterCall()
            expression.isValueContainerSetterCall() -> expression.transformValueContainerSetterCall()
            else -> expression.transformComplexCall()
        } ?: expression
    }

    /**
     * insert capturing call for pure variable property
     */
    private fun IrCall.transformPureSetterCall(): IrExpression? {
        val irBuilder = irBlockBuilder(pluginContext)
        val property = this.symbol.owner.correspondingPropertySymbol?.owner ?: return null
        val propertyGetter = property.getter ?: return null
        return irBuilder.irComposite {
            +this@transformPureSetterCall
            +generateCaptureValueCall(
                propertyName = property.name.asString(),
                getValueCall = irCall(propertyGetter.symbol).apply {
                    this.dispatchReceiver = irGet(classDispatchReceiverParameter)
                },
            )
        }
    }

    /**
     * insert capturing call for value container property
     */
    private fun IrCall.transformValueContainerSetterCall(): IrExpression? {
        val receiver = dispatchReceiver ?: extensionReceiver ?: return null
        val property = (receiver as? IrCall)?.symbol?.owner?.correspondingPropertySymbol?.owner ?: return null
        val irBuilder = irBlockBodyBuilder(pluginContext)
        val getValueCall = with(irBuilder) {
            property.generateValueHolderCaptureCall() ?: return null
        }
        return irBuilder.irComposite {
            +this@transformValueContainerSetterCall
            +getValueCall
        }
    }

    /**
     * insert capturing call for complex call
     * with, apply, let, ...
     */
    private fun IrCall.transformComplexCall(): IrExpression {
        val passedProperties = this.valueArguments.filterIsInstance<IrCall>()
            .plus(this.extensionReceiver as? IrCall)
            .filterNotNull()
            .mapNotNull { it.symbol.owner.correspondingPropertySymbol?.owner }

        if (passedProperties.isEmpty()) return this

        // ラムダ式の中でプロパティの値変更処理が発生する場合，その後ろに比較→キャプチャを挿入する
        val lambdas = this.valueArguments.filterIsInstance<IrFunctionExpression>()

        lambdas
            .map { it.function.symbol.owner }
            .forEach { function ->
                val statementsAsExpressions = function.body?.statements.orEmpty()
                function.body = function.irBlockBodyBuilder(pluginContext).blockBody {
                    statementsAsExpressions.forEach {
                        if (it is IrExpression) {
                            +it.transformCallInsideLambda(passedProperties)
                        } else {
                            +it
                        }
                    }
                }
            }

        val propertiesShouldBeCapturedAfterCall = internallyChangedPassedParams().mapNotNull { it.symbol.owner.correspondingPropertySymbol?.owner }
        val irBuilder = irBlockBuilder(pluginContext)
        val propertyCaptureCalls = propertiesShouldBeCapturedAfterCall.mapNotNull { property ->
            with(irBuilder) {
                property.generateValueHolderCaptureCall()
            }
        }

        if (propertyCaptureCalls.isEmpty()) return this

        return irBuilder.irComposite {
            +this@transformComplexCall
            +propertyCaptureCalls
        }
    }

    private fun IrCall.internallyChangedPassedParams(): List<IrCall> {
        val function = this.symbol.owner
        val callsInsideFunction = function.body?.statements ?: return emptyList()

        val valueParameterToExpressionMapping = function.valueParameters.associateWith { this.valueArguments.getOrNull(it.index) }
            .plus(function.extensionReceiverParameter to this.extensionReceiver)
            .filterKeysNotNull()
            .filterValuesNotNull()

        return valueParameterToExpressionMapping.values.filterIsInstance<IrCall>()
            .filter { call ->
                val correspondingParameter = valueParameterToExpressionMapping.entries.find { it.value == call }?.key ?: return@filter false
                callsInsideFunction.any { statement -> statement.containsParameterValueChange(correspondingParameter) }
            }
    }

    private fun IrStatement.containsParameterValueChange(parameter: IrValueParameter): Boolean {
        when (this) {
            is IrCall -> {
                val dispatchReceiverContains = this.dispatchReceiver?.containsParameterValueChange(parameter) ?: false
                val extensionReceiverContains = this.extensionReceiver?.containsParameterValueChange(parameter) ?: false
                val valueArgumentsContains = this.valueArguments.any { it?.containsParameterValueChange(parameter) ?: false }

                val receiver = this.extensionReceiver ?: this.dispatchReceiver ?: return false
                val receiverClassId = receiver.type.classOrNull?.owner?.classId ?: return false
                val receiverParameter = (receiver as? IrGetValue)?.symbol?.owner

                if (receiverParameter != parameter) return false

                val valueContainerInfo = valueContainerClassInfoList.find { it.classId == receiverClassId } ?: return false
                val captureTargetNames = valueContainerInfo.capturedCallableIds.map { it.callableName }

                return this.symbol.owner.name in captureTargetNames || dispatchReceiverContains || extensionReceiverContains || valueArgumentsContains
            }

            is IrTypeOperatorCall -> {
                return this.argument.containsParameterValueChange(parameter)
            }

            is IrReturn -> {
                return this.value.containsParameterValueChange(parameter)
            }

            else -> return false
        }
    }

    /**
     * insert capture calls inside lambda argument
     */
    private fun IrExpression.transformCallInsideLambda(
        passedProperties: List<IrProperty>,
    ): IrExpression {
        when (this) {
            is IrCall -> {
                this.dispatchReceiver = this.dispatchReceiver?.transformCallInsideLambda(passedProperties)
                this.extensionReceiver = this.extensionReceiver?.transformCallInsideLambda(passedProperties)
                this.valueArguments.map { it?.transformCallInsideLambda(passedProperties) }.forEachIndexed { index, transformedExpression ->
                    this.putValueArgument(index, transformedExpression)
                }

                val receiver = this.extensionReceiver ?: this.dispatchReceiver ?: return this
                val receiverClassId = receiver.type.classOrNull?.owner?.classId ?: return this

                val valueContainerInfo = valueContainerClassInfoList.find { it.classId == receiverClassId } ?: return this
                val captureTargetNames = valueContainerInfo.capturedCallableIds.map { it.callableName }

                if (this.symbol.owner.name !in captureTargetNames) return this

                val irBuilder = irBlockBuilder(pluginContext)

                val captureCalls = passedProperties
                    .filter { it.getter?.returnType?.classOrNull?.owner?.classId == receiverClassId }
                    .mapNotNull { property ->
                        with(irBuilder) {
                            val propertyGetter = property.getter ?: return@mapNotNull null
                            val captureCall = property.generateValueHolderCaptureCall() ?: return@mapNotNull null
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

                if (captureCalls.isEmpty()) return this

                return irBlockBodyBuilder(pluginContext).irComposite {
                    +this@transformCallInsideLambda
                    +captureCalls
                }
            }

            is IrTypeOperatorCall -> {
                argument = argument.transformCallInsideLambda(passedProperties)
            }

            is IrReturn -> {
                value = value.transformCallInsideLambda(passedProperties)
            }
        }
        return this
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

    context(IrBuilderWithScope)
    private fun generateCaptureValueCall(propertyName: String, getValueCall: IrCall): IrCall {
        return irCall(notifyValueChangeFunctionSymbol).apply {
            dispatchReceiver = irGetObject(backInTimeServiceClassSymbol)
            putValueArgument(0, irGet(classDispatchReceiverParameter))
            putValueArgument(1, irString(propertyName))
            putValueArgument(2, getValueCall)
            putValueArgument(3, irGet(uuidVariable))
        }
    }

    context(IrBuilderWithScope)
    private fun IrProperty.generateValueHolderCaptureCall(): IrCall? {
        val propertyGetter = this.getter ?: return null
        val valueGetter = this.getValueHolderValueGetterSymbol() ?: return null
        return generateCaptureValueCall(
            propertyName = name.asString(),
            getValueCall = irCall(valueGetter).apply {
                this.dispatchReceiver = irCall(propertyGetter).apply {
                    this.dispatchReceiver = irGet(classDispatchReceiverParameter)
                }
            }
        )
    }

    private fun IrProperty.getValueHolderValueGetterSymbol(): IrSimpleFunctionSymbol? {
        val propertyClass = getter?.returnType?.classOrNull?.owner ?: return null
        val valueGetterCallableId = valueContainerClassInfoList.find { it.classId == propertyClass.classId }?.valueGetter ?: return null
        return if (valueGetterCallableId.callableName.isGetterName()) {
            propertyClass.getPropertyGetterRecursively(valueGetterCallableId.callableName.getPropertyName())
        } else {
            val functionName = valueGetterCallableId.callableName.asString()
            propertyClass.getSimpleFunctionRecursively(functionName)
        }
    }
}
