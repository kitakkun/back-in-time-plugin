pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        google()
    }
    plugins {
        kotlin("multiplatform") version "1.9.20" apply false
        kotlin("plugin.serialization") version "1.9.20" apply false
        id("com.android.library") version "8.2.0" apply false
    }
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        google()
    }
}
