package com.github.kitakkun.backintime.compiler.backend

import com.github.kitakkun.backintime.compiler.BackInTimeConsts
import com.github.kitakkun.backintime.compiler.backend.utils.getCompletedName
import com.github.kitakkun.backintime.compiler.backend.utils.getGenericTypes
import com.github.kitakkun.backintime.compiler.backend.utils.irBlockBodyBuilder
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.builders.IrBuilderWithScope
import org.jetbrains.kotlin.ir.builders.irBoolean
import org.jetbrains.kotlin.ir.builders.irCall
import org.jetbrains.kotlin.ir.builders.irCallConstructor
import org.jetbrains.kotlin.ir.builders.irGet
import org.jetbrains.kotlin.ir.builders.irGetObject
import org.jetbrains.kotlin.ir.builders.irString
import org.jetbrains.kotlin.ir.builders.irVararg
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrConstructor
import org.jetbrains.kotlin.ir.declarations.IrProperty
import org.jetbrains.kotlin.ir.types.IrSimpleType
import org.jetbrains.kotlin.ir.types.classFqName
import org.jetbrains.kotlin.ir.types.defaultType
import org.jetbrains.kotlin.ir.util.constructors
import org.jetbrains.kotlin.ir.util.fqNameWhenAvailable
import org.jetbrains.kotlin.ir.util.getSimpleFunction
import org.jetbrains.kotlin.ir.util.isVararg
import org.jetbrains.kotlin.ir.util.parentClassOrNull
import org.jetbrains.kotlin.ir.util.properties
import org.jetbrains.kotlin.ir.util.statements
import org.jetbrains.kotlin.ir.visitors.IrElementTransformerVoid

class BackInTimeCallRegisterOnInitTransformer(
    private val pluginContext: IrPluginContext,
) : IrElementTransformerVoid() {
    private val backInTimeDebugServiceClass = pluginContext.referenceClass(BackInTimeConsts.backInTimeDebugServiceClassId)!!
    private val registerFunction = backInTimeDebugServiceClass.getSimpleFunction(BackInTimeConsts.registerFunctionName)!!

    // find by isPrimary because kotlinx-serialization generates secondary constructor
    private val instanceInfoConstructor = pluginContext.referenceConstructors(BackInTimeConsts.instanceInfoClassId).first { it.owner.isPrimary }
    private val propertyInfoClass = pluginContext.referenceClass(BackInTimeConsts.propertyInfoClassId)!!
    private val propertyInfoClassConstructor = propertyInfoClass.constructors.first { it.owner.isPrimary }
    private val listOfFunction = pluginContext.referenceFunctions(BackInTimeConsts.listOfFunctionId).first {
        it.owner.valueParameters.size == 1 && it.owner.valueParameters.first().isVararg
    }

    override fun visitConstructor(declaration: IrConstructor): IrStatement {
        val parentClass = declaration.parentClassOrNull ?: return super.visitConstructor(declaration)
        if (parentClass.superTypes.none { it.classFqName == BackInTimeConsts.debuggableStateHolderManipulatorFqName }) return super.visitConstructor(declaration)

        declaration.body = declaration.irBlockBodyBuilder(pluginContext).blockBody {
            +declaration.body?.statements.orEmpty()
            +generateRegisterCall(parentClass)
        }

        return super.visitConstructor(declaration)
    }

    // BackInTimeDebugService.register(this, InstanceInfo(...))
    private fun IrBuilderWithScope.generateRegisterCall(parentClass: IrClass) = irCall(registerFunction).apply {
        dispatchReceiver = irGetObject(backInTimeDebugServiceClass)
        putValueArgument(0, irGet(parentClass.thisReceiver!!))
        putValueArgument(1, generateInstanceInfo(parentClass))
    }

    // InstanceInfo(parentClass.fqNameWhenAvailable, listOf(PropertyInfo(...)))
    private fun IrBuilderWithScope.generateInstanceInfo(
        parentClass: IrClass,
    ) = irCallConstructor(instanceInfoConstructor, emptyList()).apply {
        putValueArgument(0, irString(parentClass.fqNameWhenAvailable?.asString() ?: "unknown"))
        putValueArgument(1, generatePropertiesInfo(parentClass.properties))
    }

    // listOf(PropertyInfo(...), PropertyInfo(...), ...)
    private fun IrBuilderWithScope.generatePropertiesInfo(
        properties: Sequence<IrProperty>,
    ) = irCall(listOfFunction).apply {
        putValueArgument(0, irVararg(propertyInfoClass.defaultType, properties.mapNotNull { irProperty ->
            val propertyType = irProperty.getter?.returnType as? IrSimpleType
            val propertyTypeName = propertyType?.classFqName?.asString() ?: "unknown"
            val genericTypeCompletedName = (propertyType?.getGenericTypes()?.firstOrNull() as? IrSimpleType)?.getCompletedName() ?: propertyTypeName
            irCallConstructor(propertyInfoClassConstructor, emptyList()).apply {
                putValueArgument(0, irString(irProperty.name.asString()))
                putValueArgument(1, irBoolean(true)) // FIXME: 適当に入れてる
                putValueArgument(2, irString(propertyTypeName))
                putValueArgument(3, irString(genericTypeCompletedName))
            }
        }.toList()))
        putTypeArgument(0, propertyInfoClass.defaultType)
    }
}
