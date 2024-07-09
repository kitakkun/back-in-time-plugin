package io.github.kitakkun.backintime.compiler.valuecontainer.match

import io.github.kitakkun.backintime.compiler.valuecontainer.filter.function.IrFunctionFilter
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.declarations.IrSimpleFunction
import org.jetbrains.kotlin.ir.symbols.IrSimpleFunctionSymbol
import org.jetbrains.kotlin.name.CallableId

sealed class FunctionPredicate {
    context(IrPluginContext)
    fun getMatchedSymbols(allMemberCallables: List<IrSimpleFunctionSymbol>): List<IrSimpleFunctionSymbol> {
        return when (this) {
            is LookupPredicate -> lookup()
            is MemberDeclarationPredicate -> allMemberCallables.filter { matches(it.owner) }
        }
    }
}

data class LookupPredicate(
    val callableId: CallableId,
    val filters: List<IrFunctionFilter>,
) : FunctionPredicate() {
    context(IrPluginContext)
    fun lookup(): List<IrSimpleFunctionSymbol> {
        return referenceFunctions(callableId).filter { functionSymbol ->
            filters.all { it.matches(functionSymbol.owner) }
        }
    }
}

data class MemberDeclarationPredicate(val filters: List<IrFunctionFilter>) : FunctionPredicate() {
    fun matches(function: IrSimpleFunction): Boolean {
        return filters.all { it.matches(function) }
    }
}
