package com.github.kitakkun.backintime.compiler.backend.utils

import org.jetbrains.kotlin.ir.declarations.IrSimpleFunction

val IrSimpleFunction.isInvokeFunction get() = this.isOperator && this.name.asString() == "invoke"
