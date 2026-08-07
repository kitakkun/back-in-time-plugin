package com.kitakkun.backintime.compiler.k2.checkers

import org.jetbrains.kotlin.diagnostics.KtDiagnosticsContainer
import org.jetbrains.kotlin.diagnostics.rendering.BaseDiagnosticRendererFactory
import org.jetbrains.kotlin.diagnostics.warning0
import org.jetbrains.kotlin.psi.KtAnnotationEntry

// Diagnostics are declared inside a KtDiagnosticsContainer since Kotlin 2.3: the `warningN` builders
// take the container as a context parameter, and the renderer factory is looked up through
// `getRendererFactory()` instead of being registered globally at object-initialization time.
object FirBackInTimeErrors : KtDiagnosticsContainer() {
    val MULTIPLE_PROPERTY_SETTER by warning0<KtAnnotationEntry>()
    val MULTIPLE_PROPERTY_GETTER by warning0<KtAnnotationEntry>()
    val MISSING_CAPTURE_CALL by warning0<KtAnnotationEntry>()
    val MISSING_PROPERTY_SETTER by warning0<KtAnnotationEntry>()
    val MISSING_PROPERTY_GETTER by warning0<KtAnnotationEntry>()
    val VALUE_CONTAINER_MORE_THAN_TWO_TYPE_ARGUMENTS by warning0<KtAnnotationEntry>()
    val PROPERTY_VALUE_MUST_BE_SERIALIZABLE by warning0<KtAnnotationEntry>()

    override fun getRendererFactory(): BaseDiagnosticRendererFactory = KtDefaultErrorMessagesBackInTime
}
