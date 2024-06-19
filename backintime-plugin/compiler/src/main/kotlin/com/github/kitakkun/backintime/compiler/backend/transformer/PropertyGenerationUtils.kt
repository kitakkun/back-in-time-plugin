package com.github.kitakkun.backintime.compiler.backend.transformer

import com.github.kitakkun.backintime.compiler.backend.BackInTimePluginContext
import com.github.kitakkun.backintime.compiler.backend.utils.generateUUIDStringCall
import org.jetbrains.kotlin.backend.common.lower.createIrBuilder
import org.jetbrains.kotlin.ir.builders.declarations.addBackingField
import org.jetbrains.kotlin.ir.builders.irCall
import org.jetbrains.kotlin.ir.builders.irExprBody
import org.jetbrains.kotlin.ir.declarations.IrProperty
import org.jetbrains.kotlin.ir.types.typeWith

/**
 * add a backing field for the [com.github.kitakkun.backintime.runtime.BackInTimeDebuggable.backInTimeInstanceUUID] property
 */
context(BackInTimePluginContext)
internal fun IrProperty.addBackingFieldOfBackInTimeUUID() {
    val irBuilder = irBuiltIns.createIrBuilder(symbol)
    this.addBackingField {
        this.type = irBuiltIns.stringType
    }.apply {
        initializer = with(irBuilder) {
            irExprBody(generateUUIDStringCall())
        }
    }
}

/**
 * add a backing field for the [com.github.kitakkun.backintime.runtime.BackInTimeDebuggable.backInTimeInitializedPropertyMap] property
 */
context(BackInTimePluginContext)
internal fun IrProperty.addBackingFieldOfBackInTimeInitializedPropertyMap() {
    val irBuilder = irBuiltIns.createIrBuilder(symbol)
    this.addBackingField {
        this.type = irBuiltIns.mutableMapClass.typeWith(
            irBuiltIns.stringType,
            irBuiltIns.booleanType,
        )
    }.apply {
        initializer = with(irBuilder) {
            irExprBody(
                irCall(mutableMapOfFunction).apply {
                    putTypeArgument(0, irBuiltIns.stringType)
                    putTypeArgument(1, irBuiltIns.booleanType)
                },
            )
        }
    }
}
