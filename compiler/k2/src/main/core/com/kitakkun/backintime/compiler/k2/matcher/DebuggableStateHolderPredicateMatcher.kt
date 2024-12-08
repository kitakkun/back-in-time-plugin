package com.kitakkun.backintime.compiler.k2.matcher

import com.kitakkun.backintime.compiler.common.BackInTimeAnnotations
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.extensions.predicate.DeclarationPredicate
import org.jetbrains.kotlin.fir.extensions.utils.AbstractSimpleClassPredicateMatchingService

class DebuggableStateHolderPredicateMatcher(
    session: FirSession,
) : AbstractSimpleClassPredicateMatchingService(session) {
    override val predicate = DeclarationPredicate.create {
        annotated(BackInTimeAnnotations.backInTimeAnnotationFqName)
    }
}

val FirSession.debuggableStateHolderPredicateMatcher: DebuggableStateHolderPredicateMatcher by FirSession.sessionComponentAccessor()
