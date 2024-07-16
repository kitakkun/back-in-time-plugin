package io.github.kitakkun.backintime.compiler.backend.transformer.capture

import io.github.kitakkun.backintime.compiler.backend.BackInTimePluginContext
import io.github.kitakkun.backintime.compiler.backend.utils.getCorrespondingProperty
import io.github.kitakkun.backintime.compiler.backend.utils.receiver
import io.github.kitakkun.backintime.compiler.valuecontainer.raw.CaptureStrategy
import io.github.kitakkun.backintime.compiler.valuecontainer.resolved.ResolvedValueContainer
import org.jetbrains.kotlin.backend.common.lower.createIrBuilder
import org.jetbrains.kotlin.ir.backend.js.utils.valueArguments
import org.jetbrains.kotlin.ir.builders.irBlockBody
import org.jetbrains.kotlin.ir.builders.irCall
import org.jetbrains.kotlin.ir.builders.irComposite
import org.jetbrains.kotlin.ir.builders.irGet
import org.jetbrains.kotlin.ir.builders.irString
import org.jetbrains.kotlin.ir.declarations.IrValueParameter
import org.jetbrains.kotlin.ir.declarations.IrVariable
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.IrFunctionExpression
import org.jetbrains.kotlin.ir.expressions.IrGetValue
import org.jetbrains.kotlin.ir.symbols.IrClassSymbol
import org.jetbrains.kotlin.ir.symbols.IrSimpleFunctionSymbol
import org.jetbrains.kotlin.ir.types.classOrNull
import org.jetbrains.kotlin.ir.util.fqNameWhenAvailable
import org.jetbrains.kotlin.ir.util.statements

context(BackInTimePluginContext)
fun IrCall.captureIfNeeded(
    parentClassSymbol: IrClassSymbol,
    classDispatchReceiverParameter: IrValueParameter,
    uuidVariable: IrVariable,
): IrExpression? {
    val property = receiver?.getCorrespondingProperty() ?: return null
    val valueContainer = valueContainerClassInfoList.find { it.classSymbol == property.getter?.returnType?.classOrNull } ?: return null
    val captureStrategy = valueContainer.captureTargetSymbols.firstOrNull { it.first == this.symbol }?.second ?: return null

    val ownerClassName = parentClassSymbol.owner.fqNameWhenAvailable?.asString() ?: return null
    val propertyName = property.name.asString()

    val propertyGetterSymbol = property.getter?.symbol ?: return null

    return when (captureStrategy) {
        is CaptureStrategy.AfterCall -> {
            captureAfterCall(
                propertyGetter = propertyGetterSymbol,
                classDispatchReceiverParameter = classDispatchReceiverParameter,
                uuidVariable = uuidVariable,
                ownerClassName = ownerClassName,
                propertyName = propertyName,
                valueContainer = valueContainer,
            )
        }

        is CaptureStrategy.LambdaLastExpression -> {
            captureLambdaLastExpression(
                classDispatchReceiverParameter = classDispatchReceiverParameter,
                uuidVariable = uuidVariable,
                ownerClassName = ownerClassName,
                propertyName = propertyName,
                index = captureStrategy.index,
            ) ?: captureAfterCall(
                propertyGetter = propertyGetterSymbol,
                classDispatchReceiverParameter = classDispatchReceiverParameter,
                uuidVariable = uuidVariable,
                ownerClassName = ownerClassName,
                propertyName = propertyName,
                valueContainer = valueContainer,
            )
        }

        is CaptureStrategy.ValueArgument -> {
            captureValueArgument(
                classDispatchReceiverParameter = classDispatchReceiverParameter,
                uuidVariable = uuidVariable,
                ownerClassName = ownerClassName,
                propertyName = propertyName,
                index = captureStrategy.index,
            )
        }
    }
}

context(BackInTimePluginContext)
private fun IrCall.captureAfterCall(
    classDispatchReceiverParameter: IrValueParameter,
    ownerClassName: String,
    uuidVariable: IrVariable,
    propertyName: String,
    valueContainer: ResolvedValueContainer,
    propertyGetter: IrSimpleFunctionSymbol,
): IrExpression {
    val irBuilder = irBuiltIns.createIrBuilder(symbol)
    return irBuilder.irComposite {
        +this@captureAfterCall
        +irCall(reportPropertyValueChangeFunctionSymbol).apply {
            putValueArgument(0, irGet(classDispatchReceiverParameter))
            putValueArgument(1, irString(ownerClassName))
            putValueArgument(2, irGet(uuidVariable))
            putValueArgument(3, irString(propertyName))
            putValueArgument(
                index = 4,
                valueArgument = when (valueContainer) {
                    is ResolvedValueContainer.Wrapper -> {
                        irCall(valueContainer.getterSymbol).apply {
                            dispatchReceiver = irCall(propertyGetter).apply {
                                dispatchReceiver = irGet(classDispatchReceiverParameter)
                            }
                        }
                    }

                    is ResolvedValueContainer.SelfContained -> {
                        irCall(propertyGetter).apply {
                            dispatchReceiver = irGet(classDispatchReceiverParameter)
                        }
                    }
                },
            )
        }
    }
}

context(BackInTimePluginContext)
private fun IrCall.captureValueArgument(
    index: Int,
    classDispatchReceiverParameter: IrValueParameter,
    ownerClassName: String,
    uuidVariable: IrVariable,
    propertyName: String,
): IrExpression {
    val irBuilder = irBuiltIns.createIrBuilder(symbol)
    val originalValueArgument = getValueArgument(index)

    putValueArgument(
        index = index,
        valueArgument = with(irBuilder) {
            irCall(captureThenReturnValueFunctionSymbol).apply {
                putValueArgument(0, irGet(classDispatchReceiverParameter))
                putValueArgument(1, irString(ownerClassName))
                putValueArgument(2, irGet(uuidVariable))
                putValueArgument(3, irString(propertyName))
                putValueArgument(4, originalValueArgument)
            }
        },
    )
    return this
}

context(BackInTimePluginContext)
private fun IrCall.captureLambdaLastExpression(
    classDispatchReceiverParameter: IrValueParameter,
    uuidVariable: IrVariable,
    ownerClassName: String,
    propertyName: String,
    index: Int,
): IrExpression? {
    val valueArgument = valueArguments[index] ?: return null
    when (valueArgument) {
        is IrFunctionExpression -> {
            val function = valueArgument.function
            val statements = function.body?.statements ?: return null
            val lastExpression = statements.last() as? IrExpression ?: return null
            with(irBuiltIns.createIrBuilder(function.symbol)) {
                function.body = irBlockBody {
                    +statements.dropLast(1)
                    irCall(captureThenReturnValueFunctionSymbol).apply {
                        putValueArgument(0, irGet(classDispatchReceiverParameter))
                        putValueArgument(1, irString(ownerClassName))
                        putValueArgument(2, irGet(uuidVariable))
                        putValueArgument(3, irString(propertyName))
                        putValueArgument(4, lastExpression)
                    }
                }
            }
            return this@captureLambdaLastExpression
        }

        // shared lambda object can't be modified to capture value changes
        is IrGetValue -> return null

        else -> return null
    }
}
