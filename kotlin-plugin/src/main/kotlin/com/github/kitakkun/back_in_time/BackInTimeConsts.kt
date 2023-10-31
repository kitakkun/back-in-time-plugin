package com.github.kitakkun.back_in_time

import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name

object BackInTimeConsts {
    val debuggableStateHolderManipulatorFqName = FqName("com.github.kitakkun.back_in_time.annotations.DebuggableStateHolderManipulator")
    val forceSetParameterForBackInDebugMethodName = Name.identifier("forceSetParameterForBackInTimeDebug")
    val firstParameterNameForGeneratedMethod = Name.identifier("propertyName")
    val secondParameterNameForGeneratedMethod = Name.identifier("value")
}
