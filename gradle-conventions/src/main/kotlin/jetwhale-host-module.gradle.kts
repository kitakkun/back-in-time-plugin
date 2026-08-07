import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension
import util.libs

/**
 * Base convention for the JVM modules that make up the JetWhale host plugin.
 *
 * JetWhale is published with a newer Kotlin than this repository builds with (the host targets
 * Kotlin 2.3+ consumers), so reading its metadata needs the escape hatch documented in JetWhale's
 * getting-started guide. It is scoped to these modules only — nothing else in the build compiles
 * against JetWhale artifacts.
 */
plugins {
    id("org.jetbrains.kotlin.jvm")
}

configure<KotlinJvmProjectExtension> {
    jvmToolchain(17)

    compilerOptions {
        freeCompilerArgs.add("-Xskip-metadata-version-check")
    }
}

dependencies {
    // The host provides the SDK at runtime, so it must never be bundled into the plugin jar.
    "compileOnly"(libs.findLibrary("jetwhaleHostSdk").get())
}
