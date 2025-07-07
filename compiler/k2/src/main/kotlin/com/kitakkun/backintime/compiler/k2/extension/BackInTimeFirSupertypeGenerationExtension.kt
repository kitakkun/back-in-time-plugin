package com.kitakkun.backintime.compiler.k2.extension

import com.kitakkun.backintime.compiler.common.BackInTimeConsts
import com.kitakkun.backintime.compiler.k2.predicate.BackInTimePredicate
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.declarations.FirClassLikeDeclaration
import org.jetbrains.kotlin.fir.extensions.FirDeclarationPredicateRegistrar
import org.jetbrains.kotlin.fir.extensions.FirSupertypeGenerationExtension
import org.jetbrains.kotlin.fir.extensions.predicateBasedProvider
import org.jetbrains.kotlin.fir.resolve.defaultType
import org.jetbrains.kotlin.fir.types.ConeKotlinType
import org.jetbrains.kotlin.fir.types.FirResolvedTypeRef

class BackInTimeFirSupertypeGenerationExtension(session: FirSession) : FirSupertypeGenerationExtension(session = session) {
    override fun needTransformSupertypes(declaration: FirClassLikeDeclaration): Boolean {
        return session.predicateBasedProvider.matches(BackInTimePredicate, declaration)
    }

    override fun computeAdditionalSupertypes(classLikeDeclaration: FirClassLikeDeclaration, resolvedSupertypes: List<FirResolvedTypeRef>, typeResolver: TypeResolveService): List<ConeKotlinType> {
        return listOf(BackInTimeConsts.backInTimeDebuggableInterfaceClassId.defaultType(emptyList()))
    }

    override fun FirDeclarationPredicateRegistrar.registerPredicates() {
        register(BackInTimePredicate)
    }
}