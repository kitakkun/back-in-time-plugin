package com.github.kitakkun.backintime.backend

import com.github.kitakkun.backintime.BackInTimeAnnotations
import com.github.kitakkun.backintime.BackInTimeConsts
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.jvm.fieldByName
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.UNDEFINED_OFFSET
import org.jetbrains.kotlin.ir.builders.*
import org.jetbrains.kotlin.ir.declarations.*
import org.jetbrains.kotlin.ir.declarations.impl.IrVariableImpl
import org.jetbrains.kotlin.ir.deepCopyWithVariables
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.IrGetValue
import org.jetbrains.kotlin.ir.symbols.impl.IrVariableSymbolImpl
import org.jetbrains.kotlin.ir.types.IrSimpleType
import org.jetbrains.kotlin.ir.types.classFqName
import org.jetbrains.kotlin.ir.types.classOrNull
import org.jetbrains.kotlin.ir.types.defaultType
import org.jetbrains.kotlin.ir.util.*
import org.jetbrains.kotlin.ir.visitors.IrElementTransformerVoid
import org.jetbrains.kotlin.ir.visitors.transformChildrenVoid
import org.jetbrains.kotlin.name.Name

class BackInTimeIrValueChangeNotifyCodeGenerationExtension(
    private val pluginContext: IrPluginContext,
) : IrElementTransformerVoid() {
    override fun visitSimpleFunction(declaration: IrSimpleFunction): IrStatement {
        val ownerClass = declaration.parentClassOrNull ?: return super.visitSimpleFunction(declaration)
        if (!ownerClass.hasAnnotation(BackInTimeAnnotations.debuggableStateHolderAnnotationFqName)) return super.visitSimpleFunction(declaration)
        if (!ownerClass.functions.contains(declaration)) return super.visitSimpleFunction(declaration)

        declaration.transformChildrenVoid(object : IrElementTransformerVoid() {
            override fun visitCall(expression: IrCall): IrExpression {
                val irBlockBuilder = IrBlockBuilder(
                    context = pluginContext,
                    scope = Scope(expression.symbol),
                    startOffset = expression.startOffset,
                    endOffset = expression.endOffset,
                )
                val function = expression.symbol.owner
                if (function.isSetter) {
                    val backInTimeDebugServiceClass = pluginContext.referenceClass(BackInTimeConsts.backInTimeDebugServiceClassId) ?: return super.visitCall(expression)
                    val notifyValueChangeFunction = backInTimeDebugServiceClass.getSimpleFunction(BackInTimeConsts.notifyPropertyChanged) ?: return super.visitCall(expression)

                    val fieldInstance = expression.dispatchReceiver
                    if (fieldInstance is IrGetValue) {
                        // ex) this.variable = 1
                        // fieldInstance should be "this"
                        val property = function.correspondingPropertySymbol?.owner ?: return super.visitCall(expression)
                        val propertyGetter = property.getter ?: return super.visitCall(expression)
                        return irBlockBuilder.irComposite {
                            +super.visitCall(expression)
                            +irCall(notifyValueChangeFunction).apply {
                                dispatchReceiver = irGetObject(backInTimeDebugServiceClass)
                                putValueArgument(0, irGet(declaration.dispatchReceiverParameter!!))
                                putValueArgument(1, irString(property.name.asString()))
                                putValueArgument(2, irCall(propertyGetter).apply {
                                    dispatchReceiver = fieldInstance.deepCopyWithVariables()
                                })
                                putValueArgument(3, irString(property.backingField!!.type.classFqName?.asString() ?: ""))
                            }
                        }
                    } else if (fieldInstance is IrCall) {
                        // ex) liveData.value = 1
                        // fieldInstance should be "liveData"
                        val property = fieldInstance.symbol.owner.correspondingPropertySymbol?.owner ?: return super.visitCall(expression)
                        // FIXME: should be handle properly depending on the type of property
                        val valueGetter = fieldInstance.type.classOrNull?.getPropertyGetter("value") ?: return super.visitCall(expression)
                        val printlnFunction = pluginContext.referenceFunctions(BackInTimeConsts.printlnCallableId).first {
                            it.owner.valueParameters.size == 1 && it.owner.valueParameters.first().type == pluginContext.irBuiltIns.anyNType
                        }
                        return irBlockBuilder.irComposite {
                            // FIXME: should be passed to notifyPropertyChanged
                            val timestampVariable = generateTimestampVariable(declaration)
                            +timestampVariable

                            +super.visitCall(expression)
                            +irCall(notifyValueChangeFunction).apply {
                                dispatchReceiver = irGetObject(backInTimeDebugServiceClass)
                                putValueArgument(0, irGet(declaration.dispatchReceiverParameter!!))
                                putValueArgument(1, irString(property.name.asString()))
                                putValueArgument(2, irCall(valueGetter).apply {
                                    dispatchReceiver = fieldInstance.deepCopyWithVariables()
                                })
                                putValueArgument(3, irString(property.getGenericTypes().first().classFqName?.asString() ?: ""))
                            }
                            // TODO: REMOVE THIS
                            //  (just for debugging)
                            +irCall(printlnFunction).apply {
                                putValueArgument(0,
                                    irCall(valueGetter).apply {
                                        dispatchReceiver = fieldInstance.deepCopyWithVariables()
                                    }
                                )
                            }
                        }
                    }
                }
                return super.visitCall(expression)
            }

        })
        return super.visitSimpleFunction(declaration)
    }

    private fun IrBuilderWithScope.generateNotifyValueChangedCall(
        holderInstance: IrValueParameter,
        propertyName: String,
        value: IrExpression,
        valueTypeQualifiedName: String,
    ): IrCall {
        val backInTimeServiceClass = pluginContext.referenceClass(BackInTimeConsts.backInTimeDebugServiceClassId)!!
        val backInTimeServiceClassNotifyValueChangedMethod = backInTimeServiceClass.functions.first { it.owner.name.asString() == BackInTimeConsts.notifyPropertyChanged }
        return irCall(backInTimeServiceClassNotifyValueChangedMethod).apply {
            dispatchReceiver = irGetObjectValue(backInTimeServiceClass.defaultType, backInTimeServiceClass)
            putValueArgument(0, irGet(holderInstance))
            putValueArgument(1, irString(propertyName))
            putValueArgument(2, value)
            putValueArgument(3, irString(valueTypeQualifiedName))
        }
    }

    private fun IrBuilderWithScope.generateValueGetCall(
        ownerClassDispatchReceiver: IrValueParameter,
        propertyBackingField: IrField,
        property: IrProperty,
    ): IrCall? {
//        val propertyGetter = property.getter ?: return null
        val backingFieldType = property.backingField?.type
        when (backingFieldType?.classFqName) {
            BackInTimeConsts.mutableLiveDataFqName -> {
                val mutableLiveDataGetValueFunction = backingFieldType.classOrNull?.getSimpleFunction("getValue") ?: return null
                return irCall(mutableLiveDataGetValueFunction).apply {
                    dispatchReceiver = irGetField(irGet(ownerClassDispatchReceiver), propertyBackingField)
                }
            }

            BackInTimeConsts.mutableStateFqName -> {
                val mutableStateGetValueFunction = backingFieldType.classOrNull?.getSimpleFunction("getValue") ?: return null
                return irCall(mutableStateGetValueFunction).apply {
                    dispatchReceiver = irGetField(irGet(ownerClassDispatchReceiver), propertyBackingField)
                }
            }

            BackInTimeConsts.mutableStateFlowFqName -> {
                val mutableStateFlowValueGet = backingFieldType.classOrNull?.fieldByName("value") ?: return null
            }

            else -> {
                return null
            }
        }
        return null
    }


    private fun IrBuilderWithScope.generateTimestampVariable(
        variableDefinedFunction: IrDeclarationParent,
    ): IrVariable {
        val systemClass = pluginContext.referenceClass(BackInTimeConsts.systemClassId)!!
        val currentTimeMillisFunction = systemClass.getSimpleFunction("currentTimeMillis")!!

        return IrVariableImpl(
            startOffset = UNDEFINED_OFFSET,
            endOffset = UNDEFINED_OFFSET,
            symbol = IrVariableSymbolImpl(),
            type = pluginContext.irBuiltIns.longType,
            name = Name.identifier("backInTimeTimestamp"),
            isConst = false,
            isVar = false,
            isLateinit = false,
            origin = IrDeclarationOrigin.DEFINED,
        ).apply {
            initializer = irCall(currentTimeMillisFunction)
            parent = variableDefinedFunction
        }
    }

    private fun IrBuilderWithScope.generateLoggingCode(
        timestampVariable: IrVariable,
        methodName: String,
        ownerClassValue: IrValueParameter,
        propertyName: String,
        getNewValue: IrExpression,
        valueType: IrSimpleType,
    ): List<IrStatement> {
//        val backInTimeDebugServiceClass = pluginContext.referenceClass(BackInTimeConsts.backInTimeDebugServiceClassId) ?: return emptyList()
//        val notifyPropertyChangedFunction = backInTimeDebugServiceClass.getSimpleFunction("notifyPropertyChanged") ?: return emptyList()
//
//        val printlnFunction = pluginContext.referenceFunctions(BackInTimeConsts.printlnCallableId).first {
//            it.owner.valueParameters.size == 1 && it.owner.valueParameters.first().type == pluginContext.irBuiltIns.anyNType
//        }
//
//        val debugPrintCall = irCall(printlnFunction).apply {
////            putValueArgument(0, irGet(ownerClassValue))
//            putValueArgument(0, getNewValue)
//        }
//
//        return emptyList()
//        return listOf(irCall(backInTimeDebugServiceClass.getSimpleFunction("notifyPropertyChangedType")!!).apply {
//            dispatchReceiver = irGetObject(backInTimeDebugServiceClass)
//            putValueArgument(0, getNewValue)
//            putTypeArgument(0, valueType)
//        })
//
//        val functionCall = irCall(notifyPropertyChangedFunction).apply {
//            dispatchReceiver = irGetObject(backInTimeDebugServiceClass)
//            putValueArgument(0, irGet(ownerClassValue))
//            putValueArgument(1, irString(methodName))
//            putValueArgument(2, irString(propertyName))
//            putValueArgument(3, getNewValue)
//            putValueArgument(4, irString(valueType.type.classFqName?.asString() ?: "null"))
//            putValueArgument(5, irGet(timestampVariable))
//            putTypeArgument(0, valueType)
//        }
//
//        return listOf(debugPrintCall, functionCall)
        return emptyList()
    }
}
