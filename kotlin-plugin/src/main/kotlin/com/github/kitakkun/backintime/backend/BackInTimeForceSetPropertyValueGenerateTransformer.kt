package com.github.kitakkun.backintime.backend

import com.github.kitakkun.backintime.BackInTimeConsts
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.common.lower.irThrow
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.builders.*
import org.jetbrains.kotlin.ir.declarations.*
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.types.classFqName
import org.jetbrains.kotlin.ir.types.classOrNull
import org.jetbrains.kotlin.ir.types.isNullable
import org.jetbrains.kotlin.ir.util.*
import org.jetbrains.kotlin.ir.visitors.IrElementTransformerVoid
import org.jetbrains.kotlin.name.CallableId

class BackInTimeForceSetPropertyValueGenerateTransformer(
    private val pluginContext: IrPluginContext,
    private val valueSetterCallableIds: List<CallableId>,
) : IrElementTransformerVoid() {
    private val backInTimeRuntimeException = pluginContext.referenceClass(BackInTimeConsts.backInTimeRuntimeExceptionClassId)!!
    private val nullValueNotAssignableExceptionConstructor = backInTimeRuntimeException.owner.sealedSubclasses
        .first { it.owner.classId == BackInTimeConsts.nullValueNotAssignableExceptionClassId }.constructors.first()

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
                type = pluginContext.irBuiltIns.unitType,
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
        val backingFieldClass = backingField.type.classOrNull ?: return irBlock { }

        // varのsetterならそのまま突っ込む
        if (property.isVar) {
            return if (backingField.type.isNullable()) {
                irSetField(
                    receiver = irGet(declaration.dispatchReceiverParameter!!),
                    field = backingField,
                    value = irGet(value),
                )
            } else {
                irIfNull(type = value.type, subject = irGet(value), thenPart = irBlock {
                    irThrow(irCallConstructor(nullValueNotAssignableExceptionConstructor, emptyList()).apply {
                        putValueArgument(0, irString(property.fqNameWhenAvailable?.asString() ?: "unknown"))
                        putValueArgument(1, irString(backingField.type.classFqName?.asString() ?: "unknown"))
                    })
                }, elsePart = irBlock {
                    irSetField(
                        receiver = irGet(declaration.dispatchReceiverParameter!!),
                        field = backingField,
                        value = irGet(value),
                    )
                })
            }
        }

        val parentClassAsReceiver = if (backingField.isStatic) null else irGet(declaration.dispatchReceiverParameter!!)

        val setterCallableId = valueSetterCallableIds.find { it.className == backingFieldClass.owner.fqNameWhenAvailable } ?: return irBlock {}
        val propertySetterPattern = Regex("<set-(.*?)>")
        val matchResult = propertySetterPattern.find(setterCallableId.callableName.asString())
        val valueSetter = if (matchResult != null) {
            property.backingField?.type?.classOrNull?.getPropertySetter(matchResult.groupValues[1])
        } else {
            pluginContext.referenceFunctions(setterCallableId).firstOrNull()
        } ?: return irBlock {}

        return if (backingField.type.isNullable()) {
            irCall(valueSetter).apply {
                dispatchReceiver = irGetField(receiver = parentClassAsReceiver, field = backingField)
                putValueArgument(0, irGet(value))
            }
        } else {
            irIfNull(type = pluginContext.irBuiltIns.anyNType, subject = irGet(value), thenPart = irThrow(irCallConstructor(nullValueNotAssignableExceptionConstructor, emptyList()).apply {
                putValueArgument(0, irString(property.fqNameWhenAvailable?.asString() ?: "unknown"))
                putValueArgument(1, irString(property.getGenericTypes().firstOrNull()?.classFqName?.asString() ?: "unknown"))
            }), elsePart = irBlock {
                irCall(valueSetter).apply {
                    dispatchReceiver = irGetField(receiver = parentClassAsReceiver, field = backingField)
                    putValueArgument(0, irGet(value))
                }
            })
        }
    }
}
