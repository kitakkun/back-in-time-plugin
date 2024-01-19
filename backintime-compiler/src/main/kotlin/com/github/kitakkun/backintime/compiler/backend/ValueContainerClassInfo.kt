package com.github.kitakkun.backintime.compiler.backend

import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.Name

data class ValueContainerClassInfo(
    val classId: ClassId,
    val capturedFunctionNames: List<Name>,
    val getterFunctionName: Name,
    val preSetterFunctionNames: List<Name>,
    val setterFunctionName: Name,
    val serializeItSelf: Boolean,
    val serializeAs: ClassId?,
)
