package io.github.kitakkun.backintime.compiler.consts

import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName

object BackInTimeAnnotations {
    private const val BASE_PACKAGE = "io.github.kitakkun.backintime.annotations"
    val backInTimeAnnotationFqName = FqName("$BASE_PACKAGE.BackInTime")
    val backInTimeAnnotationClassId = ClassId.topLevel(backInTimeAnnotationFqName)

    val valueContainerAnnotationFqName = FqName("$BASE_PACKAGE.ValueContainer")
    val captureAnnotationFqName = FqName("$BASE_PACKAGE.Capture")
    val getterAnnotationFqName = FqName("$BASE_PACKAGE.Getter")
    val setterAnnotationFqName = FqName("$BASE_PACKAGE.Setter")
    val selfContainedValueContainerAnnotationFqName = FqName("$BASE_PACKAGE.SelfContainedValueContainer")
    val serializeAsAnnotationFqName = FqName("$BASE_PACKAGE.SerializeAs")

    val valueContainerAnnotationClassId = ClassId.topLevel(valueContainerAnnotationFqName)
    val captureAnnotationClassId = ClassId.topLevel(captureAnnotationFqName)
    val getterAnnotationClassId = ClassId.topLevel(getterAnnotationFqName)
    val setterAnnotationClassId = ClassId.topLevel(setterAnnotationFqName)
}
