package com.github.kitakkun.backintime.compiler.backend.transformer

import com.github.kitakkun.backintime.compiler.BackInTimeAnnotations
import com.github.kitakkun.backintime.compiler.BackInTimeConsts
import com.github.kitakkun.backintime.compiler.backend.BackInTimePluginContext
import com.github.kitakkun.backintime.compiler.backend.utils.generateUUIDStringCall
import com.github.kitakkun.backintime.compiler.backend.utils.getCompletedName
import com.github.kitakkun.backintime.compiler.backend.utils.getGenericTypes
import com.github.kitakkun.backintime.compiler.backend.utils.hasBackInTimeDebuggableAsInterface
import com.github.kitakkun.backintime.compiler.backend.utils.irBlockBodyBuilder
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.builders.IrBuilderWithScope
import org.jetbrains.kotlin.ir.builders.irBoolean
import org.jetbrains.kotlin.ir.builders.irCall
import org.jetbrains.kotlin.ir.builders.irCallConstructor
import org.jetbrains.kotlin.ir.builders.irGet
import org.jetbrains.kotlin.ir.builders.irGetObject
import org.jetbrains.kotlin.ir.builders.irSetField
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
import org.jetbrains.kotlin.ir.util.classId
import org.jetbrains.kotlin.ir.util.fqNameWhenAvailable
import org.jetbrains.kotlin.ir.util.hasAnnotation
import org.jetbrains.kotlin.ir.util.parentClassOrNull
import org.jetbrains.kotlin.ir.util.properties
import org.jetbrains.kotlin.ir.util.superClass
import org.jetbrains.kotlin.ir.visitors.IrElementTransformerVoid

/**
 * this transformer will:
 * - initialize a UUID field in the constructor
 * - register the instance to [BackInTimeDebugService] in the constructor
 */
context(BackInTimePluginContext)
class ConstructorTransformer : IrElementTransformerVoid() {
    override fun visitConstructor(declaration: IrConstructor): IrStatement {
        val parentClass = declaration.parentClassOrNull ?: return declaration
        if (!parentClass.hasBackInTimeDebuggableAsInterface) return declaration

        with(declaration.irBlockBodyBuilder()) {
            val initUUIDCall = irSetField(
                receiver = irGet(parentClass.thisReceiver!!),
                field = parentClass.properties.first { it.name == BackInTimeConsts.backInTimeInstanceUUIDName }.backingField!!,
                value = generateUUIDStringCall(),
            )

            val initMap = irSetField(
                receiver = irGet(parentClass.thisReceiver!!),
                field = parentClass.properties.first { it.name == BackInTimeConsts.backInTimeInitializedPropertyMapName }.backingField!!,
                value = irCall(mutableMapOfFunction).apply {
                    putTypeArgument(0, irBuiltIns.stringType)
                    putTypeArgument(1, irBuiltIns.booleanType)
                },
            )

            val registerCall = generateRegisterCall(parentClass)

            with(declaration.body as IrBlockBody) {
                statements.add(0, initUUIDCall)
                statements.add(1, initMap)
                statements.add(registerCall)
            }
        }

        return declaration
    }

    // BackInTimeDebugService.register(this, InstanceInfo(...))
    private fun IrBuilderWithScope.generateRegisterCall(parentClass: IrClass) = irCall(registerFunction).apply {
        dispatchReceiver = irGetObject(backInTimeServiceClassSymbol)
        putValueArgument(0, irGet(parentClass.thisReceiver!!))
        putValueArgument(1, generateInstanceInfo(parentClass))
    }

    // InstanceInfo(parentClass.fqNameWhenAvailable, listOf(PropertyInfo(...)))
    private fun IrBuilderWithScope.generateInstanceInfo(
        parentClass: IrClass,
    ) = irCallConstructor(instanceInfoConstructor, emptyList()).apply {
        putValueArgument(0, irString(parentClass.fqNameWhenAvailable?.asString() ?: "unknown"))
        putValueArgument(1, irString(parentClass.superClass?.fqNameWhenAvailable?.asString() ?: "unknown"))
        putValueArgument(2, generatePropertiesInfo(parentClass.properties))
    }

    // listOf(PropertyInfo(...), PropertyInfo(...), ...)
    private fun IrBuilderWithScope.generatePropertiesInfo(
        properties: Sequence<IrProperty>,
    ) = irCall(listOfFunction).apply {
        putValueArgument(0, irVararg(propertyInfoClass.defaultType, properties
            .filter { it.name != BackInTimeConsts.backInTimeInstanceUUIDName && it.name != BackInTimeConsts.backInTimeInitializedPropertyMapName }
            .map { irProperty ->
                val propertyType = irProperty.getter?.returnType as? IrSimpleType
                val propertyTypeName = propertyType?.classFqName?.asString() ?: "unknown"
                val genericTypeCompletedName = (propertyType?.getGenericTypes()?.firstOrNull() as? IrSimpleType)?.getCompletedName() ?: propertyTypeName
                // FIXME: 必ずしも正確な判定ではない
                val isDebuggable = irProperty.isVar || propertyType?.classOrNull?.owner?.classId in valueContainerClassInfoList.map { it.classId }
                val isDebuggableStateHolder = propertyType?.classOrNull?.owner?.hasAnnotation(BackInTimeAnnotations.debuggableStateHolderAnnotationFqName) ?: false
                irCallConstructor(propertyInfoClassConstructor, emptyList()).apply {
                    putValueArgument(0, irString(irProperty.name.asString()))
                    putValueArgument(1, irBoolean(isDebuggable || isDebuggableStateHolder))
                    putValueArgument(2, irBoolean(isDebuggableStateHolder))
                    putValueArgument(3, irString(propertyTypeName))
                    putValueArgument(4, irString(genericTypeCompletedName))
                }
            }.toList()))
        putTypeArgument(0, propertyInfoClass.defaultType)
    }
}
