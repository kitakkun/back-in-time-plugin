package com.kitakkun.backintime.compiler.backend.valuecontainer

import com.kitakkun.backintime.compiler.backend.MessageCollectorHolder
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
        fun create(trackableStateHolder: TrackableStateHolder): ResolvedValueContainer? {
            val classId = ClassId.fromString(trackableStateHolder.classId)

            val classSymbol = referenceClass(classId) ?: return null

            val allVisibleMemberCallables = classSymbol.owner.getAllMemberCallables().filter { it.owner.visibility.isVisibleOutside() }
            val allOverriddenCallables = allVisibleMemberCallables.flatMap { it.owner.overriddenSymbols }.toSet()
            val allPossibleMemberCallables = allVisibleMemberCallables - allOverriddenCallables

            val preSetterFunctionSymbol = when (val preSetter = trackableStateHolder.accessor.preSetter) {
                is CallableSignature.NamedFunction -> {
                    allVisibleMemberCallables.find { it.owner.name.asString() == preSetter.name }
                }

                else -> null
            }

            val setterFunctionSymbol = when (val setter = trackableStateHolder.accessor.setter) {
                is CallableSignature.PropertyAccessor.Setter -> allPossibleMemberCallables.find {
                    it.owner.name.asString() == "<set-${setter.propertyName}>" && it.owner.isSetter
                }

                is CallableSignature.NamedFunction -> allPossibleMemberCallables.find {
                    it.owner.name.asString() == setter.name && it.owner.valueParameters.size == 1
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

                    is CallableSignature.NamedFunction.Member -> {
                        allVisibleMemberCallables.find { it.owner.name.asString() == signature.name }
                    }

                    is CallableSignature.NamedFunction.TopLevel -> {
                        // FIXME: just for supporting extension functions. Not proper way. Need to be fixed later.
                        referenceFunctions(CallableId(FqName(signature.packageFqName), Name.identifier(signature.name))).firstOrNull()
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

            if (classId.asString().contains("MutableList")) {
                MessageCollectorHolder.reportWarning(
                    """
                    classId: $classId
                    getter: ${trackableStateHolder.accessor.getter}
                """.trimIndent()
                )
            }

            val getterFunctionSymbol = when (val getter = trackableStateHolder.accessor.getter) {
                is CallableSignature.PropertyAccessor.Getter -> allPossibleMemberCallables.find {
                    it.owner.name.asString() == "<get-${getter.propertyName}>" && it.owner.isGetter
                }

                is CallableSignature.NamedFunction -> allPossibleMemberCallables.find {
                    it.owner.name.asString() == getter.name
                }

                is CallableSignature.This -> return SelfContained(
                    classSymbol = classSymbol,
                    setterSymbols = listOfNotNull(preSetterFunctionSymbol, setterFunctionSymbol),
                    captureTargetSymbols = captureTargetSymbols,
                    serializeAs = null,
                )

                else -> null
            } ?: return null

            return Wrapper(
                classSymbol = classSymbol,
                setterSymbols = listOfNotNull(preSetterFunctionSymbol, setterFunctionSymbol),
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
