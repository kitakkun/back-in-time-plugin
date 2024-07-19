package io.github.kitakkun.backintime.annotations

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.BINARY)
annotation class BackInTimeEntryPoint(val host: String, val port: Int)
