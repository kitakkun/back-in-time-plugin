pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        google()
    }
    plugins {
        kotlin("jvm") version "1.9.20" apply false
        kotlin("kapt") version "1.9.20" apply false
        kotlin("plugin.serialization") version "1.9.20" apply false
        id("com.android.library") version "8.2.0" apply false
    }
}

dependencyResolutionManagement {
    repositories {
        mavenLocal()
        mavenCentral()
        google()
    }
}

rootProject.name = "backintime"

include(
    ":plugin-common",
    ":gradle-plugin",
    ":kotlin-plugin",
)
