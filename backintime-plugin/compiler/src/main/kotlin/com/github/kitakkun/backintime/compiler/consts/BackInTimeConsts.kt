package com.github.kitakkun.backintime.compiler.consts

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

    val propertyInfoClassId = classId("com.github.kitakkun.backintime.websocket.event.model", "PropertyInfo")
    val listOfFunctionId = CallableId(FqName("kotlin.collections"), Name.identifier("listOf"))
    val UUIDClassId = classId("java.util", "UUID")
    const val RANDOM_UUID_FUNCTION_NAME = "randomUUID"

    // kotlinx.serialization
    val backInTimeJsonCallableId = CallableId(FqName("com.github.kitakkun.backintime.runtime"), Name.identifier("backInTimeJson"))
    val kotlinxSerializationEncodeToStringCallableId = CallableId(FqName("kotlinx.serialization"), Name.identifier("encodeToString"))
    val kotlinxSerializationDecodeFromStringCallableId = CallableId(FqName("kotlinx.serialization"), Name.identifier("decodeFromString"))
}
