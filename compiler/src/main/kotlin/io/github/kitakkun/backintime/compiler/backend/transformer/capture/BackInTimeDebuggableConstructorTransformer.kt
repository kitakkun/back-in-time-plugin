package io.github.kitakkun.backintime.compiler.backend.transformer.capture

import io.github.kitakkun.backintime.compiler.backend.BackInTimePluginContext
import io.github.kitakkun.backintime.compiler.backend.utils.getCompletedName
import io.github.kitakkun.backintime.compiler.backend.utils.getGenericTypes
import io.github.kitakkun.backintime.compiler.backend.utils.isBackInTimeDebuggable
import io.github.kitakkun.backintime.compiler.consts.BackInTimeAnnotations
import io.github.kitakkun.backintime.compiler.consts.BackInTimeConsts
import org.jetbrains.kotlin.backend.common.lower.createIrBuilder
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.builders.*
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrConstructor
import org.jetbrains.kotlin.ir.declarations.IrProperty
import org.jetbrains.kotlin.ir.expressions.IrBlockBody
import org.jetbrains.kotlin.ir.types.IrSimpleType
import org.jetbrains.kotlin.ir.types.classFqName
import org.jetbrains.kotlin.ir.types.classOrNull
import org.jetbrains.kotlin.ir.types.defaultType
import org.jetbrains.kotlin.ir.util.*
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
        val registerCall = irBuilder.generateRegisterCall(parentClass)
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

    /** see [io.github.kitakkun.backintime.core.runtime.event.BackInTimeDebuggableInstanceEvent.RegisterTarget] */
    private fun IrBuilderWithScope.generateRegisterCall(parentClass: IrClass) = irCall(reportInstanceRegistrationFunctionSymbol).apply {
        putValueArgument(0, irGet(parentClass.thisReceiver!!))
        putValueArgument(1, irString(parentClass.fqNameWhenAvailable?.asString() ?: "unknown"))
        putValueArgument(2, irString(parentClass.superClass?.fqNameWhenAvailable?.asString() ?: "unknown"))
        putValueArgument(3, generatePropertiesInfo(parentClass.properties))
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
