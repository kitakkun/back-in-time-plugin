pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        google()
        mavenLocal()
    }
    plugins {
        kotlin("multiplatform") version "1.9.21" apply false
        id("com.android.library") version "8.2.0" apply false
        id("com.github.kitakkun.backintime") version "1.0.0" apply false
    }
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        mavenLocal()
        google()
    }
}
