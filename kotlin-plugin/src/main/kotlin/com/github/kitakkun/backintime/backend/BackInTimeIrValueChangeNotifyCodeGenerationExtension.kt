package com.github.kitakkun.backintime.backend

import com.github.kitakkun.backintime.BackInTimeAnnotations
import com.github.kitakkun.backintime.BackInTimeConsts
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.backend.js.lower.serialization.ir.JsManglerIr.signatureString
import org.jetbrains.kotlin.ir.builders.*
import org.jetbrains.kotlin.ir.declarations.IrDeclarationOrigin
import org.jetbrains.kotlin.ir.declarations.IrSimpleFunction
import org.jetbrains.kotlin.ir.deepCopyWithVariables
import org.jetbrains.kotlin.ir.expressions.IrBlockBody
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.IrGetValue
import org.jetbrains.kotlin.ir.types.classFqName
import org.jetbrains.kotlin.ir.types.classOrNull
import org.jetbrains.kotlin.ir.util.*
import org.jetbrains.kotlin.ir.visitors.IrElementTransformerVoid
import org.jetbrains.kotlin.ir.visitors.transformChildrenVoid
import org.jetbrains.kotlin.name.CallableId

class BackInTimeIrValueChangeNotifyCodeGenerationExtension(
    private val pluginContext: IrPluginContext,
    private val capturedCallableIds: List<CallableId>,
    private val valueGetterCallableIds: List<CallableId>,
) : IrElementTransformerVoid() {
    override fun visitSimpleFunction(declaration: IrSimpleFunction): IrStatement {
        val ownerClass = declaration.parentClassOrNull ?: return super.visitSimpleFunction(declaration)
        if (!ownerClass.hasAnnotation(BackInTimeAnnotations.debuggableStateHolderAnnotationFqName)) return super.visitSimpleFunction(declaration)
        if (!ownerClass.functions.contains(declaration)) return super.visitSimpleFunction(declaration)

        val irBuilder = IrBlockBuilder(
            context = pluginContext,
            scope = Scope(declaration.symbol),
            startOffset = declaration.startOffset,
            endOffset = declaration.endOffset,
        )

        val methodCallInfo = pluginContext.referenceClass(BackInTimeConsts.methodCallInfoClassId) ?: return super.visitSimpleFunction(declaration)
        val constructor = methodCallInfo.constructors.firstOrNull() ?: return super.visitSimpleFunction(declaration)

        val callInfo = with(irBuilder) {
            irTemporary(
                irCallConstructor(constructor, emptyList()).apply {
                    putValueArgument(0, irString(declaration.signatureString(false)))
                },
                origin = IrDeclarationOrigin.DEFINED,
            )
        }

        (declaration.body as? IrBlockBody)?.statements?.add(0, callInfo)

        declaration.transformChildrenVoid(object : IrElementTransformerVoid() {
            override fun visitCall(expression: IrCall): IrExpression {
                val irBlockBuilder = IrBlockBuilder(
                    context = pluginContext,
                    scope = Scope(expression.symbol),
                    startOffset = expression.startOffset,
                    endOffset = expression.endOffset,
                )
                val callingFunction = expression.symbol.owner
                val dispatchReceiverForExpression = expression.dispatchReceiver
                // ピュアなvalueセッター
                // ex) this.variable = 1
                if (callingFunction.isSetter && dispatchReceiverForExpression is IrGetValue) {
                    val property = callingFunction.correspondingPropertySymbol?.owner ?: return super.visitCall(expression)
                    val propertyGetter = property.getter ?: return super.visitCall(expression)
                    return irBlockBuilder.irComposite {
                        +super.visitCall(expression)
                        with(pluginContext) {
                            irNotifyValueChangeCall(
                                parentInstance = declaration.dispatchReceiverParameter ?: return@irComposite,
                                propertyName = property.name.asString(),
                                value = irCall(propertyGetter).apply {
                                    this.dispatchReceiver = dispatchReceiverForExpression.deepCopyWithVariables()
                                },
                                propertyTypeClassFqName = property.backingField?.type?.classFqName?.asString() ?: return@irComposite,
                                callInfo = callInfo,
                            )?.let { +it }
                        }
                    }
                }
                // 他のクラスの内側に値を持っている場合
                // ex) liveData.value = 1
                if (
                    (callingFunction.fqNameWhenAvailable in capturedCallableIds.map { it.asSingleFqName() })
                    && (callingFunction.parentClassOrNull?.fqNameWhenAvailable in valueGetterCallableIds.map { it.className })
                    && dispatchReceiverForExpression is IrCall
                ) {
                    val property = dispatchReceiverForExpression.symbol.owner.correspondingPropertySymbol?.owner ?: return super.visitCall(expression)
                    val propertyClass = property.backingField?.type?.classOrNull?.owner ?: return super.visitCall(expression)
                    val valueGetterCallableId = valueGetterCallableIds.firstOrNull { it.className == callingFunction.parentClassOrNull?.fqNameWhenAvailable } ?: return super.visitCall(expression)

                    val propertyGetterPattern = Regex("<get-(.*?)>")
                    val matchResult = propertyGetterPattern.find(valueGetterCallableId.callableName.asString())

                    val valueGetter = if (matchResult != null) {
                        val fieldName = matchResult.groupValues[1]
                        propertyClass.getPropertyGetterRecursively(fieldName)
                    } else {
                        val functionName = valueGetterCallableId.callableName.asString()
                        propertyClass.getSimpleFunctionRecursively(functionName)
                    } ?: return super.visitCall(expression)

                    return irBlockBuilder.irComposite {
                        +super.visitCall(expression)
                        with(pluginContext) {
                            irNotifyValueChangeCall(
                                parentInstance = declaration.dispatchReceiverParameter ?: return@irComposite,
                                propertyName = property.name.asString(),
                                value = irCall(valueGetter).apply { this.dispatchReceiver = dispatchReceiverForExpression.deepCopyWithVariables() },
                                propertyTypeClassFqName = property.getGenericTypes().first().classFqName?.asString() ?: return@irComposite,
                                callInfo = callInfo,
                            )?.let { +it }
                        }
                    }
                }
                return super.visitCall(expression)
            }

        })
        return super.visitSimpleFunction(declaration)
    }
}
