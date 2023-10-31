package com.github.kitakkun.back_in_time.fir

import com.github.kitakkun.back_in_time.BackInTimeAnnotations
import com.github.kitakkun.back_in_time.BackInTimeConsts
import com.github.kitakkun.back_in_time.BackInTimePluginKey
import com.github.kitakkun.back_in_time.BackInTimePredicate
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.declarations.FirSimpleFunction
import org.jetbrains.kotlin.fir.declarations.hasAnnotation
import org.jetbrains.kotlin.fir.expressions.builder.buildEmptyExpressionBlock
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
    override fun generateFunctions(callableId: CallableId, context: MemberGenerationContext?): List<FirNamedFunctionSymbol> {
        val ownerClass = context?.owner ?: return emptyList()
        return listOf(createForceSetParameterForBackInTimeDebug(ownerClass, callableId).symbol)
    }

    private fun createForceSetParameterForBackInTimeDebug(ownerClass: FirClassSymbol<*>, callableId: CallableId): FirSimpleFunction {
        return createMemberFunction(
            owner = ownerClass,
            key = BackInTimePluginKey,
            name = callableId.callableName,
            returnType = session.builtinTypes.unitType.coneType,
            config = {
                valueParameter(BackInTimeConsts.firstParameterNameForGeneratedMethod, type = session.builtinTypes.stringType.coneType)
                valueParameter(BackInTimeConsts.secondParameterNameForGeneratedMethod, type = session.builtinTypes.nullableAnyType.coneType)
                status { isOverride = true }
            },
        ).apply {
            replaceBody(buildEmptyExpressionBlock())
        }
    }

    override fun getCallableNamesForClass(classSymbol: FirClassSymbol<*>, context: MemberGenerationContext): Set<Name> {
        if (!classSymbol.hasAnnotation(BackInTimeAnnotations.debuggableStateHolderAnnotationClassId, session)) return emptySet()
        return setOf(BackInTimeConsts.forceSetParameterForBackInDebugMethodName)
    }

    override fun FirDeclarationPredicateRegistrar.registerPredicates() {
        register(BackInTimePredicate.debuggableStateHolder)
    }
}

