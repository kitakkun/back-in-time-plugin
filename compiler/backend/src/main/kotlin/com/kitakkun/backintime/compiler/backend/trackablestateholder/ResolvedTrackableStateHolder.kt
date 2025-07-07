package com.kitakkun.backintime.compiler.backend.trackablestateholder

import com.kitakkun.backintime.compiler.backend.BackInTimePluginContext
import com.kitakkun.backintime.compiler.backend.utils.extensionReceiverParameter
import com.kitakkun.backintime.compiler.backend.utils.valueParameters
import com.kitakkun.backintime.compiler.yaml.CallableSignature
import com.kitakkun.backintime.compiler.yaml.ParametersSignature
import com.kitakkun.backintime.compiler.yaml.TrackableStateHolder
import com.kitakkun.backintime.compiler.yaml.TypeSignature
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.symbols.IrClassSymbol
import org.jetbrains.kotlin.ir.symbols.IrSimpleFunctionSymbol
import org.jetbrains.kotlin.ir.symbols.IrTypeParameterSymbol
import org.jetbrains.kotlin.ir.types.classOrNull
import org.jetbrains.kotlin.ir.types.classifierOrNull
import org.jetbrains.kotlin.ir.util.classId
import org.jetbrains.kotlin.ir.util.getAllSuperclasses
import org.jetbrains.kotlin.ir.util.isGetter
import org.jetbrains.kotlin.ir.util.isSetter
import org.jetbrains.kotlin.ir.util.simpleFunctions
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.synthetic.isVisibleOutside

sealed class ResolvedTrackableStateHolder {
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
    ) : ResolvedTrackableStateHolder() {
        override val getterSymbol: IrSimpleFunctionSymbol? = null
    }

    data class Wrapper(
        override val classSymbol: IrClassSymbol,
        override val setterSymbols: List<IrSimpleFunctionSymbol>,
        override val captureTargetSymbols: List<Pair<IrSimpleFunctionSymbol, CaptureStrategy>>,
        override val getterSymbol: IrSimpleFunctionSymbol,
        override val serializeAs: IrClassSymbol?,
    ) : ResolvedTrackableStateHolder()

    companion object {
        fun create(
            irContext: BackInTimePluginContext,
            trackableStateHolder: TrackableStateHolder,
        ): ResolvedTrackableStateHolder? {
            val classId = ClassId.fromString(trackableStateHolder.classId)

            val classSymbol = irContext.referenceClass(classId) ?: return null

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
                    it.owner.name.asString() == setter.name && it.owner.valueParameters().size == 1
                }

                else -> null
            } ?: return null

            val captureTargetSymbols = trackableStateHolder.captures.flatMap { captureTarget ->
                val functionSymbols = when (val signature = captureTarget.signature) {
                    is CallableSignature.PropertyAccessor.Getter -> {
                        allVisibleMemberCallables.filter {
                            it.owner.name.asString() == "<get-${signature.propertyName}>"
                        }
                    }

                    is CallableSignature.PropertyAccessor.Setter -> {
                        allVisibleMemberCallables.filter {
                            it.owner.name.asString() == "<set-${signature.propertyName}>"
                        }
                    }

                    is CallableSignature.NamedFunction.Member -> {
                        allVisibleMemberCallables.filter { it.owner.matchesFunctionSignature(signature) }
                    }

                    is CallableSignature.NamedFunction.TopLevel -> {
                        irContext.referenceFunctions(CallableId(FqName(signature.packageFqName), Name.identifier(signature.name))).filter {
                            if (signature.receiverClassId.isNotEmpty()) {
                                it.owner.matchesFunctionSignature(signature)
                                    && it.owner.extensionReceiverParameter()?.type?.classOrNull?.owner?.classId == ClassId.fromString(signature.receiverClassId)
                            } else {
                                it.owner.matchesFunctionSignature(signature)
                            }
                        }
                    }

                    else -> null
                } ?: return@flatMap emptyList()

                val strategy = when (val strategy = captureTarget.strategy) {
                    is com.kitakkun.backintime.compiler.yaml.CaptureStrategy.ValueArgument -> CaptureStrategy.ValueArgument(strategy.index)
                    is com.kitakkun.backintime.compiler.yaml.CaptureStrategy.AfterCall -> CaptureStrategy.AfterCall
                }

                functionSymbols.map { it to strategy }
            }

            val serializeClassSymbol = (trackableStateHolder.serializeAs as? TypeSignature.Class)?.classId?.let {
                irContext.referenceClass(ClassId.fromString(it))
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
                    serializeAs = serializeClassSymbol,
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

        private fun IrFunction.matchesFunctionSignature(signature: CallableSignature.NamedFunction): Boolean {
            return when (val parametersSignature = signature.valueParameters) {
                is ParametersSignature.Any -> this.name.asString() == signature.name
                is ParametersSignature.Specified -> {
                    parametersSignature.parameterTypes.size == this.valueParameters().size &&
                        parametersSignature.parameterTypes.zip(this.valueParameters()).all { (typeSignature, irValueParameter) ->
                            when (typeSignature) {
                                is TypeSignature.Any -> true

                                is TypeSignature.Class -> {
                                    irValueParameter.type.classOrNull?.owner?.classId == ClassId.fromString(typeSignature.classId)
                                }

                                is TypeSignature.Generic -> {
                                    (irValueParameter.type.classifierOrNull as? IrTypeParameterSymbol)?.owner?.index == typeSignature.index
                                }
                            }
                        }
                }
            }
        }
    }
}
