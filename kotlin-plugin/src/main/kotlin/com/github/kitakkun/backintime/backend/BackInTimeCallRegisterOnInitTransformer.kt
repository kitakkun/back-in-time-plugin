package com.github.kitakkun.backintime.backend

import com.github.kitakkun.backintime.BackInTimeConsts
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.builders.*
import org.jetbrains.kotlin.ir.declarations.IrConstructor
import org.jetbrains.kotlin.ir.types.classFqName
import org.jetbrains.kotlin.ir.util.getSimpleFunction
import org.jetbrains.kotlin.ir.util.parentClassOrNull
import org.jetbrains.kotlin.ir.util.statements
import org.jetbrains.kotlin.ir.visitors.IrElementTransformerVoid

class BackInTimeCallRegisterOnInitTransformer(
    private val pluginContext: IrPluginContext,
) : IrElementTransformerVoid() {
    override fun visitConstructor(declaration: IrConstructor): IrStatement {
        val parentClass = declaration.parentClassOrNull ?: return super.visitConstructor(declaration)
        if (parentClass.superTypes.none { it.classFqName == BackInTimeConsts.debuggableStateHolderManipulatorFqName }) return super.visitConstructor(declaration)

        val backInTimeDebugServiceClass = pluginContext.referenceClass(BackInTimeConsts.backInTimeDebugServiceClassId) ?: return super.visitConstructor(declaration)
        val registerFunction = backInTimeDebugServiceClass.getSimpleFunction(BackInTimeConsts.registerFunctionName) ?: return super.visitConstructor(declaration)

        declaration.body = IrBlockBodyBuilder(
            context = pluginContext,
            startOffset = declaration.startOffset,
            endOffset = declaration.endOffset,
            scope = Scope(declaration.symbol),
        ).blockBody {
            +declaration.body?.statements.orEmpty()
            +irCall(registerFunction).apply {
                dispatchReceiver = irGetObject(backInTimeDebugServiceClass)
                putValueArgument(0, irGet(parentClass.thisReceiver!!))
            }
        }

        return super.visitConstructor(declaration)
    }
}
