package com.github.kitakkun.backintime.backend

import com.github.kitakkun.backintime.BackInTimeAnnotations
import com.github.kitakkun.backintime.BackInTimeConsts
import com.github.kitakkun.backintime.MessageCollectorHolder
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.UNDEFINED_OFFSET
import org.jetbrains.kotlin.ir.backend.js.utils.valueArguments
import org.jetbrains.kotlin.ir.builders.*
import org.jetbrains.kotlin.ir.declarations.*
import org.jetbrains.kotlin.ir.declarations.impl.IrVariableImpl
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.symbols.impl.IrVariableSymbolImpl
import org.jetbrains.kotlin.ir.types.IrSimpleType
import org.jetbrains.kotlin.ir.types.classFqName
import org.jetbrains.kotlin.ir.types.defaultType
import org.jetbrains.kotlin.ir.util.*
import org.jetbrains.kotlin.ir.visitors.IrElementTransformerVoid
import org.jetbrains.kotlin.name.Name

class BackInTimeIrValueChangeNotifyCodeGenerationExtension(
    private val pluginContext: IrPluginContext,
) : IrElementTransformerVoid() {
    override fun visitSimpleFunction(declaration: IrSimpleFunction): IrStatement {
        val ownerClass = declaration.parentClassOrNull ?: return super.visitSimpleFunction(declaration)
        if (!ownerClass.hasAnnotation(BackInTimeAnnotations.debuggableStateHolderAnnotationFqName)) return super.visitSimpleFunction(declaration)

        declaration.body = IrBlockBodyBuilder(
            context = pluginContext,
            scope = Scope(declaration.symbol),
            startOffset = declaration.startOffset,
            endOffset = declaration.endOffset,
        ).blockBody {
            val timestampVariable = generateTimestampVariable(declaration)
            +timestampVariable

            declaration.body?.statements?.forEach { statement ->
                +statement

                if (statement !is IrCall) return@forEach

                MessageCollectorHolder.reportWarning(
                    "statement: ${statement.render()}",
                )

                if (statement.symbol.owner.isSetter) {
                    val callingFunction = statement.symbol.owner
                    val property = callingFunction.correspondingPropertySymbol?.owner
                    val propertyOwner = declaration.dispatchReceiverParameter
                    val valueArgument = statement.valueArguments.first()
                    +generateNotifyValueChangedCall(
                        holderInstance = propertyOwner ?: return@forEach,
                        propertyName = property?.name?.asString() ?: return@forEach,
                        value = valueArgument ?: return@forEach,
                        valueTypeQualifiedName = valueArgument.type.classFqName?.asString() ?: return@forEach,
                    )
                } else {
//                    val property = statement.getDispatchReceiverAsProperty()
//                    val valueType = property?.getGenericTypes()
//                    MessageCollectorHolder.reportWarning(
//                        "valueType: ${valueType?.first()?.classFqName}",
//                    )
//                    when (valueType?.first()?.classFqName) {
//                        BackInTimeConsts.mutableLiveDataFqName -> {
////                                +generateLoggingCode()
//                        }
//
//                        else -> {
////                                +generateLoggingCode()
//                        }
//                    }
                }
            }
//
        }

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
