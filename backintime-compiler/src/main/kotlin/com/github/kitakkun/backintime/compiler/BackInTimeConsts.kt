package com.github.kitakkun.backintime.compiler

import org.jetbrains.kotlin.javac.resolve.classId
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name

object BackInTimeConsts {
    val backInTimeDebuggableInterfaceClassId = classId("com.github.kitakkun.backintime.runtime", "BackInTimeDebuggable")
    val backInTimeDebuggableInterfaceClassFqName = backInTimeDebuggableInterfaceClassId.asSingleFqName()

    val serializeMethodName = Name.identifier("serializeValue")
    val deserializeMethodName = Name.identifier("deserializeValue")
    val forceSetValueMethodName = Name.identifier("forceSetValue")
    val backInTimeInstanceUUIDName = Name.identifier("backInTimeInstanceUUID")
    val backInTimeInitializedPropertyMapName = Name.identifier("backInTimeInitializedPropertyMap")

    val backInTimeDebugServiceClassId = classId("com.github.kitakkun.backintime.runtime", "BackInTimeDebugService")
    val backInTimeDebugServiceEventClassId = classId("com.github.kitakkun.backintime.runtime", "BackInTimeDebugServiceEvent")
    val registerEventClassId = backInTimeDebugServiceEventClassId.createNestedClassId(Name.identifier("RegisterInstance"))
    val registerRelationshipEventClassId = backInTimeDebugServiceEventClassId.createNestedClassId(Name.identifier("RegisterRelationShip"))
    val methodCallEventClassId = backInTimeDebugServiceEventClassId.createNestedClassId(Name.identifier("MethodCall"))
    val propertyValueChangeEventClassId = backInTimeDebugServiceEventClassId.createNestedClassId(Name.identifier("PropertyValueChange"))
    val printlnCallableId = CallableId(FqName("kotlin.io"), Name.identifier("println"))

    val instanceInfoClassId = classId("com.github.kitakkun.backintime.runtime", "InstanceInfo")
    val propertyInfoClassId = classId("com.github.kitakkun.backintime.runtime", "PropertyInfo")
    val listOfFunctionId = CallableId(FqName("kotlin.collections"), Name.identifier("listOf"))
    val UUIDClassId = classId("java.util", "UUID")
    const val randomUUIDFunctionName = "randomUUID"

    // Exception classes
    val backInTimeRuntimeExceptionClassId = classId("com.github.kitakkun.backintime.runtime.exception", "BackInTimeRuntimeException")
    val nullValueNotAssignableExceptionClassId = classId("com.github.kitakkun.backintime.runtime.exception", "BackInTimeRuntimeException.NullValueNotAssignableException")
    val noSuchPropertyExceptionClassId = classId("com.github.kitakkun.backintime.runtime.exception", "BackInTimeRuntimeException.NoSuchPropertyException")

    // kotlinx.serialization
    val backInTimeJsonCallableId = CallableId(FqName("com.github.kitakkun.backintime.runtime"), Name.identifier("backInTimeJson"))
    val kotlinxSerializationEncodeToStringCallableId = CallableId(FqName("kotlinx.serialization"), Name.identifier("encodeToString"))
    val kotlinxSerializationDecodeFromStringCallableId = CallableId(FqName("kotlinx.serialization"), Name.identifier("decodeFromString"))
}
