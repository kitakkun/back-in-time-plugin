package com.github.kitakkun.back_in_time.fir

import com.github.kitakkun.back_in_time.BackInTimePluginKey
import com.github.kitakkun.back_in_time.BackInTimePredicate
import com.github.kitakkun.back_in_time.annotations.DebuggableStateHolder
import org.jetbrains.kotlin.descriptors.runtime.structure.classId
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
    companion object {
        private val NAME_OF_GENERATED_FUNCTION = Name.identifier("forceSetParameterForBackInTimeDebug")
    }

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
                valueParameter(Name.identifier("paramKey"), type = session.builtinTypes.stringType.coneType)
                valueParameter(Name.identifier("value"), type = session.builtinTypes.stringType.coneType)
                status { isOverride = true }
            },
        ).apply {
            replaceBody(buildEmptyExpressionBlock())
        }
    }

    override fun getCallableNamesForClass(classSymbol: FirClassSymbol<*>, context: MemberGenerationContext): Set<Name> {
        if (!classSymbol.hasAnnotation(DebuggableStateHolder::class.java.classId, session)) return emptySet()
        return setOf(NAME_OF_GENERATED_FUNCTION)
    }

    override fun FirDeclarationPredicateRegistrar.registerPredicates() {
        register(BackInTimePredicate.debuggableStateHolder)
    }
}

