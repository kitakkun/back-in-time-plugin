package com.github.kitakkun.backintime.compiler.backend.transformer

import com.github.kitakkun.backintime.compiler.backend.BackInTimePluginContext
import com.github.kitakkun.backintime.compiler.backend.utils.getCompletedName
import com.github.kitakkun.backintime.compiler.backend.utils.getGenericTypes
import com.github.kitakkun.backintime.compiler.consts.BackInTimeAnnotations
import com.github.kitakkun.backintime.compiler.consts.BackInTimeConsts
import org.jetbrains.kotlin.ir.builders.IrBuilderWithScope
import org.jetbrains.kotlin.ir.builders.irBoolean
import org.jetbrains.kotlin.ir.builders.irCall
import org.jetbrains.kotlin.ir.builders.irCallConstructor
import org.jetbrains.kotlin.ir.builders.irGet
import org.jetbrains.kotlin.ir.builders.irString
import org.jetbrains.kotlin.ir.builders.irVararg
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrProperty
import org.jetbrains.kotlin.ir.types.IrSimpleType
import org.jetbrains.kotlin.ir.types.classFqName
import org.jetbrains.kotlin.ir.types.classOrNull
import org.jetbrains.kotlin.ir.types.defaultType
import org.jetbrains.kotlin.ir.util.classId
import org.jetbrains.kotlin.ir.util.fqNameWhenAvailable
import org.jetbrains.kotlin.ir.util.hasAnnotation
import org.jetbrains.kotlin.ir.util.properties
import org.jetbrains.kotlin.ir.util.superClass

/** see [com.github.kitakkun.backintime.runtime.event.BackInTimeDebuggableInstanceEvent] */
context(BackInTimePluginContext)
internal fun IrBuilderWithScope.generateRegisterCall(parentClass: IrClass) = irCall(reportInstanceRegistrationFunctionSymbol).apply {
    putValueArgument(0, irGet(parentClass.thisReceiver!!))
    putValueArgument(1, irString(parentClass.fqNameWhenAvailable?.asString() ?: "unknown"))
    putValueArgument(2, irString(parentClass.superClass?.fqNameWhenAvailable?.asString() ?: "unknown"))
    putValueArgument(3, generatePropertiesInfo(parentClass.properties))
}

context(BackInTimePluginContext)
internal fun IrBuilderWithScope.generatePropertiesInfo(
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
                    val isDebuggable = irProperty.isVar || propertyType?.classOrNull?.owner?.classId in valueContainerClassInfoList.map { it.classId }
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
