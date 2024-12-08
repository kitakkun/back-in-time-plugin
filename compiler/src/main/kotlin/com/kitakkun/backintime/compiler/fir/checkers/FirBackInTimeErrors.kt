package com.kitakkun.backintime.compiler.fir.checkers

import org.jetbrains.kotlin.diagnostics.rendering.RootDiagnosticRendererFactory
import org.jetbrains.kotlin.diagnostics.warning0
import org.jetbrains.kotlin.psi.KtAnnotationEntry

object FirBackInTimeErrors {
    val MULTIPLE_PROPERTY_SETTER by warning0<KtAnnotationEntry>()
    val MULTIPLE_PROPERTY_GETTER by warning0<KtAnnotationEntry>()
    val MISSING_CAPTURE_CALL by warning0<KtAnnotationEntry>()
    val MISSING_PROPERTY_SETTER by warning0<KtAnnotationEntry>()
    val MISSING_PROPERTY_GETTER by warning0<KtAnnotationEntry>()
    val VALUE_CONTAINER_MORE_THAN_TWO_TYPE_ARGUMENTS by warning0<KtAnnotationEntry>()
    val PROPERTY_VALUE_MUST_BE_SERIALIZABLE by warning0<KtAnnotationEntry>()

    init {
        RootDiagnosticRendererFactory.registerFactory(KtDefaultErrorMessagesBackInTime)
    }
}
