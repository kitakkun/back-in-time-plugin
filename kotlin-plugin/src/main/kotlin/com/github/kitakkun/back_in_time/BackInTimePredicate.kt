package com.github.kitakkun.back_in_time

import com.github.kitakkun.back_in_time.annotations.DebuggableStateHolder
import org.jetbrains.kotlin.descriptors.runtime.structure.classId
import org.jetbrains.kotlin.fir.extensions.predicate.DeclarationPredicate

object BackInTimePredicate {
    val debuggableStateHolder = DeclarationPredicate.create {
        annotated(DebuggableStateHolder::class.java.classId.asSingleFqName())
    }
}
