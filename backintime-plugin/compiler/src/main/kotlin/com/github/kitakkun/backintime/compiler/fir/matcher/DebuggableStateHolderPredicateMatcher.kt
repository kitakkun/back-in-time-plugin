package com.github.kitakkun.backintime.compiler.fir.matcher

import com.github.kitakkun.backintime.compiler.consts.BackInTimeAnnotations
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
