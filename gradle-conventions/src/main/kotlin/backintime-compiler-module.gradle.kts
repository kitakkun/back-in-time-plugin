import org.jetbrains.kotlin.gradle.dsl.KotlinJvmExtension
import util.configureVersionSpecificSourceDirectories
import util.libs
import util.parseToKotlinVersion

/**
 * This plugin will configure compiler modules.
 * - update version to kotlin-aware one (ex: 2.0.0-0.1.0)
 * - configure version-specific srcDirs (ex: v2.0.0)
 */
plugins {
    id("org.jetbrains.kotlin.jvm")
}

val kotlinVersion = libs.findVersion("kotlin").get().toString().parseToKotlinVersion()
val kotlinAwareVersion = "$kotlinVersion-$version"
version = kotlinAwareVersion

configure<KotlinJvmExtension> {
    jvmToolchain(17)
    configureVersionSpecificSourceDirectories(kotlinVersion)
}
