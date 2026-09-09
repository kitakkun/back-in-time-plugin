import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension
import util.libs

/**
 * Base convention for the JVM modules that make up the JetWhale host plugin.
 */
plugins {
    id("org.jetbrains.kotlin.jvm")
}

configure<KotlinJvmProjectExtension> {
    jvmToolchain(17)
}

dependencies {
    // The host provides the SDK at runtime, so it must never be bundled into the plugin jar.
    "compileOnly"(libs.findLibrary("jetwhaleHostSdk").get())
    // compileOnly does not reach the test compilation, and tests exercise SDK types directly.
    // Test configurations are not part of runtimeClasspath, so this cannot leak into the jar.
    "testImplementation"(libs.findLibrary("jetwhaleHostSdk").get())
}
