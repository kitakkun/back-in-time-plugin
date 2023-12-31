pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        google()
    }
    plugins {
        kotlin("jvm") version "1.9.21" apply false
        kotlin("kapt") version "1.9.21" apply false
        id("com.android.library") version "8.2.0" apply false
        kotlin("multiplatform") version "1.9.21" apply false
        kotlin("plugin.serialization") version "1.9.21" apply false
    }
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
}

rootProject.name = "back-in-time"
include(":plugin-common")
include(":gradle-plugin")
include(":kotlin-plugin")
