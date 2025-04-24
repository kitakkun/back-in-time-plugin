@file:Suppress("unused")

package com.kitakkun.backintime.core.annotations

import kotlin.reflect.KClass

/**
 * configure TrackableStateHolder by annotating the class with this annotation.
 */
@Target(AnnotationTarget.CLASS)
annotation class TrackableStateHolder

@Target(AnnotationTarget.CLASS)
@TrackableStateHolder
annotation class SelfContainedTrackableStateHolder

@Target(AnnotationTarget.CLASS)
annotation class ExternalTrackableStateHolderConfig(val forClass: KClass<*>)

@Target(AnnotationTarget.CLASS)
annotation class SerializableAs(val clazz: KClass<*>)

@Target(AnnotationTarget.PROPERTY, AnnotationTarget.FUNCTION)
annotation class Capture(val strategy: CaptureStrategy = CaptureStrategy.AFTER_CALL)

@Target(AnnotationTarget.PROPERTY, AnnotationTarget.FUNCTION)
annotation class Getter

@Target(AnnotationTarget.PROPERTY, AnnotationTarget.FUNCTION)
annotation class Setter
