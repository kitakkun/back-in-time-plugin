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
import org.jetbrains.kotlin.fir.types.toRegularClassSymbol
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.Name

class BackInTimeFirDeclarationGenerationExtension(session: FirSession) : FirDeclarationGenerationExtension(session) {
    override fun generateProperties(callableId: CallableId, context: MemberGenerationContext?): List<FirPropertySymbol> {
        val ownerClass = context?.owner ?: return emptyList()
        val superTypeClass = ownerClass.resolvedSuperTypes.mapNotNull { it.toRegularClassSymbol(session) }
            .find { it.classId == BackInTimeConsts.backInTimeDebuggableInterfaceClassId } ?: return emptyList()
        val superDeclaration = superTypeClass.declarationSymbols.filterIsInstance<FirPropertySymbol>()
            .find { it.name == callableId.callableName } ?: return emptyList()

        return listOf(
            createMemberProperty(
                owner = ownerClass,
                key = BackInTimePluginKey,
                name = callableId.callableName,
                returnType = superDeclaration.resolvedReturnType,
                config = {
                    status { isOverride = true }
                    modality = Modality.OPEN
                }
            ).symbol
        )
    }

    override fun generateFunctions(callableId: CallableId, context: MemberGenerationContext?): List<FirNamedFunctionSymbol> {
        val ownerClass = context?.owner ?: return emptyList()
        val superTypeClass = ownerClass.resolvedSuperTypeRefs.mapNotNull { it.toRegularClassSymbol(session) }
            .find { it.classId == BackInTimeConsts.backInTimeDebuggableInterfaceClassId } ?: return emptyList()
        val superDeclaration = superTypeClass.declarationSymbols.filterIsInstance<FirNamedFunctionSymbol>()
            .find { it.callableId.callableName == callableId.callableName } ?: return emptyList()
        return listOf(
            createMemberFunction(
                owner = ownerClass,
                key = BackInTimePluginKey,
                name = callableId.callableName,
                returnType = superDeclaration.resolvedReturnType,
                config = {
                    superDeclaration.valueParameterSymbols.forEach { valueParameter(it.name, it.resolvedReturnType) }
                    status { isOverride = true }
                    modality = Modality.OPEN
                }
            ).symbol
        )
    }

    override fun getCallableNamesForClass(classSymbol: FirClassSymbol<*>, context: MemberGenerationContext): Set<Name> {
        return if (classSymbol is FirRegularClassSymbol && session.debuggableStateHolderPredicateMatcher.isAnnotated(classSymbol)) {
            setOf(
                BackInTimeConsts.serializeMethodName, BackInTimeConsts.deserializeMethodName, BackInTimeConsts.forceSetValueMethodName,
                BackInTimeConsts.backInTimeInstanceUUIDName, BackInTimeConsts.backInTimeInitializedPropertyMapName,
            )
        } else {
            emptySet()
        }
    }
}
