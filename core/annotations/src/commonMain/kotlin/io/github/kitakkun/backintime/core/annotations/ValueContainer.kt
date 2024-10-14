@file:Suppress("unused")

package io.github.kitakkun.backintime.core.annotations

import kotlin.reflect.KClass

/**
 * configure ValueContainer by annotating the class with this annotation.
 */
@Target(AnnotationTarget.CLASS)
annotation class ValueContainer

@Target(AnnotationTarget.CLASS)
@ValueContainer
annotation class SelfContainedValueContainer

@Target(AnnotationTarget.CLASS)
annotation class ExternalValueContainerConfig(val forClass: KClass<*>)

@Target(AnnotationTarget.CLASS)
annotation class SerializableAs(val clazz: KClass<*>)

@Target(AnnotationTarget.PROPERTY, AnnotationTarget.FUNCTION)
annotation class Capture(val strategy: CaptureStrategy = CaptureStrategy.AFTER_CALL)

@Target(AnnotationTarget.PROPERTY, AnnotationTarget.FUNCTION)
annotation class Getter

@Target(AnnotationTarget.PROPERTY, AnnotationTarget.FUNCTION)
annotation class Setter
