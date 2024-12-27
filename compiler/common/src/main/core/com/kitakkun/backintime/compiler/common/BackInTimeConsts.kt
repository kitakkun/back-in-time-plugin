package com.kitakkun.backintime.compiler.common

import org.jetbrains.kotlin.javac.resolve.classId
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name

object BackInTimeConsts {
    val backInTimeDebuggableInterfaceClassId = classId("com.kitakkun.backintime.core.runtime", "BackInTimeDebuggable")

    val serializeMethodName = Name.identifier("serializeValue")
    val deserializeMethodName = Name.identifier("deserializeValue")
    val forceSetValueMethodName = Name.identifier("forceSetValue")
    val backInTimeInstanceUUIDName = Name.identifier("backInTimeInstanceUUID")
    val backInTimeInitializedPropertyMapName = Name.identifier("backInTimeInitializedPropertyMap")

    // kotlinx.serialization
    val backInTimeJsonCallableId = CallableId(FqName("com.kitakkun.backintime.core.runtime"), Name.identifier("backInTimeJson"))
}
