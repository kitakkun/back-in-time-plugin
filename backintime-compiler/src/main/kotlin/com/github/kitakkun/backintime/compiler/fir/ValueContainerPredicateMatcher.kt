package com.github.kitakkun.backintime.compiler.fir

import com.github.kitakkun.backintime.compiler.BackInTimeAnnotations
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.extensions.predicate.DeclarationPredicate
import org.jetbrains.kotlin.fir.extensions.utils.AbstractSimpleClassPredicateMatchingService

class ValueContainerPredicateMatcher(
    session: FirSession,
) : AbstractSimpleClassPredicateMatchingService(session) {
    override val predicate = DeclarationPredicate.create {
        annotated(BackInTimeAnnotations.valueContainerAnnotationFqName)
    }
}

val FirSession.valueContainerPredicateMatcher: ValueContainerPredicateMatcher by FirSession.sessionComponentAccessor()
