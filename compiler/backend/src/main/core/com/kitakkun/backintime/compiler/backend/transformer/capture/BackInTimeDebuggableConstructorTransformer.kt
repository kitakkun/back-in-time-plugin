package com.kitakkun.backintime.compiler.backend.transformer.capture

import com.kitakkun.backintime.compiler.backend.BackInTimePluginContext
import com.kitakkun.backintime.compiler.backend.utils.getCompletedName
import com.kitakkun.backintime.compiler.backend.utils.getGenericTypes
import com.kitakkun.backintime.compiler.backend.utils.isBackInTimeDebuggable
import com.kitakkun.backintime.compiler.common.BackInTimeAnnotations
import com.kitakkun.backintime.compiler.common.BackInTimeConsts
import org.jetbrains.kotlin.backend.common.lower.createIrBuilder
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.builders.IrBuilderWithScope
import org.jetbrains.kotlin.ir.builders.irBoolean
import org.jetbrains.kotlin.ir.builders.irCall
import org.jetbrains.kotlin.ir.builders.irCallConstructor
import org.jetbrains.kotlin.ir.builders.irGet
import org.jetbrains.kotlin.ir.builders.irGetField
import org.jetbrains.kotlin.ir.builders.irString
import org.jetbrains.kotlin.ir.builders.irVararg
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrConstructor
import org.jetbrains.kotlin.ir.declarations.IrProperty
import org.jetbrains.kotlin.ir.expressions.IrBlockBody
import org.jetbrains.kotlin.ir.types.IrSimpleType
import org.jetbrains.kotlin.ir.types.classFqName
import org.jetbrains.kotlin.ir.types.classOrNull
import org.jetbrains.kotlin.ir.types.defaultType
import org.jetbrains.kotlin.ir.util.fqNameWhenAvailable
import org.jetbrains.kotlin.ir.util.hasAnnotation
import org.jetbrains.kotlin.ir.util.parentAsClass
import org.jetbrains.kotlin.ir.util.properties
import org.jetbrains.kotlin.ir.util.superClass
import org.jetbrains.kotlin.ir.visitors.IrElementTransformerVoid

/**
 * insert register call and property relationship resolve calls
 * to [IrConstructor] of [IrClass]es that are [isBackInTimeDebuggable]
 */
context(BackInTimePluginContext)
class BackInTimeDebuggableConstructorTransformer : IrElementTransformerVoid() {
    override fun visitConstructor(declaration: IrConstructor): IrStatement {
        val parentClass = declaration.parentAsClass
        if (!parentClass.isBackInTimeDebuggable) return declaration

        val irBuilder = irBuiltIns.createIrBuilder(declaration.symbol)

        val registerCall = with(irBuilder) {
            /** see [com.kitakkun.backintime.core.runtime.event.BackInTimeDebuggableInstanceEvent.RegisterTarget] */
            irCall(reportInstanceRegistrationFunctionSymbol).apply {
                putValueArgument(0, irGet(parentClass.thisReceiver!!))
                putValueArgument(1, irString(parentClass.fqNameWhenAvailable?.asString() ?: "unknown"))
                putValueArgument(2, irString(parentClass.superClass?.fqNameWhenAvailable?.asString() ?: "unknown"))
                putValueArgument(3, generatePropertiesInfo(parentClass.properties))
            }
        }

        val propertyRelationshipResolveCalls = parentClass.properties
            .filter { it.isBackInTimeDebuggable && !it.isDelegated && !it.isVar }
            .mapNotNull { property ->
                val backingField = property.backingField ?: return@mapNotNull null
                val parentReceiver = parentClass.thisReceiver ?: return@mapNotNull null

                with(irBuilder) {
                    irCall(reportNewRelationshipFunctionSymbol).apply {
                        putValueArgument(0, irGet(parentReceiver))
                        putValueArgument(1, irGetField(receiver = irGet(parentReceiver), field = backingField))
                    }
                }
            }

        (declaration.body as IrBlockBody).statements += listOf(registerCall) + propertyRelationshipResolveCalls

        return declaration
    }

    private fun IrBuilderWithScope.generatePropertiesInfo(
        properties: Sequence<IrProperty>,
    ) = irCall(listOfFunction).apply {
        putValueArgument(
            0,
            irVararg(
                propertyInfoClass.defaultType,
                properties
                    .filter { it.name != BackInTimeConsts.backInTimeInstanceUUIDName && it.name != BackInTimeConsts.backInTimeInitializedPropertyMapName }
                    .map { irProperty ->
                        val propertyType = irProperty.getter?.returnType as? IrSimpleType
                        val propertyTypeName = propertyType?.classFqName?.asString() ?: "unknown"
                        val genericTypeCompletedName = (propertyType?.getGenericTypes()?.firstOrNull() as? IrSimpleType)?.getCompletedName() ?: propertyTypeName
                        // FIXME: 必ずしも正確な判定ではない
                        val isDebuggable = irProperty.isVar || propertyType?.classOrNull in valueContainerClassInfoList.map { it.classSymbol }
                        val isDebuggableStateHolder = propertyType?.classOrNull?.owner?.hasAnnotation(BackInTimeAnnotations.backInTimeAnnotationFqName) ?: false
                        irCallConstructor(propertyInfoClassConstructor, emptyList()).apply {
                            putValueArgument(0, irString(irProperty.name.asString()))
                            putValueArgument(1, irBoolean(isDebuggable || isDebuggableStateHolder))
                            putValueArgument(2, irBoolean(isDebuggableStateHolder))
                            putValueArgument(3, irString(propertyTypeName))
                            putValueArgument(4, irString(genericTypeCompletedName))
                        }
                    }.toList(),
            ),
        )
        putTypeArgument(0, propertyInfoClass.defaultType)
    }
}
