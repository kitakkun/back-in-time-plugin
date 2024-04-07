package com.github.kitakkun.backintime.compiler.backend.transformer

import com.github.kitakkun.backintime.compiler.backend.BackInTimePluginContext
import com.github.kitakkun.backintime.compiler.backend.utils.irBlockBodyBuilder
import com.github.kitakkun.backintime.compiler.backend.utils.irRegisterRelationship
import com.github.kitakkun.backintime.compiler.backend.utils.receiver
import com.github.kitakkun.backintime.compiler.consts.BackInTimeAnnotations
import com.github.kitakkun.backintime.compiler.consts.BackInTimeConsts
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.builders.irCall
import org.jetbrains.kotlin.ir.builders.irComposite
import org.jetbrains.kotlin.ir.builders.irGet
import org.jetbrains.kotlin.ir.builders.irGetField
import org.jetbrains.kotlin.ir.builders.irIfThen
import org.jetbrains.kotlin.ir.builders.irNotEquals
import org.jetbrains.kotlin.ir.builders.irString
import org.jetbrains.kotlin.ir.builders.irTrue
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrConstructor
import org.jetbrains.kotlin.ir.declarations.IrProperty
import org.jetbrains.kotlin.ir.expressions.IrBlockBody
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.types.classOrNull
import org.jetbrains.kotlin.ir.util.getSimpleFunction
import org.jetbrains.kotlin.ir.util.hasAnnotation
import org.jetbrains.kotlin.ir.util.isGetter
import org.jetbrains.kotlin.ir.util.isSetter
import org.jetbrains.kotlin.ir.util.parentClassOrNull
import org.jetbrains.kotlin.ir.util.properties
import org.jetbrains.kotlin.ir.visitors.IrElementTransformerVoid

/**
 * generate code to register the relationship between the instance and the property (both are debuggable)
 */
context(BackInTimePluginContext)
class RelationshipResolveCallGenerationTransformer(
    private val parentClass: IrClass,
) : IrElementTransformerVoid() {
    private val initializedMapProperty = parentClass.properties.first { it.name == BackInTimeConsts.backInTimeInitializedPropertyMapName }
    private val IrProperty.isDebuggableStateHolder get() = getter?.returnType?.classOrNull?.owner?.hasAnnotation(BackInTimeAnnotations.debuggableStateHolderAnnotationFqName) == true

    override fun visitElement(element: IrElement): IrElement {
        element.transformChildrenVoid()
        return element
    }

    override fun visitConstructor(declaration: IrConstructor): IrStatement {
        val propertyRelationshipResolveCalls = parentClass.properties
            .filter { it.isDebuggableStateHolder && !it.isDelegated && !it.isVar }
            .mapNotNull { property ->
                val backingField = property.backingField ?: return@mapNotNull null
                val parentReceiver = parentClass.thisReceiver ?: return@mapNotNull null

                with(declaration.irBlockBodyBuilder()) {
                    irRegisterRelationship(
                        irGet(parentReceiver),
                        irGetField(receiver = irGet(parentReceiver), field = backingField),
                    )
                }
            }
        (declaration.body as? IrBlockBody)?.statements?.addAll(propertyRelationshipResolveCalls)
        return declaration
    }

    override fun visitCall(expression: IrCall): IrExpression {
        expression.transformChildrenVoid()

        val callingFunction = expression.symbol.owner
        if (!callingFunction.isGetter && !callingFunction.isSetter) return expression

        val property = callingFunction.correspondingPropertySymbol?.owner ?: return expression
        if (property.parentClassOrNull != parentClass) return expression

        val receiver = expression.receiver ?: return expression

        if (property.isDebuggableStateHolder && property.isDelegated) {
            with(expression.irBlockBodyBuilder()) {
                val condition = irNotEquals(
                    arg1 = irTrue(),
                    arg2 = irCall(irBuiltIns.mapClass.getSimpleFunction("get")!!).apply {
                        dispatchReceiver = irGetField(receiver, initializedMapProperty.backingField!!)
                        putValueArgument(0, irString(property.name.asString()))
                    },
                )
                val thenPart = irComposite {
                    +irRegisterRelationship(
                        receiver,
                        irCall(property.getter!!).apply { dispatchReceiver = receiver },
                    )
                    +irCall(irBuiltIns.mutableMapClass.getSimpleFunction("put")!!).apply {
                        dispatchReceiver = irGetField(receiver, initializedMapProperty.backingField!!)
                        putValueArgument(0, irString(property.name.asString()))
                        putValueArgument(1, irTrue())
                    }
                }
                return irComposite {
                    when {
                        callingFunction.isSetter -> +irIfThen(type = irBuiltIns.unitType, condition = irTrue(), thenPart = thenPart)
                        callingFunction.isGetter -> +irIfThen(type = irBuiltIns.unitType, condition = condition, thenPart = thenPart)
                    }
                    +expression
                }
            }
        }
        return expression
    }
}
