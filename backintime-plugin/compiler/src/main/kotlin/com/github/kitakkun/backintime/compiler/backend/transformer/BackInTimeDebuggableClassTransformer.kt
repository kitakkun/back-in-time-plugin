package com.github.kitakkun.backintime.compiler.backend.transformer

import com.github.kitakkun.backintime.compiler.backend.BackInTimePluginContext
import com.github.kitakkun.backintime.compiler.backend.utils.generateUUIDVariable
import com.github.kitakkun.backintime.compiler.backend.utils.hasBackInTimeDebuggableAsInterface
import com.github.kitakkun.backintime.compiler.backend.utils.isBackInTimeDebuggable
import com.github.kitakkun.backintime.compiler.backend.utils.isBackInTimeGenerated
import com.github.kitakkun.backintime.compiler.consts.BackInTimeConsts
import org.jetbrains.kotlin.backend.common.lower.createIrBuilder
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.builders.irCall
import org.jetbrains.kotlin.ir.builders.irGet
import org.jetbrains.kotlin.ir.builders.irGetField
import org.jetbrains.kotlin.ir.builders.irString
import org.jetbrains.kotlin.ir.declarations.IrConstructor
import org.jetbrains.kotlin.ir.declarations.IrProperty
import org.jetbrains.kotlin.ir.declarations.IrSimpleFunction
import org.jetbrains.kotlin.ir.expressions.IrBlockBody
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.util.parentClassOrNull
import org.jetbrains.kotlin.ir.util.properties
import org.jetbrains.kotlin.ir.visitors.IrElementTransformerVoid
import org.jetbrains.kotlin.ir.visitors.transformChildrenVoid

context(BackInTimePluginContext)
class BackInTimeDebuggableClassTransformer : IrElementTransformerVoid() {
    override fun visitConstructor(declaration: IrConstructor): IrStatement {
        val parentClass = declaration.parentClassOrNull ?: return declaration
        if (!parentClass.hasBackInTimeDebuggableAsInterface) return declaration

        with(irBuiltIns.createIrBuilder(declaration.symbol)) {
            val registerCall = generateRegisterCall(parentClass)
            val propertyRelationshipResolveCalls = parentClass.properties
                .filter { it.isBackInTimeDebuggable && !it.isDelegated && !it.isVar }
                .mapNotNull { property ->
                    val backingField = property.backingField ?: return@mapNotNull null
                    val parentReceiver = parentClass.thisReceiver ?: return@mapNotNull null

                    irCall(reportNewRelationshipFunctionSymbol).apply {
                        putValueArgument(0, irGet(parentReceiver))
                        putValueArgument(1, irGetField(receiver = irGet(parentReceiver), field = backingField))
                    }
                }
            with(declaration.body as IrBlockBody) {
                statements.add(registerCall)
                statements.addAll(propertyRelationshipResolveCalls)
            }
        }

        return declaration
    }

    override fun visitSimpleFunction(declaration: IrSimpleFunction): IrStatement {
        declaration.transformChildrenVoid()

        val parentClass = declaration.parentClassOrNull ?: return declaration

        if (declaration.isBackInTimeGenerated) {
            declaration.addBackInTimeDebuggableMethodBody()
        } else if (parentClass.isBackInTimeDebuggable) {
            val parentClassSymbol = declaration.parentClassOrNull?.symbol ?: return declaration
            val parentClassDispatchReceiver = declaration.dispatchReceiverParameter ?: return declaration

            with(irBuiltIns.createIrBuilder(declaration.symbol)) {
                val uuidVariable = generateUUIDVariable() ?: return declaration

                val notifyMethodCallFunctionCall = irCall(reportMethodInvocationFunctionSymbol).apply {
                    putValueArgument(0, irGet(parentClassDispatchReceiver))
                    putValueArgument(1, irGet(uuidVariable))
                    putValueArgument(2, irString(declaration.name.asString()))
                }

                (declaration.body as? IrBlockBody)?.statements?.addAll(0, listOf(uuidVariable, notifyMethodCallFunctionCall))

                declaration.transformChildrenVoid(
                    CaptureValueChangeTransformer(
                        parentClassSymbol = parentClassSymbol,
                        classDispatchReceiverParameter = parentClassDispatchReceiver,
                        uuidVariable = uuidVariable,
                    ),
                )
            }
        }
        return declaration
    }

    override fun visitProperty(declaration: IrProperty): IrStatement {
        if (!declaration.isBackInTimeGenerated) return declaration

        when (declaration.name) {
            BackInTimeConsts.backInTimeInstanceUUIDName -> declaration.addBackingFieldOfBackInTimeUUID()
            BackInTimeConsts.backInTimeInitializedPropertyMapName -> declaration.addBackingFieldOfBackInTimeInitializedPropertyMap()
        }

        return declaration
    }

    override fun visitCall(expression: IrCall): IrExpression {
        expression.transformChildrenVoid()
        val transformedExpression = expression.captureLazyDebuggablePropertyAccess() ?: expression
        return transformedExpression
    }
}
