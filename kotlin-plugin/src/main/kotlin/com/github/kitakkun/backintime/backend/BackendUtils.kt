package com.github.kitakkun.backintime.backend

import com.github.kitakkun.backintime.BackInTimeConsts
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.builders.*
import org.jetbrains.kotlin.ir.declarations.*
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.symbols.IrSimpleFunctionSymbol
import org.jetbrains.kotlin.ir.types.*
import org.jetbrains.kotlin.ir.util.getPropertyGetter
import org.jetbrains.kotlin.ir.util.getSimpleFunction

@Suppress("UNUSED")
fun IrType.isKotlinPrimitiveType(): Boolean {
    return when (this.classFqName?.asString()) {
        "kotlin.Boolean" -> true
        "kotlin.Byte" -> true
        "kotlin.Char" -> true
        "kotlin.Short" -> true
        "kotlin.Int" -> true
        "kotlin.Long" -> true
        "kotlin.Float" -> true
        "kotlin.Double" -> true
        "kotlin.String" -> true
        else -> false
    }
}

/**
 * LiveData<T> のようなジェネリック型のTの部分を取得する
 */
fun IrProperty.getGenericTypes(): List<IrType> {
    return (this.backingField?.type as? IrSimpleType)
        ?.arguments
        ?.mapNotNull { it.typeOrNull }
        .orEmpty()
}

context(IrPluginContext)
@Suppress("UNUSED")
fun IrBuilderWithScope.generateDebugPrintCall(argument: IrExpression): IrCall {
    val printlnFunction = referenceFunctions(BackInTimeConsts.printlnCallableId).first {
        it.owner.valueParameters.size == 1 && it.owner.valueParameters.first().type == irBuiltIns.anyNType
    }
    return irCall(printlnFunction).apply {
        putValueArgument(0, argument)
    }
}

@Suppress("UNUSED")
fun IrFunction.generateSignature(): String {
    return "${this.name}(${this.valueParameters.joinToString { "${it.name}: ${it.type.classFqName}" }}): ${this.returnType.classFqName}"
}

context(IrBuilderWithScope, IrPluginContext)
fun irNotifyValueChangeCall(
    parentInstance: IrValueParameter,
    propertyName: String,
    propertyTypeClassFqName: String,
    value: IrExpression,
    callInfo: IrVariable,
): IrExpression? {
    val debugServiceClass = referenceClass(BackInTimeConsts.backInTimeDebugServiceClassId) ?: return null
    val notifyValueChangeFunction = debugServiceClass.getSimpleFunction(BackInTimeConsts.notifyPropertyChanged) ?: return null
    return irCall(notifyValueChangeFunction).apply {
        dispatchReceiver = irGetObject(debugServiceClass)
        putValueArgument(0, irGet(parentInstance))
        putValueArgument(1, irString(propertyName))
        putValueArgument(2, value)
        putValueArgument(3, irString(propertyTypeClassFqName))
        putValueArgument(4, irGet(callInfo))
    }
}

fun IrClass.getSimpleFunctionRecursively(name: String): IrSimpleFunctionSymbol? {
    return getSimpleFunction(name)
        ?: superTypes
            .mapNotNull { it.classOrNull }
            .firstNotNullOfOrNull { it.getSimpleFunction(name) }
}

fun IrClass.getPropertyGetterRecursively(name: String): IrSimpleFunctionSymbol? {
    return getPropertyGetter(name)
        ?: superTypes
            .mapNotNull { it.classOrNull }
            .firstNotNullOfOrNull { it.getPropertyGetter(name) }
}
