package com.kitakkun.backintime.compiler.common

import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName

object BackInTimeAnnotations {
    private const val BASE_PACKAGE = "com.kitakkun.backintime.core.annotations"
    val backInTimeEntryPointAnnotationFaName = FqName("$BASE_PACKAGE.BackInTimeEntryPoint")
    val backInTimeAnnotationFqName = FqName("$BASE_PACKAGE.BackInTime")
    val backInTimeAnnotationClassId = ClassId.topLevel(backInTimeAnnotationFqName)

    val trackableStateHolderAnnotationFqName = FqName("$BASE_PACKAGE.TrackableStateHolder")
    val captureAnnotationFqName = FqName("$BASE_PACKAGE.Capture")
    val getterAnnotationFqName = FqName("$BASE_PACKAGE.Getter")
    val setterAnnotationFqName = FqName("$BASE_PACKAGE.Setter")
    val selfContainedTrackableStateHolderAnnotationFqName = FqName("$BASE_PACKAGE.SelfContainedTrackableStateHolder")
    val serializeAsAnnotationFqName = FqName("$BASE_PACKAGE.SerializeAs")

    val trackableStateHolderAnnotationClassId = ClassId.topLevel(trackableStateHolderAnnotationFqName)
    val captureAnnotationClassId = ClassId.topLevel(captureAnnotationFqName)
    val getterAnnotationClassId = ClassId.topLevel(getterAnnotationFqName)
    val setterAnnotationClassId = ClassId.topLevel(setterAnnotationFqName)
}
