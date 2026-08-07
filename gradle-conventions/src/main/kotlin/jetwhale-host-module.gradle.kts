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
}
