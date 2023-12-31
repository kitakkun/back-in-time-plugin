package com.github.kitakkun.backintime.compiler.backend

import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.ClassId

data class ValueContainerClassInfo(
    val classId: ClassId,
    val capturedCallableIds: List<CallableId>,
    val valueGetter: CallableId,
    val valueSetter: CallableId,
    val serializeItSelf: Boolean,
)
