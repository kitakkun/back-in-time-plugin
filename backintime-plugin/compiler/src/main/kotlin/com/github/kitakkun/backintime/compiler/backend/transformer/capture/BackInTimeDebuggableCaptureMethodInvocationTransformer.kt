package com.github.kitakkun.backintime.compiler.backend.transformer.capture

import com.github.kitakkun.backintime.compiler.backend.BackInTimePluginContext
import com.github.kitakkun.backintime.compiler.backend.utils.generateUUIDVariable
import com.github.kitakkun.backintime.compiler.backend.utils.isBackInTimeDebuggable
import com.github.kitakkun.backintime.compiler.backend.utils.isBackInTimeGenerated
import org.jetbrains.kotlin.backend.common.lower.createIrBuilder
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.builders.irCall
import org.jetbrains.kotlin.ir.builders.irGet
import org.jetbrains.kotlin.ir.builders.irString
import org.jetbrains.kotlin.ir.declarations.IrSimpleFunction
import org.jetbrains.kotlin.ir.expressions.IrBlockBody
import org.jetbrains.kotlin.ir.util.kotlinFqName
import org.jetbrains.kotlin.ir.util.isPropertyAccessor
import org.jetbrains.kotlin.ir.util.parentClassOrNull
import org.jetbrains.kotlin.ir.visitors.IrElementTransformerVoid
import org.jetbrains.kotlin.ir.visitors.transformChildrenVoid

context(BackInTimePluginContext)
class BackInTimeDebuggableCaptureMethodInvocationTransformer : IrElementTransformerVoid() {
    override fun visitSimpleFunction(declaration: IrSimpleFunction): IrStatement {
        if (declaration.isBackInTimeGenerated) return declaration
        if (declaration.isPropertyAccessor) return declaration

        val parentClass = declaration.parentClassOrNull ?: return declaration
        if (!parentClass.isBackInTimeDebuggable) return declaration
        val parentClassSymbol = parentClass.symbol

        val parentClassDispatchReceiver = declaration.dispatchReceiverParameter ?: return declaration

        with(irBuiltIns.createIrBuilder(declaration.symbol)) {
            val uuidVariable = generateUUIDVariable()

            val notifyMethodCallFunctionCall = irCall(reportMethodInvocationFunctionSymbol).apply {
                putValueArgument(0, irGet(parentClassDispatchReceiver))
                putValueArgument(1, irGet(uuidVariable))
                putValueArgument(2, irString(declaration.name.asString()))
                putValueArgument(3, irString(parentClassSymbol.owner.kotlinFqName.asString()))
            }

            (declaration.body as? IrBlockBody)?.statements?.addAll(0, listOf(uuidVariable, notifyMethodCallFunctionCall))

            declaration.transformChildrenVoid(
                CaptureValueChangeTransformer(
                    parentClassSymbol = parentClass.symbol,
                    classDispatchReceiverParameter = parentClassDispatchReceiver,
                    uuidVariable = uuidVariable,
                ),
            )
        }

        return declaration
    }
}
