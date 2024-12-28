package com.kitakkun.backintime.compiler.backend.valuecontainer.resolved

import com.kitakkun.backintime.compiler.backend.MessageCollectorHolder
import com.kitakkun.backintime.compiler.backend.valuecontainer.raw.CaptureStrategy
import com.kitakkun.backintime.compiler.backend.valuecontainer.raw.RawValueContainer
import com.kitakkun.backintime.compiler.yaml.CallableSignature
import com.kitakkun.backintime.compiler.yaml.TrackableStateHolder
import com.kitakkun.backintime.compiler.yaml.TypeSignature
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.symbols.IrClassSymbol
import org.jetbrains.kotlin.ir.symbols.IrSimpleFunctionSymbol
import org.jetbrains.kotlin.ir.util.getAllSuperclasses
import org.jetbrains.kotlin.ir.util.isGetter
import org.jetbrains.kotlin.ir.util.isSetter
import org.jetbrains.kotlin.ir.util.kotlinFqName
import org.jetbrains.kotlin.ir.util.simpleFunctions
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
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
        fun create(valueContainer: RawValueContainer.SelfContained): ResolvedValueContainer? {
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

            return SelfContained(
                classSymbol = classSymbol,
                setterSymbols = possibleSetterSymbols.map { it.single() },
                captureTargetSymbols = captureTargetSymbols,
                serializeAs = serializeClassSymbol,
            )
        }

        context(IrPluginContext)
        fun create(trackableStateHolder: TrackableStateHolder): ResolvedValueContainer? {
            val classId = ClassId.fromString(trackableStateHolder.classId)

            val classSymbol = referenceClass(classId) ?: return null

            val allVisibleMemberCallables = classSymbol.owner.getAllMemberCallables().filter { it.owner.visibility.isVisibleOutside() }
            val allOverriddenCallables = allVisibleMemberCallables.flatMap { it.owner.overriddenSymbols }.toSet()
            val allPossibleMemberCallables = allVisibleMemberCallables - allOverriddenCallables

            val setterFunctionSymbol = when (val setter = trackableStateHolder.accessor.setter) {
                is CallableSignature.PropertyAccessor.Setter -> allPossibleMemberCallables.find {
                    it.owner.name.asString() == "<set-${setter.propertyName}>" && it.owner.isSetter
                }

                is CallableSignature.NamedFunction -> allPossibleMemberCallables.find {
                    it.owner.name.asString() == setter.name
                }

                else -> null
            } ?: return null

            val getterFunctionSymbol = when (val getter = trackableStateHolder.accessor.getter) {
                is CallableSignature.PropertyAccessor.Getter -> allPossibleMemberCallables.find {
                    it.owner.name.asString() == "<get-${getter.propertyName}>" && it.owner.isGetter
                }

                is CallableSignature.NamedFunction -> allPossibleMemberCallables.find {
                    it.owner.name.asString() == getter.name
                }

                else -> null
            } ?: return null

            val captureTargetSymbols = trackableStateHolder.captures.mapNotNull { captureTarget ->
                val functionSymbol = when (val signature = captureTarget.signature) {
                    is CallableSignature.PropertyAccessor.Getter -> {
                        allVisibleMemberCallables.find {
                            it.owner.name.asString() == "<get-${signature.propertyName}>"
                        }
                    }

                    is CallableSignature.PropertyAccessor.Setter -> {
                        allVisibleMemberCallables.find {
                            it.owner.name.asString() == "<set-${signature.propertyName}>"
                        }
                    }

                    is CallableSignature.NamedFunction -> {
                        allVisibleMemberCallables.find { it.owner.name.asString() == signature.name } ?:
                        // FIXME: just for supporting extension functions. Not proper way. Need to be fixed later.
                        referenceFunctions(
                            CallableId(
                                FqName(signature.name.split("/").dropLast(1).joinToString(".")),
                                Name.identifier(signature.name.split("/").lastOrNull() ?: "")
                            )
                        ).firstOrNull()
                    }

                    else -> null
                } ?: return@mapNotNull null

                val strategy = when (val strategy = captureTarget.strategy) {
                    is com.kitakkun.backintime.compiler.yaml.CaptureStrategy.ValueArgument -> CaptureStrategy.ValueArgument(strategy.index)
                    is com.kitakkun.backintime.compiler.yaml.CaptureStrategy.AfterCall -> CaptureStrategy.AfterCall
                }

                functionSymbol to strategy
            }

            val serializeClassSymbol = (trackableStateHolder.serializeAs as? TypeSignature.Class)?.classId?.let {
                referenceClass(ClassId.fromString(it))
            }

            return Wrapper(
                classSymbol = classSymbol,
                setterSymbols = listOf(setterFunctionSymbol),
                captureTargetSymbols = captureTargetSymbols,
                getterSymbol = getterFunctionSymbol,
                serializeAs = serializeClassSymbol,
            )
        }

        private fun IrClass.getAllMemberCallables(): List<IrSimpleFunctionSymbol> {
            return (simpleFunctions() + getAllSuperclasses().flatMap { it.simpleFunctions() }).map { it.symbol }
        }
    }
}
