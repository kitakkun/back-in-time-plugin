package com.github.kitakkun.backintime.compiler

import org.jetbrains.kotlin.fir.extensions.predicate.DeclarationPredicate

object BackInTimePredicate {
    val debuggableStateHolder = DeclarationPredicate.create {
        annotated(BackInTimeAnnotations.debuggableStateHolderAnnotationFqName)
    }
}
