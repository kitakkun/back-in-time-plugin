package com.github.kitakkun.backintime

import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName

object BackInTimeAnnotations {
    val debuggableStateHolderAnnotationFqName = FqName("com.github.kitakkun.backintime.annotations.DebuggableStateHolder")
    val debuggableStateHolderManipulatorFqName = FqName("com.github.kitakkun.backintime.runtime.DebuggableStateHolderManipulator")
    val debuggableStateHolderAnnotationClassId = ClassId.topLevel(debuggableStateHolderAnnotationFqName)
    val debuggableStateHolderManipulatorAnnotationClassId = ClassId.topLevel(debuggableStateHolderManipulatorFqName)
}
