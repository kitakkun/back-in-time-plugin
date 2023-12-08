package com.github.kitakkun.backintime.fir

import com.github.kitakkun.backintime.BackInTimeAnnotations
import com.github.kitakkun.backintime.BackInTimePluginKey
import com.github.kitakkun.backintime.BackInTimePredicate
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

class BackInTimeFirDeclarationGenerationExtension(session: FirSession) : FirDeclarationGenerationExtension(session) {
    companion object {
        private val forceSetPropertyValueForBackInTimeDebugMethodName = Name.identifier("forceSetPropertyValueForBackInTimeDebug")
        private val serializePropertyMethodName = Name.identifier("serializePropertyValueForBackInTimeDebug")
    }

    override fun generateFunctions(callableId: CallableId, context: MemberGenerationContext?): List<FirNamedFunctionSymbol> {
        val ownerClass = context?.owner ?: return emptyList()
        return listOf(createForceSetPropertyValueForBackInTimeDebug(ownerClass, callableId).symbol)
    }

    private fun createForceSetPropertyValueForBackInTimeDebug(ownerClass: FirClassSymbol<*>, callableId: CallableId): FirSimpleFunction {
        when (callableId.callableName) {
            serializePropertyMethodName -> {
                return createMemberFunction(
                    owner = ownerClass,
                    key = BackInTimePluginKey,
                    name = callableId.callableName,
                    returnType = session.builtinTypes.stringType.coneType,
                    config = {
                        valueParameter(Name.identifier("propertyName"), type = session.builtinTypes.stringType.coneType)
                        valueParameter(Name.identifier("value"), type = session.builtinTypes.nullableAnyType.coneType)
                    },
                )
            }

            forceSetPropertyValueForBackInTimeDebugMethodName -> {
                return createMemberFunction(
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

            else -> {
                error("Unknown callable name: ${callableId.callableName}")
            }
        }
    }

    override fun getCallableNamesForClass(classSymbol: FirClassSymbol<*>, context: MemberGenerationContext): Set<Name> {
        if (!classSymbol.hasAnnotation(BackInTimeAnnotations.debuggableStateHolderAnnotationClassId, session)) return emptySet()
        return setOf(forceSetPropertyValueForBackInTimeDebugMethodName, serializePropertyMethodName)
    }

    override fun FirDeclarationPredicateRegistrar.registerPredicates() {
        register(BackInTimePredicate.debuggableStateHolder)
    }
}

