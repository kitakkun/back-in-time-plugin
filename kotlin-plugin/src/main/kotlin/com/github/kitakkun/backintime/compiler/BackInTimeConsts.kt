package com.github.kitakkun.backintime.compiler

import org.jetbrains.kotlin.javac.resolve.classId
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name

object BackInTimeConsts {
    val debuggableStateHolderManipulatorFqName = FqName("com.github.kitakkun.backintime.runtime.DebuggableStateHolderManipulator")

    val serializeMethodName = Name.identifier("serializeValue")
    val deserializeMethodName = Name.identifier("deserializeValue")
    val forceSetValueMethodName = Name.identifier("forceSetValue")

    val backInTimeDebugServiceClassId = classId("com.github.kitakkun.backintime.runtime", "BackInTimeDebugService")
    val registerFunctionName = "register"
    val notifyMethodCallFunctionName = "notifyMethodCall"
    val notifyPropertyChanged = "notifyPropertyChanged"
    val printlnCallableId = CallableId(FqName("kotlin.io"), Name.identifier("println"))

    val methodCallInfoClassId = classId("com.github.kitakkun.backintime.runtime", "MethodCallInfo")
    val instanceInfoClassId = classId("com.github.kitakkun.backintime.runtime", "InstanceInfo")
    val propertyInfoClassId = classId("com.github.kitakkun.backintime.runtime", "PropertyInfo")
    val listOfFunctionId = CallableId(FqName("kotlin.collections"), Name.identifier("listOf"))
    val UUIDClassId = classId("java.util", "UUID")
    val randomUUIDFunctionName = "randomUUID"

    // Exception classes
    val backInTimeRuntimeExceptionClassId = classId("com.github.kitakkun.backintime.runtime.exception", "BackInTimeRuntimeException")
    val nullValueNotAssignableExceptionClassId = classId("com.github.kitakkun.backintime.runtime.exception", "BackInTimeRuntimeException.NullValueNotAssignableException")

    val kotlinxSerializationJsonClassId = classId("kotlinx.serialization.json", "Json")
    val kotlinxSerializationEncodeToStringFunctionId = CallableId(kotlinxSerializationJsonClassId, Name.identifier("encodeToString"))
}
