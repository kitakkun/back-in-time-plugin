package com.github.kitakkun.backintime.compiler

import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName

object BackInTimeAnnotations {
    val debuggableStateHolderAnnotationFqName = FqName("com.github.kitakkun.backintime.annotations.DebuggableStateHolder")
    val debuggableStateHolderManipulatorFqName = FqName("com.github.kitakkun.backintime.runtime.DebuggableStateHolderManipulator")
    val debuggableStateHolderAnnotationClassId = ClassId.topLevel(debuggableStateHolderAnnotationFqName)
    val debuggableStateHolderManipulatorAnnotationClassId = ClassId.topLevel(debuggableStateHolderManipulatorFqName)

    val captureAnnotationFqName = FqName("com.github.kitakkun.backintime.annotations.Capture")
    val getterAnnotationFqName = FqName("com.github.kitakkun.backintime.annotations.Getter")
    val setterAnnotationFqName = FqName("com.github.kitakkun.backintime.annotations.Setter")
}
