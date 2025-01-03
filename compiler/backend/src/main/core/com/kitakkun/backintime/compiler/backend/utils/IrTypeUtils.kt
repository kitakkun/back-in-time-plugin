package com.kitakkun.backintime.compiler.backend.utils

import com.kitakkun.backintime.compiler.backend.BackInTimePluginContext
import com.kitakkun.backintime.compiler.backend.valuecontainer.ResolvedValueContainer
import org.jetbrains.kotlin.ir.types.IrSimpleType
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.types.classFqName
import org.jetbrains.kotlin.ir.types.classOrNull
import org.jetbrains.kotlin.ir.types.typeOrFail
import org.jetbrains.kotlin.ir.types.typeOrNull
import org.jetbrains.kotlin.ir.types.typeWith

fun IrType.getGenericTypes(): List<IrType> {
    return (this as? IrSimpleType)
        ?.arguments
        ?.mapNotNull { it.typeOrNull }
        .orEmpty()
}

fun IrSimpleType.getCompletedName(): String? {
    if (this.arguments.isEmpty()) {
        return this.classFqName?.asString()
    } else {
        val typeArgumentNames = this.arguments.map { (it.typeOrNull as? IrSimpleType)?.getCompletedName() }
        if (typeArgumentNames.any { it == null }) return null
        return this.classFqName?.asString() + typeArgumentNames.joinToString(prefix = "<", postfix = ">") { it!! }
    }
}

context(BackInTimePluginContext)
fun IrType.getSerializerType(): IrType? {
    val valueContainerClassInfo = valueContainerClassInfoList.find { it.classSymbol == this.classOrNull } ?: return this

    val typeArguments = (this as? IrSimpleType)?.arguments?.map { it.typeOrFail } ?: return null
    val manuallyConfiguredSerializeType = valueContainerClassInfo.serializeAs?.owner?.typeWith(typeArguments)
    if (manuallyConfiguredSerializeType != null) {
        return manuallyConfiguredSerializeType
    }

    if (valueContainerClassInfo is ResolvedValueContainer.SelfContained) {
        return this
    }

    return typeArguments.firstOrNull()
}

