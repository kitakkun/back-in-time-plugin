package com.kitakkun.backintime.compiler.k2.predicate

import com.kitakkun.backintime.compiler.common.BackInTimeAnnotations
import org.jetbrains.kotlin.fir.extensions.predicate.DeclarationPredicate

val BackInTimePredicate = DeclarationPredicate.create {
    annotated(BackInTimeAnnotations.backInTimeAnnotationFqName)
}
