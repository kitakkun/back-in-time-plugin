package com.kitakkun.backintime.compiler.k2.extension

import com.kitakkun.backintime.compiler.common.BackInTimeConsts
import com.kitakkun.backintime.compiler.common.BackInTimePluginKey
import com.kitakkun.backintime.compiler.k2.predicate.BackInTimePredicate
import org.jetbrains.kotlin.descriptors.Modality
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.declarations.processAllDeclarations
import org.jetbrains.kotlin.fir.extensions.FirDeclarationGenerationExtension
import org.jetbrains.kotlin.fir.extensions.MemberGenerationContext
import org.jetbrains.kotlin.fir.extensions.predicateBasedProvider
import org.jetbrains.kotlin.fir.plugin.createMemberFunction
import org.jetbrains.kotlin.fir.plugin.createMemberProperty
import org.jetbrains.kotlin.fir.resolve.defaultType
import org.jetbrains.kotlin.fir.resolve.toRegularClassSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirClassSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirNamedFunctionSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirPropertySymbol
import org.jetbrains.kotlin.fir.types.classId
import org.jetbrains.kotlin.fir.types.isSubtypeOf
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.Name

class BackInTimeFirDeclarationGenerationExtension(session: FirSession) : FirDeclarationGenerationExtension(session) {
    override fun generateProperties(callableId: CallableId, context: MemberGenerationContext?): List<FirPropertySymbol> {
        val ownerClass = context?.owner ?: return emptyList()

        // if it already has a property from the parent class, don't generate it.
        if (
            ownerClass.resolvedSuperTypes.any {
                it.classId != BackInTimeConsts.backInTimeDebuggableInterfaceClassId && it.isSubtypeOf(
                    superType = BackInTimeConsts.backInTimeDebuggableInterfaceClassId.defaultType(emptyList()),
                    session = session,
                )
            }
        ) {
            return emptyList()
        }

        val backInTimeDebuggableInterfaceType = ownerClass.resolvedSuperTypes.find { it.classId == BackInTimeConsts.backInTimeDebuggableInterfaceClassId } ?: return emptyList()
        val backInTimeDebuggableInterfaceClassSymbol = backInTimeDebuggableInterfaceType.toRegularClassSymbol(session) ?: return emptyList()

        var originalDeclaration: FirPropertySymbol? = null

        backInTimeDebuggableInterfaceClassSymbol
            .processAllDeclarations(session) {
                if (it is FirPropertySymbol && it.callableId.callableName == callableId.callableName) {
                    originalDeclaration = it
                    return@processAllDeclarations
                }
            }

        if (originalDeclaration == null) return emptyList()

        return listOf(
            createMemberProperty(
                owner = ownerClass,
                key = BackInTimePluginKey,
                name = callableId.callableName,
                returnType = originalDeclaration.resolvedReturnType,
                config = {
                    status { isOverride = true }
                    modality = Modality.OPEN
                },
            ).symbol,
        )
    }

    override fun generateFunctions(callableId: CallableId, context: MemberGenerationContext?): List<FirNamedFunctionSymbol> {
        val ownerClass = context?.owner ?: return emptyList()

        val backInTimeDebuggableInterfaceType = ownerClass.resolvedSuperTypes.find { it.classId == BackInTimeConsts.backInTimeDebuggableInterfaceClassId } ?: return emptyList()
        val backInTimeDebuggableInterfaceClassSymbol = backInTimeDebuggableInterfaceType.toRegularClassSymbol(session) ?: return emptyList()

        var originalDeclaration: FirNamedFunctionSymbol? = null

        backInTimeDebuggableInterfaceClassSymbol
            .processAllDeclarations(session) {
                if (it is FirNamedFunctionSymbol && it.callableId.callableName == callableId.callableName) {
                    originalDeclaration = it
                    return@processAllDeclarations
                }
            }

        if (originalDeclaration == null) return emptyList()

        return listOf(
            createMemberFunction(
                owner = ownerClass,
                key = BackInTimePluginKey,
                name = callableId.callableName,
                returnType = originalDeclaration.resolvedReturnType,
                config = {
                    originalDeclaration.valueParameterSymbols.forEach { valueParameter(it.name, it.resolvedReturnType) }
                    status { isOverride = true }
                    modality = Modality.OPEN
                },
            ).symbol,
        )
    }

    override fun getCallableNamesForClass(classSymbol: FirClassSymbol<*>, context: MemberGenerationContext): Set<Name> {
        if (!session.predicateBasedProvider.matches(BackInTimePredicate, classSymbol)) return emptySet()
        return setOf(
            // methods
            BackInTimeConsts.forceSetValueMethodName,
            // properties
            BackInTimeConsts.backInTimeInstanceUUIDName,
            BackInTimeConsts.backInTimeInitializedPropertyMapName,
        )
    }
}
