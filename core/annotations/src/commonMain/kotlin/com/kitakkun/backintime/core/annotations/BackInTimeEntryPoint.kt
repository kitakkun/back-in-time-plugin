@file:Suppress("UNUSED")

package com.kitakkun.backintime.core.annotations

const val BACK_IN_TIME_DEFAULT_HOST = "localhost"
const val BACK_IN_TIME_DEFAULT_SERVER_PORT = 8080

/**
 * Marks a class or function as an entry point for the BackInTime library.
 * If it is a class, BackInTime service initialization will be performed in the constructor.
 * If it is a function, BackInTime service initialization will be performed when the function is called.
 *
 * You can specify the host and port of the BackInTime server.
 * The default host is "localhost" and the default port is 8080.
 */
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
annotation class BackInTimeEntryPoint(
    val host: String = BACK_IN_TIME_DEFAULT_HOST,
    val port: Int = BACK_IN_TIME_DEFAULT_SERVER_PORT,
)
