package io.github.kitakkun.backintime.compiler.consts

import org.jetbrains.kotlin.javac.resolve.classId
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName

object BackInTimeAnnotations {
    val backInTimeAnnotationFqName = FqName("io.github.kitakkun.backintime.annotations.BackInTime")
    val backInTimeAnnotationClassId = ClassId.topLevel(backInTimeAnnotationFqName)

    val valueContainerAnnotationFqName = FqName("io.github.kitakkun.backintime.annotations.ValueContainer")
    val captureAnnotationFqName = FqName("io.github.kitakkun.backintime.annotations.Capture")
    val getterAnnotationFqName = FqName("io.github.kitakkun.backintime.annotations.Getter")
    val setterAnnotationFqName = FqName("io.github.kitakkun.backintime.annotations.Setter")

    val valueContainerAnnotationClassId = ClassId.topLevel(valueContainerAnnotationFqName)
    val captureAnnotationClassId = ClassId.topLevel(captureAnnotationFqName)
    val getterAnnotationClassId = ClassId.topLevel(getterAnnotationFqName)
    val setterAnnotationClassId = ClassId.topLevel(setterAnnotationFqName)
    val serializableItSelfAnnotationClassId = classId("io.github.kitakkun.backintime.annotations", "SerializableItself")
}
