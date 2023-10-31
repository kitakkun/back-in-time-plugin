package com.github.kitakkun.back_in_time.annotations

/**
 * compiler plugin will inject code for debugging if the class is marked with this annotation.
 */
@Suppress("unused")
@Target(AnnotationTarget.CLASS)
annotation class DebuggableStateHolder(val applyAllProperties: Boolean = true)

@Suppress("unused")
@Target(AnnotationTarget.PROPERTY)
annotation class DebuggableProperty

interface DebuggableStateHolderManipulator {
    fun forceSetPropertyValueForBackInTimeDebug(propertyName: String, value: Any?)
}
