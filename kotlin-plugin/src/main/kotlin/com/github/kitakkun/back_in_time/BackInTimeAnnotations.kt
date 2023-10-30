package com.github.kitakkun.back_in_time

import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName

object BackInTimeAnnotations {
    val debuggableStateHolderAnnotationFqName = FqName("com.github.kitakkun.back_in_time.annotations.DebuggableStateHolder")
    val debuggableStateHolderManipulatorFqName = FqName("com.github.kitakkun.back_in_time.annotations.DebuggableStateHolderManipulator")
    val debuggableStateHolderAnnotationClassId = ClassId.topLevel(debuggableStateHolderAnnotationFqName)
    val debuggableStateHolderManipulatorAnnotationClassId = ClassId.topLevel(debuggableStateHolderManipulatorFqName)
}
