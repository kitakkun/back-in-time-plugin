package com.github.kitakkun.back_in_time

import org.jetbrains.kotlin.fir.extensions.predicate.DeclarationPredicate

object BackInTimePredicate {
    val debuggableStateHolder = DeclarationPredicate.create {
        annotated(BackInTimeConsts.debuggableStateHolderAnnotationFqName)
    }
}
