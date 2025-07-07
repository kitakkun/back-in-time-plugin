package com.kitakkun.backintime.compiler.backend.transformer.implement

import com.kitakkun.backintime.compiler.backend.BackInTimePluginContext
import com.kitakkun.backintime.compiler.backend.utils.putRegularArgument
import com.kitakkun.backintime.compiler.common.BackInTimeAnnotations
import org.jetbrains.kotlin.backend.common.lower.createIrBuilder
import org.jetbrains.kotlin.backend.jvm.ir.getIntConstArgument
import org.jetbrains.kotlin.backend.jvm.ir.getStringConstArgument
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.builders.irCall
import org.jetbrains.kotlin.ir.builders.irInt
import org.jetbrains.kotlin.ir.builders.irString
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.expressions.IrBlockBody
import org.jetbrains.kotlin.ir.util.getAnnotation
import org.jetbrains.kotlin.ir.util.primaryConstructor
import org.jetbrains.kotlin.ir.visitors.IrElementTransformerVoid

class BackInTimeEntryPointTransformer(
    private val irContext: BackInTimePluginContext,
) : IrElementTransformerVoid() {
    override fun visitFunction(declaration: IrFunction): IrStatement {
        val annotation = declaration.getAnnotation(BackInTimeAnnotations.backInTimeEntryPointAnnotationFaName) ?: return super.visitFunction(declaration)
        val host = annotation.getStringConstArgument(0)
        val port = annotation.getIntConstArgument(1)

        insertStartServiceCallToFunction(declaration, host, port)

        return super.visitFunction(declaration)
    }

    override fun visitClass(declaration: IrClass): IrStatement {
        val annotation = declaration.getAnnotation(BackInTimeAnnotations.backInTimeEntryPointAnnotationFaName) ?: return super.visitClass(declaration)
        val host = annotation.getStringConstArgument(0)
        val port = annotation.getIntConstArgument(1)

        val constructor = declaration.primaryConstructor ?: return super.visitClass(declaration)

        insertStartServiceCallToFunction(constructor, host, port)

        return super.visitClass(declaration)
    }

    private fun insertStartServiceCallToFunction(
        declaration: IrFunction,
        host: String,
        port: Int,
    ) {
        val irBuilder = irContext.irBuiltIns.createIrBuilder(declaration.symbol)
        val startServiceCall = with(irBuilder) {
            irCall(irContext.backInTimeEntryPointRegisterFunctionSymbol).apply {
                putRegularArgument(0, irString(host))
                putRegularArgument(1, irInt(port))
            }
        }
        (declaration.body as? IrBlockBody)?.statements?.add(0, startServiceCall)
    }
}