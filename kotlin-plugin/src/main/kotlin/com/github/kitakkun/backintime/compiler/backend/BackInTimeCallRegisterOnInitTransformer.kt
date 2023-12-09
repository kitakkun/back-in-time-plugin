package com.github.kitakkun.backintime.compiler.backend

import com.github.kitakkun.backintime.compiler.BackInTimeConsts
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.builders.*
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrConstructor
import org.jetbrains.kotlin.ir.declarations.IrProperty
import org.jetbrains.kotlin.ir.types.classFqName
import org.jetbrains.kotlin.ir.types.defaultType
import org.jetbrains.kotlin.ir.util.*
import org.jetbrains.kotlin.ir.visitors.IrElementTransformerVoid

class BackInTimeCallRegisterOnInitTransformer(
    private val pluginContext: IrPluginContext,
) : IrElementTransformerVoid() {
    private val backInTimeDebugServiceClass = pluginContext.referenceClass(BackInTimeConsts.backInTimeDebugServiceClassId)!!
    private val registerFunction = backInTimeDebugServiceClass.getSimpleFunction(BackInTimeConsts.registerFunctionName)!!
    private val instanceInfoConstructor = pluginContext.referenceConstructors(BackInTimeConsts.instanceInfoClassId).first()
    private val propertyInfoClass = pluginContext.referenceClass(BackInTimeConsts.propertyInfoClassId)!!
    private val propertyInfoClassConstructor = propertyInfoClass.constructors.first()
    private val listOfFunction = pluginContext.referenceFunctions(BackInTimeConsts.listOfFunctionId).first {
        it.owner.valueParameters.size == 1 && it.owner.valueParameters.first().isVararg
    }

    override fun visitConstructor(declaration: IrConstructor): IrStatement {
        val parentClass = declaration.parentClassOrNull ?: return super.visitConstructor(declaration)
        if (parentClass.superTypes.none { it.classFqName == BackInTimeConsts.debuggableStateHolderManipulatorFqName }) return super.visitConstructor(declaration)

        declaration.body = IrBlockBodyBuilder(
            context = pluginContext,
            startOffset = declaration.startOffset,
            endOffset = declaration.endOffset,
            scope = Scope(declaration.symbol),
        ).blockBody {
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
        putValueArgument(0, irVararg(propertyInfoClass.defaultType, properties.map { irProperty ->
            irCallConstructor(propertyInfoClassConstructor, emptyList()).apply {
                putValueArgument(0, irString(irProperty.name.asString()))
                putValueArgument(1, irBoolean(true)) // FIXME: 適当に入れてる
                val genericTypes = irProperty.getGenericTypes()
                if (genericTypes.isEmpty()) {
                    putValueArgument(2, irString(irProperty.backingField?.type?.classFqName?.asString() ?: "unknown"))
                    putValueArgument(3, irString(irProperty.backingField?.type?.classFqName?.asString() ?: "unknown"))
                } else {
                    putValueArgument(2, irString(irProperty.backingField?.type?.classFqName?.asString() ?: "unknown"))
                    putValueArgument(3, irString(genericTypes.first().classFqName?.asString() ?: "unknown"))
                }
            }
        }.toList()))
        putTypeArgument(0, propertyInfoClass.defaultType)
    }
}
