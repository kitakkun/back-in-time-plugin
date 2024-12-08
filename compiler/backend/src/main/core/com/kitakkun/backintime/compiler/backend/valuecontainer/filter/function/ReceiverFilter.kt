package com.kitakkun.backintime.compiler.backend.valuecontainer.filter.function

import com.kitakkun.backintime.compiler.backend.valuecontainer.filter.type.TypeMatcher
import org.jetbrains.kotlin.ir.declarations.IrSimpleFunction
import org.jetbrains.kotlin.ir.declarations.IrValueParameter

sealed class ReceiverFilter(private val typeMatcher: TypeMatcher) : IrFunctionFilter {
    final override fun matches(function: IrSimpleFunction): Boolean {
        val receiver = getReceiverParameter(function) ?: return false
        return typeMatcher.matches(receiver.type)
    }

    abstract fun getReceiverParameter(function: IrSimpleFunction): IrValueParameter?
}

class ExtensionReceiverFilter(typeMatcher: TypeMatcher) : ReceiverFilter(typeMatcher) {
    override fun getReceiverParameter(function: IrSimpleFunction): IrValueParameter? {
        return function.extensionReceiverParameter
    }
}

class DispatchReceiverFilter(typeMatcher: TypeMatcher) : ReceiverFilter(typeMatcher) {
    override fun getReceiverParameter(function: IrSimpleFunction): IrValueParameter? {
        return function.dispatchReceiverParameter
    }
}
