package com.github.kitakkun.backintime.backend

import com.github.kitakkun.backintime.BackInTimeConsts
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.builders.*
import org.jetbrains.kotlin.ir.declarations.IrDeclarationOrigin
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.declarations.IrProperty
import org.jetbrains.kotlin.ir.declarations.IrValueParameter
import org.jetbrains.kotlin.ir.expressions.IrBlockBody
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.types.*
import org.jetbrains.kotlin.ir.util.*
import org.jetbrains.kotlin.ir.visitors.IrElementTransformerVoid

class BackInTimeIrTransformer(
    private val pluginContext: IrPluginContext,
) : IrElementTransformerVoid() {
    override fun visitFunction(declaration: IrFunction): IrStatement {
        val ownerClass = declaration.parentClassOrNull ?: return super.visitFunction(declaration)
        if (ownerClass.superTypes.none { it.classFqName == BackInTimeConsts.debuggableStateHolderManipulatorFqName }) return super.visitFunction(declaration)
        if (declaration.name != BackInTimeConsts.forceSetPropertyValueForBackInDebugMethodName) return super.visitFunction(declaration)
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
                branches = ownerClass.properties.map { property ->
                    irBranch(
                        condition = irEquals(irGet(propertyName), irString(property.name.asString())),
                        result = generateSetForProperty(declaration, property, value)
                    )
                }.toList() + irElseBranch(irBlock {}),
            )
        }
        return super.visitFunction(declaration)
    }

    override fun visitProperty(declaration: IrProperty): IrStatement {
        val ownerClass = declaration.parentClassOrNull ?: return super.visitProperty(declaration)
        if (ownerClass.superTypes.none { it.classFqName == BackInTimeConsts.debuggableStateHolderManipulatorFqName }) return super.visitProperty(declaration)
        val backingField = declaration.backingField ?: return super.visitProperty(declaration)
        val backingFieldType = backingField.type
        val backingFieldClass = backingField.type.classOrNull ?: return super.visitProperty(declaration)

        if (backingFieldType.isKotlinPrimitiveType() || backingFieldType.isPrimitiveType()) {
            val setter = declaration.setter ?: return super.visitProperty(declaration)
            val setterBody = setter.body as? IrBlockBody ?: return super.visitProperty(declaration)
            // IMPORTANT: If this line is removed, property access inside a class is not executed via generated setter.
            setter.origin = IrDeclarationOrigin.DEFINED
            setter.body = IrBlockBodyBuilder(
                context = pluginContext,
                startOffset = setter.startOffset,
                endOffset = setter.endOffset,
                scope = Scope(declaration.symbol),
            ).blockBody {
                // Throwable().stackTrace[1].methodName
                val throwable = irCall(callee = pluginContext.irBuiltIns.throwableClass.constructors.first { it.owner.valueParameters.isEmpty() })
                val stackTrace = irCall(
                    callee = pluginContext.irBuiltIns.throwableClass.functions.first { it.owner.fqNameWhenAvailable == BackInTimeConsts.getStackTraceFqName },
                ).apply {
                    dispatchReceiver = throwable
                }
                val stackTraceElement = irCall(
                    callee = pluginContext.irBuiltIns.arrayClass.getSimpleFunction("get")!!,
                ).apply {
                    dispatchReceiver = stackTrace
                    putValueArgument(0, irInt(1))
                }
                val getMethodName = irCall(
                    callee = pluginContext.referenceFunctions(callableId = BackInTimeConsts.stackTraceGetMethodNameCallableId).first(),
                ).apply {
                    dispatchReceiver = stackTraceElement
                }

                val printlnFunction = pluginContext.referenceFunctions(callableId = BackInTimeConsts.printlnCallableId).first {
                    it.owner.valueParameters.size == 1 && it.owner.valueParameters[0].type == pluginContext.irBuiltIns.anyNType
                }

                val printlnCall = irCall(
                    callee = printlnFunction,
                ).apply {
                    putValueArgument(0, irGet(declaration.setter!!.dispatchReceiverParameter!!))
                }

                +setterBody.statements
                +printlnCall
            }
            return super.visitProperty(declaration)
        }
        when (backingFieldClass.owner.fqNameWhenAvailable) {
            BackInTimeConsts.mutableLiveDataFqName -> {
                // TODO: generate code for MutableLiveData
//                val typeArguments = backingFieldType as? IrSimpleType
//                val genericTypeArgument = typeArguments?.arguments?.firstOrNull() as? IrTypeProjection ?: return irBlock { }
//                val genericType = genericTypeArgument.type.classOrNull ?: return irBlock { }
            }

            BackInTimeConsts.mutableStateFlowFqName -> {
                // TODO: generate code for MutableStateFlow
            }

            BackInTimeConsts.mutableStateFqName -> {
                // TODO: generate code for MutableState
            }
        }
        return super.visitProperty(declaration)
    }

    private fun IrBlockBodyBuilder.generateSetForProperty(declaration: IrFunction, property: IrProperty, value: IrValueParameter): IrExpression {
        val backingField = property.backingField ?: return irBlock { }
        val backingFieldType = backingField.type
        val backingFieldClass = backingField.type.classOrNull ?: return irBlock { }
        if (property.isVar) {
            return irSetField(
                receiver = irGet(declaration.dispatchReceiverParameter!!),
                field = property.backingField!!,
                value = irGet(value),
            )
        }

        when (backingFieldClass.owner.fqNameWhenAvailable) {
            BackInTimeConsts.mutableLiveDataFqName -> {
                val typeArguments = backingFieldType as? IrSimpleType
                val genericTypeArgument = typeArguments?.arguments?.firstOrNull() as? IrTypeProjection ?: return irBlock { }
                val genericType = genericTypeArgument.type.classOrNull ?: return irBlock { }
//                MessageCollectorHolder.messageCollector.report(
//                    CompilerMessageSeverity.ERROR,
//                    "LIVEDATA!!" + genericTypeArgument.type.classFqName
//                )
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
