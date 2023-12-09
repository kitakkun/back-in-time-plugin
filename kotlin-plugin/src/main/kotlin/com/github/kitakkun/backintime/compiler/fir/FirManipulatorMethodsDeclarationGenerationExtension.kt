package com.github.kitakkun.backintime.compiler.fir

import com.github.kitakkun.backintime.compiler.BackInTimeAnnotations
import com.github.kitakkun.backintime.compiler.BackInTimePluginKey
import com.github.kitakkun.backintime.compiler.BackInTimePredicate
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.declarations.FirSimpleFunction
import org.jetbrains.kotlin.fir.declarations.hasAnnotation
import org.jetbrains.kotlin.fir.extensions.FirDeclarationGenerationExtension
import org.jetbrains.kotlin.fir.extensions.FirDeclarationPredicateRegistrar
import org.jetbrains.kotlin.fir.extensions.MemberGenerationContext
import org.jetbrains.kotlin.fir.plugin.createMemberFunction
import org.jetbrains.kotlin.fir.symbols.impl.FirClassSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirNamedFunctionSymbol
import org.jetbrains.kotlin.fir.types.coneType
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.Name

class FirManipulatorMethodsDeclarationGenerationExtension(session: FirSession) : FirDeclarationGenerationExtension(session) {
    companion object {
        private val forceSetPropertyValueForBackInTimeDebugMethodName = Name.identifier("forceSetPropertyValueForBackInTimeDebug")
        private val serializePropertyMethodName = Name.identifier("serializePropertyValueForBackInTimeDebug")
        private val deserializePropertyMethodName = Name.identifier("deserializePropertyValueForBackInTimeDebug")
    }

    override fun generateFunctions(callableId: CallableId, context: MemberGenerationContext?): List<FirNamedFunctionSymbol> {
        val ownerClass = context?.owner ?: return emptyList()
        return listOf(createForceSetPropertyValueForBackInTimeDebug(ownerClass, callableId).symbol)
    }

    private fun createForceSetPropertyValueForBackInTimeDebug(ownerClass: FirClassSymbol<*>, callableId: CallableId): FirSimpleFunction {
        return when (callableId.callableName) {
            serializePropertyMethodName -> {
                createMemberFunction(
                    owner = ownerClass,
                    key = BackInTimePluginKey,
                    name = callableId.callableName,
                    returnType = session.builtinTypes.stringType.coneType,
                    config = {
                        valueParameter(Name.identifier("propertyName"), type = session.builtinTypes.stringType.coneType)
                        valueParameter(Name.identifier("value"), type = session.builtinTypes.nullableAnyType.coneType)
                        status { isOverride = true }
                    },
                )
            }

            forceSetPropertyValueForBackInTimeDebugMethodName -> {
                createMemberFunction(
                    owner = ownerClass,
                    key = BackInTimePluginKey,
                    name = callableId.callableName,
                    returnType = session.builtinTypes.unitType.coneType,
                    config = {
                        valueParameter(Name.identifier("propertyName"), type = session.builtinTypes.stringType.coneType)
                        valueParameter(Name.identifier("value"), type = session.builtinTypes.nullableAnyType.coneType)
                        status { isOverride = true }
                    },
                )
            }

            deserializePropertyMethodName -> {
                createMemberFunction(
                    owner = ownerClass,
                    key = BackInTimePluginKey,
                    name = callableId.callableName,
                    returnType = session.builtinTypes.nullableAnyType.coneType,
                    config = {
                        valueParameter(Name.identifier("propertyName"), type = session.builtinTypes.stringType.coneType)
                        valueParameter(Name.identifier("value"), type = session.builtinTypes.stringType.coneType)
                        status { isOverride = true }
                    },
                )
            }


            else -> {
                error("Unknown callable name: ${callableId.callableName}")
            }
        }
    }

    override fun getCallableNamesForClass(classSymbol: FirClassSymbol<*>, context: MemberGenerationContext): Set<Name> {
        if (!classSymbol.hasAnnotation(BackInTimeAnnotations.debuggableStateHolderAnnotationClassId, session)) return emptySet()
        return setOf(forceSetPropertyValueForBackInTimeDebugMethodName, serializePropertyMethodName, deserializePropertyMethodName)
    }

    override fun FirDeclarationPredicateRegistrar.registerPredicates() {
        register(BackInTimePredicate.debuggableStateHolder)
    }
}

