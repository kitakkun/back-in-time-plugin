package com.github.kitakkun.backintime.compiler.backend.transformer

import com.github.kitakkun.backintime.compiler.BackInTimeAnnotations
import com.github.kitakkun.backintime.compiler.backend.BackInTimePluginContext
import com.github.kitakkun.backintime.compiler.backend.utils.generateUUIDVariable
import com.github.kitakkun.backintime.compiler.backend.utils.irBlockBodyBuilder
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.builders.irCall
import org.jetbrains.kotlin.ir.builders.irGet
import org.jetbrains.kotlin.ir.builders.irGetObject
import org.jetbrains.kotlin.ir.builders.irString
import org.jetbrains.kotlin.ir.declarations.IrSimpleFunction
import org.jetbrains.kotlin.ir.expressions.IrBlockBody
import org.jetbrains.kotlin.ir.util.functions
import org.jetbrains.kotlin.ir.util.hasAnnotation
import org.jetbrains.kotlin.ir.util.parentClassOrNull
import org.jetbrains.kotlin.ir.visitors.IrElementTransformerVoid
import org.jetbrains.kotlin.ir.visitors.transformChildrenVoid

context(BackInTimePluginContext)
class ValueCaptureCallGenerationTransformer : IrElementTransformerVoid() {
    override fun visitSimpleFunction(declaration: IrSimpleFunction): IrStatement {
        val ownerClass = declaration.parentClassOrNull ?: return super.visitSimpleFunction(declaration)
        if (!ownerClass.hasAnnotation(BackInTimeAnnotations.debuggableStateHolderAnnotationFqName)) return super.visitSimpleFunction(declaration)
        if (!ownerClass.functions.contains(declaration)) return super.visitSimpleFunction(declaration)

        val irBuilder = declaration.irBlockBodyBuilder()
        val parentClassDispatchReceiver = declaration.dispatchReceiverParameter ?: return super.visitSimpleFunction(declaration)

        val uuidVariable = with(pluginContext) { irBuilder.generateUUIDVariable() } ?: return super.visitSimpleFunction(declaration)

        val notifyMethodCallFunctionCall = with(irBuilder) {
            irCall(backInTimeNotifyMethodCallFunction).apply {
                dispatchReceiver = irGetObject(backInTimeServiceClassSymbol)
                putValueArgument(0, irGet(declaration.dispatchReceiverParameter!!))
                putValueArgument(1, irString(declaration.name.asString()))
                putValueArgument(2, irGet(uuidVariable))
            }
        }

        (declaration.body as? IrBlockBody)?.statements?.addAll(0, listOf(uuidVariable, notifyMethodCallFunctionCall))

        declaration.transformChildrenVoid(
            InsertValueCaptureAfterCallTransformer(
                parentClassSymbol = ownerClass.symbol,
                classDispatchReceiverParameter = parentClassDispatchReceiver,
                uuidVariable = uuidVariable,
            )
        )
        return super.visitSimpleFunction(declaration)
    }
}
