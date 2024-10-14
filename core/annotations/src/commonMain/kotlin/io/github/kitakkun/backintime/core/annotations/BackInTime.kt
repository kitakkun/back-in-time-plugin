@file:Suppress("unused")

package io.github.kitakkun.backintime.core.annotations

/**
 * compiler plugin will inject code for debugging if the class is marked with this annotation.
 */
@Target(AnnotationTarget.CLASS)
annotation class BackInTime
