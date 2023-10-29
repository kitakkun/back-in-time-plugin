package com.github.kitakkun.back_in_time.backend

import com.github.kitakkun.back_in_time.annotations.DebuggableStateHolder
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.builders.*
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.util.hasAnnotation
import org.jetbrains.kotlin.ir.util.parentClassOrNull
import org.jetbrains.kotlin.ir.util.properties
import org.jetbrains.kotlin.ir.visitors.IrElementTransformerVoid
import org.jetbrains.kotlin.name.FqName

class BackInTimeIrTransformer(
    private val pluginContext: IrPluginContext,
) : IrElementTransformerVoid() {
    override fun visitFunction(declaration: IrFunction): IrStatement {
        val ownerClass = declaration.parentClassOrNull ?: return super.visitFunction(declaration)
        if (!ownerClass.hasAnnotation(FqName(DebuggableStateHolder::class.java.name))) return super.visitFunction(declaration)
        if (declaration.name.asString() != "forceSetParameterForBackInTimeDebug") return super.visitFunction(declaration)
        declaration.body = IrBlockBodyBuilder(
            context = pluginContext,
            startOffset = declaration.startOffset,
            endOffset = declaration.endOffset,
            scope = Scope(declaration.symbol),
        ).blockBody {
            val paramKey = declaration.valueParameters[0]
            val value = declaration.valueParameters[1]
            +irWhen(
                type = irUnit().type,
                branches = ownerClass.properties.filter { it.isVar }.map { property ->
                    irBranch(
                        condition = irEquals(irGet(paramKey), irString(property.name.asString())),
                        result = irSetField(
                            receiver = irGet(declaration.dispatchReceiverParameter!!),
                            field = property.backingField!!,
                            value = irGet(value),
                        )
                    )
                }.toList() + irElseBranch(irBlock {}),
            )
        }
        return super.visitFunction(declaration)
    }
}
