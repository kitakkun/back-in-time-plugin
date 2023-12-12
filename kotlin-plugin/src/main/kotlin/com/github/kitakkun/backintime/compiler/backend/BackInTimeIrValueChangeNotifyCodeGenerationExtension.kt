package com.github.kitakkun.backintime.compiler.backend

import com.github.kitakkun.backintime.compiler.BackInTimeAnnotations
import com.github.kitakkun.backintime.compiler.BackInTimeConsts
import com.github.kitakkun.backintime.compiler.backend.utils.generateUUIDVariable
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.builders.IrBlockBuilder
import org.jetbrains.kotlin.ir.builders.Scope
import org.jetbrains.kotlin.ir.builders.irCall
import org.jetbrains.kotlin.ir.builders.irGet
import org.jetbrains.kotlin.ir.builders.irGetObject
import org.jetbrains.kotlin.ir.builders.irString
import org.jetbrains.kotlin.ir.declarations.IrSimpleFunction
import org.jetbrains.kotlin.ir.expressions.IrBlockBody
import org.jetbrains.kotlin.ir.util.functions
import org.jetbrains.kotlin.ir.util.getSimpleFunction
import org.jetbrains.kotlin.ir.util.hasAnnotation
import org.jetbrains.kotlin.ir.util.parentClassOrNull
import org.jetbrains.kotlin.ir.visitors.IrElementTransformerVoid
import org.jetbrains.kotlin.ir.visitors.transformChildrenVoid
import org.jetbrains.kotlin.name.CallableId

class BackInTimeIrValueChangeNotifyCodeGenerationExtension(
    private val pluginContext: IrPluginContext,
    private val valueContainerClassInfo: List<ValueContainerClassInfo>,
    // FIXME: temporary solution
    private val capturedCallableIds: Set<CallableId> = valueContainerClassInfo.flatMap { it.capturedCallableIds }.toSet(),
    private val valueGetterCallableIds: Set<CallableId> = valueContainerClassInfo.map { it.valueGetter }.toSet(),
) : IrElementTransformerVoid() {
    private val backInTimeDebugServiceClass = pluginContext.referenceClass(BackInTimeConsts.backInTimeDebugServiceClassId) ?: error("backInTimeDebugServiceClassId is not found")
    private val backInTimeNotifyMethodCallFunction = backInTimeDebugServiceClass.getSimpleFunction(BackInTimeConsts.notifyMethodCallFunctionName) ?: error("notifyMethodCall is not found")

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

        val uuidVariable = with(pluginContext) { irBuilder.generateUUIDVariable() } ?: return super.visitSimpleFunction(declaration)

        val notifyMethodCallFunctionCall = with(irBuilder) {
            irCall(backInTimeNotifyMethodCallFunction).apply {
                dispatchReceiver = irGetObject(backInTimeDebugServiceClass)
                putValueArgument(0, irGet(declaration.dispatchReceiverParameter!!))
                putValueArgument(1, irString(declaration.name.asString()))
                putValueArgument(2, irGet(uuidVariable))
            }
        }

        (declaration.body as? IrBlockBody)?.statements?.addAll(0, listOf(uuidVariable, notifyMethodCallFunctionCall))

        declaration.transformChildrenVoid(InsertValueCaptureAfterCallTransformer(pluginContext, declaration, uuidVariable, capturedCallableIds, valueGetterCallableIds))
        return super.visitSimpleFunction(declaration)
    }
}
