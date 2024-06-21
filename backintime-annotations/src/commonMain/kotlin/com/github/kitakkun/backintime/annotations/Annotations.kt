@file:Suppress("unused")

package com.github.kitakkun.backintime.annotations

/**
 * compiler plugin will inject code for debugging if the class is marked with this annotation.
 */
@Target(AnnotationTarget.CLASS)
annotation class BackInTime

/**
 * configure ValueContainer by annotating the class with this annotation.
 */
@Target(AnnotationTarget.CLASS)
annotation class ValueContainer

@Target(AnnotationTarget.PROPERTY, AnnotationTarget.FUNCTION)
annotation class Capture

@Target(AnnotationTarget.PROPERTY, AnnotationTarget.FUNCTION)
annotation class Getter

@Target(AnnotationTarget.PROPERTY, AnnotationTarget.FUNCTION)
annotation class Setter

@Target(AnnotationTarget.CLASS)
annotation class SerializableItself(val asClass: String = "")
