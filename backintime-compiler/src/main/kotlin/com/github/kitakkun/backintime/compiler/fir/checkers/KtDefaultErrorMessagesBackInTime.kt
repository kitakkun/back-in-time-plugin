package com.github.kitakkun.backintime.compiler.fir.checkers

import org.jetbrains.kotlin.diagnostics.KtDiagnosticFactoryToRendererMap
import org.jetbrains.kotlin.diagnostics.rendering.BaseDiagnosticRendererFactory

object KtDefaultErrorMessagesBackInTime : BaseDiagnosticRendererFactory() {
    override val MAP = KtDiagnosticFactoryToRendererMap("BackInTime").apply {
        put(
            FirBackInTimeErrors.PROPERTY_VALUE_MUST_BE_SERIALIZABLE,
            "Non-serializable value is not debuggable. " +
                "Please add @Serializable annotation to the class of the property or the class of holding value.",
        )
        put(
            FirBackInTimeErrors.VALUE_CONTAINER_MORE_THAN_TWO_TYPE_ARGUMENTS,
            "The value container must have one or zero type arguments. " +
                "Currently, the value container which has more than two type argument is not supported.",
        )
        put(
            FirBackInTimeErrors.MISSING_PROPERTY_GETTER,
            "The value container must have a function or a property which can be used to get its value. " +
                "Please make sure you have a single member annotated with @Getter.",
        )
        put(
            FirBackInTimeErrors.MISSING_PROPERTY_SETTER,
            "The value container must have a function or a property which can be used to set its value. " +
                "Please make sure you have a single member annotated with @Setter.",
        )
        put(
            FirBackInTimeErrors.MISSING_CAPTURE_CALL,
            "The value container which has no capture call can't be debugged. " +
                "Please make sure you have any functions or properties annotated with @Capture.",
        )
        put(
            FirBackInTimeErrors.MULTIPLE_PROPERTY_GETTER,
            "The value container must have a single member annotated with @Getter.",
        )
        put(
            FirBackInTimeErrors.MULTIPLE_PROPERTY_SETTER,
            "The value container must have a single member annotated with @Setter.",
        )
    }
}
