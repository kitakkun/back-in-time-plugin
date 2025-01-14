package com.kitakkun.backintime.tooling.model

import kotlinx.serialization.Serializable
import kotlin.js.JsExport

@JsExport
@Serializable
data class PropertyInfo(
    val signature: String,
    val debuggable: Boolean,
    val isDebuggableStateHolder: Boolean,
    val propertyType: String,
    val valueType: String,
) {
    val name: String get() = signature.split(".").last()

    companion object {
        fun fromString(rawValue: String): PropertyInfo {
            /**
             * see [com.kitakkun.backintime.compiler.backend.transformer.capture.BackInTimeDebuggableConstructorTransformer] for details.
             */
            val info = rawValue.split(",")
            return PropertyInfo(
                signature = info[0],
                debuggable = info[1].toBoolean(),
                isDebuggableStateHolder = info[2].toBoolean(),
                propertyType = info[3],
                valueType = info[4],
            )
        }
    }
}
