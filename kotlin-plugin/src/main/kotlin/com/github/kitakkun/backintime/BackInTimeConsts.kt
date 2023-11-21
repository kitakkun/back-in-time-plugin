package com.github.kitakkun.backintime

import org.jetbrains.kotlin.javac.resolve.classId
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name

object BackInTimeConsts {
    val debuggableStateHolderManipulatorFqName = FqName("com.github.kitakkun.backintime.runtime.DebuggableStateHolderManipulator")

    val forceSetPropertyValueForBackInDebugMethodName = Name.identifier("forceSetPropertyValueForBackInTimeDebug")
    val firstParameterNameForGeneratedMethod = Name.identifier("propertyName")
    val secondParameterNameForGeneratedMethod = Name.identifier("value")

    val backInTimeDebugServiceClassId = classId("com.github.kitakkun.backintime.runtime", "BackInTimeDebugService")
    val notifyPropertyChanged = "notifyPropertyChanged"
    val registerFunctionName = "register"
    val systemClassId = classId("java.lang", "System")
    val printlnCallableId = CallableId(FqName("kotlin.io"), Name.identifier("println"))

    val methodCallInfoClassId = classId("com.github.kitakkun.backintime.runtime", "BackInTimeParentMethodCallInfo")
}
