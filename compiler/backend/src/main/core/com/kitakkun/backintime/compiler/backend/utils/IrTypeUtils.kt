package com.kitakkun.backintime.compiler.backend.utils

import com.kitakkun.backintime.compiler.backend.BackInTimePluginContext
import com.kitakkun.backintime.compiler.backend.trackablestateholder.ResolvedTrackableStateHolder
import org.jetbrains.kotlin.ir.types.IrSimpleType
import org.jetbrains.kotlin.ir.types.IrType
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

fun IrType.getSerializerType(
    irContext: BackInTimePluginContext,
): IrType? {
    val trackableStateHolderClassInfo = irContext.trackableStateHolderClassInfoList.find { it.classSymbol == this.classOrNull } ?: return this

    val typeArguments = (this as? IrSimpleType)?.arguments?.map { it.typeOrFail } ?: return null
    val manuallyConfiguredSerializeType = trackableStateHolderClassInfo.serializeAs?.owner?.typeWith(typeArguments)
    if (manuallyConfiguredSerializeType != null) {
        return manuallyConfiguredSerializeType
    }

    if (trackableStateHolderClassInfo is ResolvedTrackableStateHolder.SelfContained) {
        return this
    }

    return typeArguments.firstOrNull()
}

