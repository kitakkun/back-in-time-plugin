package com.github.kitakkun.backintime.compiler.fir

import com.github.kitakkun.backintime.compiler.BackInTimeConsts
import com.github.kitakkun.backintime.compiler.BackInTimePluginKey
import org.jetbrains.kotlin.descriptors.Modality
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.analysis.checkers.toRegularClassSymbol
import org.jetbrains.kotlin.fir.extensions.FirDeclarationGenerationExtension
import org.jetbrains.kotlin.fir.extensions.MemberGenerationContext
import org.jetbrains.kotlin.fir.plugin.createMemberFunction
import org.jetbrains.kotlin.fir.plugin.createMemberProperty
import org.jetbrains.kotlin.fir.symbols.impl.FirClassSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirNamedFunctionSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirPropertySymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirRegularClassSymbol
import org.jetbrains.kotlin.fir.types.classId
import org.jetbrains.kotlin.fir.types.coneType
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.Name

class BackInTimeDebuggableMethodsDeclarationGenerationExtension(session: FirSession) : FirDeclarationGenerationExtension(session) {
    override fun generateProperties(callableId: CallableId, context: MemberGenerationContext?): List<FirPropertySymbol> {
        if (callableId.callableName != BackInTimeConsts.backInTimeInstanceUUIDName) return emptyList()
        val ownerClass = context?.owner ?: return emptyList()

        return listOf(
            createMemberProperty(
                owner = ownerClass,
                key = BackInTimePluginKey,
                name = callableId.callableName,
                returnType = session.builtinTypes.stringType.coneType,
                config = {
                    status { isOverride = true }
                    modality = Modality.OPEN
                }
            ).symbol
        )
    }

    override fun generateFunctions(callableId: CallableId, context: MemberGenerationContext?): List<FirNamedFunctionSymbol> {
        val ownerClass = context?.owner ?: return emptyList()
        val interfaceSymbol = ownerClass.resolvedSuperTypeRefs
            .find { it.type.classId == BackInTimeConsts.backInTimeDebuggableInterfaceClassId }
            ?.toRegularClassSymbol(session) ?: return emptyList()
        val correspondingFunctionSymbol = interfaceSymbol.declarationSymbols
            .filterIsInstance<FirNamedFunctionSymbol>()
            .find { it.callableId.callableName == callableId.callableName }
            ?: return emptyList()
        return listOf(
            createMemberFunction(
                owner = ownerClass,
                key = BackInTimePluginKey,
                name = callableId.callableName,
                returnType = correspondingFunctionSymbol.resolvedReturnType,
                config = {
                    correspondingFunctionSymbol.valueParameterSymbols.forEach { valueParameter(it.name, it.resolvedReturnType) }
                    status { isOverride = true }
                    modality = Modality.OPEN
                }
            ).symbol
        )
    }

    override fun getCallableNamesForClass(classSymbol: FirClassSymbol<*>, context: MemberGenerationContext): Set<Name> {
        return if (classSymbol is FirRegularClassSymbol && session.backInTimePredicateMatcher.isAnnotated(classSymbol)) {
            setOf(BackInTimeConsts.serializeMethodName, BackInTimeConsts.deserializeMethodName, BackInTimeConsts.forceSetValueMethodName, BackInTimeConsts.backInTimeInstanceUUIDName)
        } else {
            emptySet()
        }
    }
}