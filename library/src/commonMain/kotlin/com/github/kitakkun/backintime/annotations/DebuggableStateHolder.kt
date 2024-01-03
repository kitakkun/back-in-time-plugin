package com.github.kitakkun.backintime.annotations

/**
 * compiler plugin will inject code for debugging if the class is marked with this annotation.
 */
@Suppress("unused")
@Target(AnnotationTarget.CLASS)
annotation class DebuggableStateHolder(val applyAllProperties: Boolean = true)

@Suppress("unused")
@Target(AnnotationTarget.PROPERTY)
annotation class DebuggableProperty
