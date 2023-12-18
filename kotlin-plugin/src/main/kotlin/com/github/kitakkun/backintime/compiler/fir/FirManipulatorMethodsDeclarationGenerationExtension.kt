package com.github.kitakkun.backintime.compiler.fir

import com.github.kitakkun.backintime.compiler.BackInTimeConsts
import com.github.kitakkun.backintime.compiler.BackInTimePluginKey
import com.github.kitakkun.backintime.compiler.BackInTimePredicate
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.declarations.FirSimpleFunction
import org.jetbrains.kotlin.fir.extensions.FirDeclarationGenerationExtension
import org.jetbrains.kotlin.fir.extensions.FirDeclarationPredicateRegistrar
import org.jetbrains.kotlin.fir.extensions.MemberGenerationContext
import org.jetbrains.kotlin.fir.extensions.predicateBasedProvider
import org.jetbrains.kotlin.fir.plugin.createMemberFunction
import org.jetbrains.kotlin.fir.symbols.impl.FirClassSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirNamedFunctionSymbol
import org.jetbrains.kotlin.fir.types.coneType
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.Name

class FirManipulatorMethodsDeclarationGenerationExtension(session: FirSession) : FirDeclarationGenerationExtension(session) {
    override fun generateFunctions(callableId: CallableId, context: MemberGenerationContext?): List<FirNamedFunctionSymbol> {
        val ownerClass = context?.owner ?: return emptyList()
        return listOf(createForceSetPropertyValueForBackInTimeDebug(ownerClass, callableId).symbol)
    }

    private fun createForceSetPropertyValueForBackInTimeDebug(ownerClass: FirClassSymbol<*>, callableId: CallableId): FirSimpleFunction {
        return when (callableId.callableName) {
            BackInTimeConsts.serializeMethodName -> {
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

            BackInTimeConsts.forceSetValueMethodName -> {
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

            BackInTimeConsts.deserializeMethodName -> {
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
        return if (session.predicateBasedProvider.matches(BackInTimePredicate.debuggableStateHolder, classSymbol)) {
            setOf(
                BackInTimeConsts.serializeMethodName,
                BackInTimeConsts.deserializeMethodName,
                BackInTimeConsts.forceSetValueMethodName,
            )
        } else {
            emptySet()
        }
    }

    override fun FirDeclarationPredicateRegistrar.registerPredicates() {
        register(BackInTimePredicate.debuggableStateHolder)
    }
}

