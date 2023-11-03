pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        google()
    }
    plugins {
        kotlin("jvm") version "1.9.20" apply false
        kotlin("kapt") version "1.9.20" apply false
        id("com.android.library") version "8.1.0" apply false
        kotlin("multiplatform") version "1.9.20" apply false
    }
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
}

rootProject.name = "back-in-time"
include(":gradle-plugin")
include(":kotlin-plugin")
