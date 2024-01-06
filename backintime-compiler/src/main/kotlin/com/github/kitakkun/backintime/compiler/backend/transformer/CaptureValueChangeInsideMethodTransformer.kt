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
import org.jetbrains.kotlin.ir.util.hasAnnotation
import org.jetbrains.kotlin.ir.util.parentClassOrNull
import org.jetbrains.kotlin.ir.visitors.IrElementTransformerVoid
import org.jetbrains.kotlin.ir.visitors.transformChildrenVoid

context(BackInTimePluginContext)
class CaptureValueChangeInsideMethodTransformer : IrElementTransformerVoid() {
    private fun shouldBeTransformed(declaration: IrSimpleFunction): Boolean {
        return declaration.parentClassOrNull?.hasAnnotation(BackInTimeAnnotations.debuggableStateHolderAnnotationFqName) ?: return false
    }

    override fun visitSimpleFunction(declaration: IrSimpleFunction): IrStatement {
        if (!shouldBeTransformed(declaration)) return declaration

        val parentClassSymbol = declaration.parentClassOrNull?.symbol ?: return declaration
        val parentClassDispatchReceiver = declaration.dispatchReceiverParameter ?: return declaration

        with(declaration.irBlockBodyBuilder()) {
            val uuidVariable = generateUUIDVariable() ?: return declaration

            val notifyMethodCallFunctionCall = irCall(backInTimeNotifyMethodCallFunction).apply {
                dispatchReceiver = irGetObject(backInTimeServiceClassSymbol)
                putValueArgument(0, irGet(parentClassDispatchReceiver))
                putValueArgument(1, irString(declaration.name.asString()))
                putValueArgument(2, irGet(uuidVariable))
            }

            (declaration.body as? IrBlockBody)?.statements?.addAll(0, listOf(uuidVariable, notifyMethodCallFunctionCall))

            declaration.transformChildrenVoid(
                CaptureValueChangeTransformer(
                    parentClassSymbol = parentClassSymbol,
                    classDispatchReceiverParameter = parentClassDispatchReceiver,
                    uuidVariable = uuidVariable,
                )
            )
        }

        return declaration
    }
}