package com.kitakkun.backintime.compiler.k2.predicate

import com.kitakkun.backintime.compiler.common.BackInTimeAnnotations
import org.jetbrains.kotlin.fir.extensions.predicate.DeclarationPredicate

val trackableStateHolderPredicate = DeclarationPredicate.create {
    annotated(BackInTimeAnnotations.trackableStateHolderAnnotationFqName)
}
