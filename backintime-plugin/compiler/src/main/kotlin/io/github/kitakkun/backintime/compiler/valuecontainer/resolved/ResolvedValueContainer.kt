package io.github.kitakkun.backintime.compiler.valuecontainer.resolved

import io.github.kitakkun.backintime.compiler.util.MessageCollectorHolder
import io.github.kitakkun.backintime.compiler.valuecontainer.raw.CaptureStrategy
import io.github.kitakkun.backintime.compiler.valuecontainer.raw.RawValueContainer
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.symbols.IrClassSymbol
import org.jetbrains.kotlin.ir.symbols.IrSimpleFunctionSymbol
import org.jetbrains.kotlin.ir.util.getAllSuperclasses
import org.jetbrains.kotlin.ir.util.kotlinFqName
import org.jetbrains.kotlin.ir.util.simpleFunctions
import org.jetbrains.kotlin.synthetic.isVisibleOutside

sealed class ResolvedValueContainer {
    abstract val getterSymbol: IrSimpleFunctionSymbol?
    abstract val classSymbol: IrClassSymbol
    abstract val setterSymbols: List<IrSimpleFunctionSymbol>
    abstract val captureTargetSymbols: List<Pair<IrSimpleFunctionSymbol, CaptureStrategy>>
    abstract val serializeAs: IrClassSymbol?

    data class SelfContained(
        override val classSymbol: IrClassSymbol,
        override val setterSymbols: List<IrSimpleFunctionSymbol>,
        override val captureTargetSymbols: List<Pair<IrSimpleFunctionSymbol, CaptureStrategy>>,
        override val serializeAs: IrClassSymbol?,
    ) : ResolvedValueContainer() {
        override val getterSymbol: IrSimpleFunctionSymbol? = null
    }

    data class Wrapper(
        override val classSymbol: IrClassSymbol,
        override val setterSymbols: List<IrSimpleFunctionSymbol>,
        override val captureTargetSymbols: List<Pair<IrSimpleFunctionSymbol, CaptureStrategy>>,
        override val getterSymbol: IrSimpleFunctionSymbol,
        override val serializeAs: IrClassSymbol?,
    ) : ResolvedValueContainer()

    companion object {
        context(IrPluginContext)
        fun create(valueContainer: RawValueContainer): ResolvedValueContainer? {
            val classId = valueContainer.classId
            val classSymbol = referenceClass(classId) ?: return null

            val allVisibleMemberCallables = classSymbol.owner.getAllMemberCallables().filter { it.owner.visibility.isVisibleOutside() }
            val allOverriddenCallables = allVisibleMemberCallables.flatMap { it.owner.overriddenSymbols }.toSet()
            val allPossibleMemberCallables = allVisibleMemberCallables - allOverriddenCallables

            val possibleSetterSymbols = valueContainer.setter.map { it.getMatchedSymbols(allPossibleMemberCallables) }
            val captureTargetSymbols = valueContainer.captureTargets.flatMap { (matcher, strategy) ->
                // use visible one to avoid to accidentally ignore capture targets
                matcher.getMatchedSymbols(allVisibleMemberCallables).map { it to strategy }
            }

            val serializeClassSymbol = valueContainer.serializeAs?.let { referenceClass(it) }

            // if multiple possible setter functions are found, report a warning
            if (possibleSetterSymbols.any { it.size > 1 }) {
                MessageCollectorHolder.reportWarning(
                    "${classId.asString()} has multiple setter occurrences. Check if the matching filter is specific enough. matched setters: " +
                        possibleSetterSymbols.joinToString("\n") { "[" + it.joinToString(", ") { it.owner.kotlinFqName.asString() } + "]" },
                )
                return null
            }

            when (valueContainer) {
                is RawValueContainer.SelfContained -> {
                    return SelfContained(
                        classSymbol = classSymbol,
                        setterSymbols = possibleSetterSymbols.map { it.single() },
                        captureTargetSymbols = captureTargetSymbols,
                        serializeAs = serializeClassSymbol,
                    )
                }

                is RawValueContainer.Wrapper -> {
                    val possibleGetterSymbols = valueContainer.getter.getMatchedSymbols(allPossibleMemberCallables)
                    // if multiple possible getter symbols are found, report a warning
                    if (possibleGetterSymbols.size > 1) {
                        MessageCollectorHolder.reportWarning(
                            "${classId.asString()} has multiple getter occurrences. Check if the matching filter is specific enough. matched getters: " +
                                possibleGetterSymbols.joinToString(", ") { it.owner.kotlinFqName.asString() },
                        )
                        return null
                    } else if (possibleGetterSymbols.isEmpty()) {
                        MessageCollectorHolder.reportWarning(
                            "${classId.asString()} has no getter. Check if the matching filter is specific enough.",
                        )
                        return null
                    }

                    return Wrapper(
                        classSymbol = classSymbol,
                        setterSymbols = possibleSetterSymbols.map { it.single() },
                        captureTargetSymbols = captureTargetSymbols,
                        getterSymbol = possibleGetterSymbols.single(),
                        serializeAs = serializeClassSymbol,
                    )
                }
            }
        }

        private fun IrClass.getAllMemberCallables(): List<IrSimpleFunctionSymbol> {
            return (simpleFunctions() + getAllSuperclasses().flatMap { it.simpleFunctions() }).map { it.symbol }
        }
    }

    fun shouldCapture(target: IrSimpleFunctionSymbol): Boolean {
        return captureTargetSymbols.any { it.first == target }
    }
}
