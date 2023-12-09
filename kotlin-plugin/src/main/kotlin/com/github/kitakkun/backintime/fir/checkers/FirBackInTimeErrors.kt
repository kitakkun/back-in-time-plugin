package com.github.kitakkun.backintime.fir.checkers

import org.jetbrains.kotlin.diagnostics.rendering.RootDiagnosticRendererFactory
import org.jetbrains.kotlin.diagnostics.warning0
import org.jetbrains.kotlin.psi.KtAnnotationEntry

object FirBackInTimeErrors {
    val PROPERTY_VALUE_MUST_BE_SERIALIZABLE by warning0<KtAnnotationEntry>()

    init {
        RootDiagnosticRendererFactory.registerFactory(KtDefaultErrorMessagesBackInTime)
    }
}
