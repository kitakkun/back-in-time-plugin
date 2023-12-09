package com.github.kitakkun.backintime.fir.checkers

import org.jetbrains.kotlin.diagnostics.KtDiagnosticFactoryToRendererMap
import org.jetbrains.kotlin.diagnostics.rendering.BaseDiagnosticRendererFactory

object KtDefaultErrorMessagesBackInTime : BaseDiagnosticRendererFactory() {
    override val MAP = KtDiagnosticFactoryToRendererMap("BackInTime").apply {
        put(
            FirBackInTimeErrors.PROPERTY_VALUE_MUST_BE_SERIALIZABLE,
            "The value held by the property must be serializable. Please add @Serializable annotation to the type of the property.",
        )
    }
}
