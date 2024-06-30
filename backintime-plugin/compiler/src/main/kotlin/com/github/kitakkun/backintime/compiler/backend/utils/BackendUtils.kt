package com.github.kitakkun.backintime.compiler.backend.utils

import com.github.kitakkun.backintime.compiler.backend.BackInTimePluginContext
import com.github.kitakkun.backintime.compiler.consts.BackInTimeConsts
import com.github.kitakkun.backintime.compiler.consts.BackInTimePluginKey
import org.jetbrains.kotlin.ir.builders.IrBuilderWithScope
import org.jetbrains.kotlin.ir.builders.irCall
import org.jetbrains.kotlin.ir.builders.parent
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrDeclaration
import org.jetbrains.kotlin.ir.declarations.IrDeclarationOrigin
import org.jetbrains.kotlin.ir.declarations.IrProperty
import org.jetbrains.kotlin.ir.declarations.IrVariable
import org.jetbrains.kotlin.ir.declarations.impl.IrVariableImpl
import org.jetbrains.kotlin.ir.symbols.impl.IrVariableSymbolImpl
import org.jetbrains.kotlin.ir.types.classOrNull
import org.jetbrains.kotlin.ir.types.getClass
import org.jetbrains.kotlin.ir.util.classId
import org.jetbrains.kotlin.ir.util.getAllSuperclasses
import org.jetbrains.kotlin.name.Name

context(BackInTimePluginContext)
fun IrBuilderWithScope.generateUUIDVariable(): IrVariable {
    return IrVariableImpl(
        startOffset = this.startOffset,
        endOffset = this.endOffset,
        origin = IrDeclarationOrigin.GeneratedByPlugin(BackInTimePluginKey),
        symbol = IrVariableSymbolImpl(),
        name = Name.identifier("backInTimeUUID"),
        type = irBuiltIns.stringType,
        isVar = false,
        isConst = false,
        isLateinit = false,
    ).apply {
        this.initializer = irCall(uuidFunctionSymbol)
        this.parent = this@generateUUIDVariable.parent
    }
}

val IrDeclaration.isBackInTimeGenerated: Boolean
    get() {
        val origin = this.origin as? IrDeclarationOrigin.GeneratedByPlugin ?: return false
        return origin.pluginKey == BackInTimePluginKey
    }

val IrClass.isBackInTimeDebuggable: Boolean
    get() = this.superTypes.any { it.getClass()?.classId == BackInTimeConsts.backInTimeDebuggableInterfaceClassId }

val IrProperty.isBackInTimeDebuggable get() = getter?.returnType?.classOrNull?.owner?.getAllSuperclasses()?.any { it.classId == BackInTimeConsts.backInTimeDebuggableInterfaceClassId } ?: false
