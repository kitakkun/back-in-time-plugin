package com.kitakkun.backintime.compiler.backend.transformer.capture

import com.kitakkun.backintime.compiler.backend.BackInTimePluginContext
import com.kitakkun.backintime.compiler.backend.utils.isBackInTimeDebuggable
import com.kitakkun.backintime.compiler.backend.utils.putRegularArgument
import com.kitakkun.backintime.compiler.backend.utils.receiver
import com.kitakkun.backintime.compiler.common.BackInTimeConsts
import org.jetbrains.kotlin.backend.common.lower.createIrBuilder
import org.jetbrains.kotlin.ir.builders.irCall
import org.jetbrains.kotlin.ir.builders.irComposite
import org.jetbrains.kotlin.ir.builders.irGetField
import org.jetbrains.kotlin.ir.builders.irIfThen
import org.jetbrains.kotlin.ir.builders.irNotEquals
import org.jetbrains.kotlin.ir.builders.irString
import org.jetbrains.kotlin.ir.builders.irTrue
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.util.getSimpleFunction
import org.jetbrains.kotlin.ir.util.isGetter
import org.jetbrains.kotlin.ir.util.isSetter
import org.jetbrains.kotlin.ir.util.parentClassOrNull
import org.jetbrains.kotlin.ir.util.properties
import org.jetbrains.kotlin.ir.visitors.IrElementTransformerVoid

class BackInTimeDebuggableCaptureLazyDebuggablePropertyAccessTransformer(
    private val irContext: BackInTimePluginContext,
) : IrElementTransformerVoid() {
    override fun visitCall(expression: IrCall): IrExpression {
        expression.transformChildrenVoid()
        val transformedExpression = expression.captureLazyDebuggablePropertyAccess() ?: expression
        return transformedExpression
    }

    private fun IrCall.captureLazyDebuggablePropertyAccess(): IrExpression? {
        val callingFunction = this.symbol.owner
        if (!callingFunction.isGetter && !callingFunction.isSetter) return null

        val property = callingFunction.correspondingPropertySymbol?.owner ?: return null
        val propertyOwnerClass = property.parentClassOrNull ?: return null
        if (!propertyOwnerClass.isBackInTimeDebuggable) return null

        val receiver = this.receiver ?: return null

        val initializedMapProperty = propertyOwnerClass.properties.first { it.name == BackInTimeConsts.backInTimeInitializedPropertyMapName }

        if (!property.isBackInTimeDebuggable || !property.isDelegated) return null

        with(irContext.irBuiltIns.createIrBuilder(this.symbol)) {
            val condition = irNotEquals(
                arg1 = irTrue(),
                arg2 = irCall(irContext.irBuiltIns.mapClass.getSimpleFunction("get")!!).apply {
                    dispatchReceiver = irGetField(receiver, initializedMapProperty.backingField!!)
                    putRegularArgument(0, irString(property.name.asString()))
                },
            )
            val thenPart = irComposite {
                +irCall(irContext.reportNewRelationshipFunctionSymbol).apply {
                    putRegularArgument(0, receiver)
                    putRegularArgument(1, irCall(property.getter!!).apply { dispatchReceiver = receiver })
                }
                +irCall(irContext.irBuiltIns.mutableMapClass.getSimpleFunction("put")!!).apply {
                    dispatchReceiver = irGetField(receiver, initializedMapProperty.backingField!!)
                    putRegularArgument(0, irString(property.name.asString()))
                    putRegularArgument(1, irTrue())
                }
            }
            return irComposite {
                when {
                    callingFunction.isSetter -> +thenPart
                    callingFunction.isGetter -> +irIfThen(type = irContext.irBuiltIns.unitType, condition = condition, thenPart = thenPart)
                }
                +this@captureLazyDebuggablePropertyAccess
            }
        }
    }
}
