package com.kitakkun.backintime.compiler.k2.checkers

import com.kitakkun.backintime.compiler.k2.predicate.trackableStateHolderPredicate
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.analysis.checkers.declaration.DeclarationCheckers
import org.jetbrains.kotlin.fir.analysis.checkers.declaration.FirRegularClassChecker
import org.jetbrains.kotlin.fir.analysis.extensions.FirAdditionalCheckersExtension
import org.jetbrains.kotlin.fir.extensions.FirDeclarationPredicateRegistrar

class BackInTimeFirAdditionalCheckersExtension(session: FirSession) : FirAdditionalCheckersExtension(session) {
    override val declarationCheckers = object : DeclarationCheckers() {
        override val regularClassCheckers: Set<FirRegularClassChecker> = setOf(TrackableStateHolderDefinitionChecker, DebuggableStateHolderPropertyChecker)
    }

    override fun FirDeclarationPredicateRegistrar.registerPredicates() {
        register(trackableStateHolderPredicate)
    }
}
