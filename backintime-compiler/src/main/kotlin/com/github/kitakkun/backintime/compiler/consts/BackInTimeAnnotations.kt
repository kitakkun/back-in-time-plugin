package com.github.kitakkun.backintime.compiler.consts

import org.jetbrains.kotlin.javac.resolve.classId
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName

object BackInTimeAnnotations {
    val debuggableStateHolderAnnotationFqName = FqName("com.github.kitakkun.backintime.annotations.DebuggableStateHolder")
    val debuggableStateHolderAnnotationClassId = ClassId.topLevel(debuggableStateHolderAnnotationFqName)

    val valueContainerAnnotationFqName = FqName("com.github.kitakkun.backintime.annotations.ValueContainer")
    val captureAnnotationFqName = FqName("com.github.kitakkun.backintime.annotations.Capture")
    val getterAnnotationFqName = FqName("com.github.kitakkun.backintime.annotations.Getter")
    val setterAnnotationFqName = FqName("com.github.kitakkun.backintime.annotations.Setter")

    val valueContainerAnnotationClassId = ClassId.topLevel(valueContainerAnnotationFqName)
    val captureAnnotationClassId = ClassId.topLevel(captureAnnotationFqName)
    val getterAnnotationClassId = ClassId.topLevel(getterAnnotationFqName)
    val setterAnnotationClassId = ClassId.topLevel(setterAnnotationFqName)
    val serializableItSelfAnnotationClassId = classId("com.github.kitakkun.backintime.annotations", "SerializableItself")
}
