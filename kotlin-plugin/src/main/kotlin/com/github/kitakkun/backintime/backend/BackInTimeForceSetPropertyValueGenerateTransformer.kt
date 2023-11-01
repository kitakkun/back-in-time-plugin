package com.github.kitakkun.backintime.backend

import com.github.kitakkun.backintime.BackInTimeConsts
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.builders.*
import org.jetbrains.kotlin.ir.declarations.*
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.types.classFqName
import org.jetbrains.kotlin.ir.types.classOrNull
import org.jetbrains.kotlin.ir.util.fqNameWhenAvailable
import org.jetbrains.kotlin.ir.util.parentClassOrNull
import org.jetbrains.kotlin.ir.util.properties
import org.jetbrains.kotlin.ir.visitors.IrElementTransformerVoid

class BackInTimeForceSetPropertyValueGenerateTransformer(
    private val pluginContext: IrPluginContext,
) : IrElementTransformerVoid() {
    private fun shouldGenerateFunctionBody(parentClass: IrClass, declaration: IrSimpleFunction): Boolean {
        return parentClass.superTypes.any { it.classFqName == BackInTimeConsts.debuggableStateHolderManipulatorFqName }
            && declaration.name == BackInTimeConsts.forceSetPropertyValueForBackInDebugMethodName
    }

    override fun visitSimpleFunction(declaration: IrSimpleFunction): IrStatement {
        val parentClass = declaration.parentClassOrNull ?: return super.visitSimpleFunction(declaration)
        if (!shouldGenerateFunctionBody(parentClass, declaration)) return super.visitSimpleFunction(declaration)

        declaration.body = IrBlockBodyBuilder(
            context = pluginContext,
            startOffset = declaration.startOffset,
            endOffset = declaration.endOffset,
            scope = Scope(declaration.symbol),
        ).blockBody {
            val propertyName = declaration.valueParameters[0]
            val value = declaration.valueParameters[1]
            +irWhen(
                type = irUnit().type,
                branches = parentClass.properties.map { property ->
                    irBranch(
                        condition = irEquals(irGet(propertyName), irString(property.name.asString())),
                        result = generateSetForProperty(declaration, property, value)
                    )
                }.toList() + irElseBranch(irBlock {}),
            )
        }
        return super.visitFunction(declaration)
    }

    private fun IrBlockBodyBuilder.generateSetForProperty(declaration: IrFunction, property: IrProperty, value: IrValueParameter): IrExpression {
        val backingField = property.backingField ?: return irBlock { }
        val backingFieldType = backingField.type
        val backingFieldClass = backingField.type.classOrNull ?: return irBlock { }
        if (property.isVar && backingFieldType.isKotlinPrimitiveType()) {
            return irSetField(
                receiver = irGet(declaration.dispatchReceiverParameter!!),
                field = backingField,
                value = irGet(value),
            )
        }

        when (backingFieldClass.owner.fqNameWhenAvailable) {
            BackInTimeConsts.mutableLiveDataFqName -> {
                val postValueMethod = pluginContext.referenceFunctions(BackInTimeConsts.mutableLiveDataPostValueCallableId).single()
                return irCall(postValueMethod).apply {
                    dispatchReceiver = irGetField(receiver = null, field = backingField)
                    putValueArgument(0, irGet(value))
                }
            }

            BackInTimeConsts.mutableStateFlowFqName -> {
//                MessageCollectorHolder.messageCollector.report(
//                    CompilerMessageSeverity.ERROR,
//                    "STATEFLOW!!"
//                )
            }
        }

        return irBlock { }
    }
}
