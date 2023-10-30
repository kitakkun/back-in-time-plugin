package com.github.kitakkun.back_in_time

import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name

object BackInTimeConsts {
    val forceSetParameterForBackInDebugMethodName = Name.identifier("forceSetParameterForBackInTimeDebug")
    val firstParameterNameForGeneratedMethod = Name.identifier("paramKey")
    val secondParameterNameForGeneratedMethod = Name.identifier("value")
    val debuggableStateHolderAnnotationFqName = FqName("com.github.kitakkun.back_in_time.annotations.DebuggableStateHolder")
    val debuggableStateHolderManipulatorFqName = FqName("com.github.kitakkun.back_in_time.annotations.DebuggableStateHolderManipulator")
    val debuggableStateHolderAnnotationClassId = ClassId.topLevel(debuggableStateHolderAnnotationFqName)
    val debuggableStateHolderManipulatorAnnotationClassId = ClassId.topLevel(debuggableStateHolderManipulatorFqName)
}
