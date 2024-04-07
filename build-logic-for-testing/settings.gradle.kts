pluginManagement {
    includeBuild("../build-logic")
    repositories {
        mavenCentral()
        gradlePluginPortal()
        google()
    }
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        google()
    }
    versionCatalogs {
        create("libs") {
            from(files("../gradle/libs.versions.toml"))
        }
    }
}

rootProject.name = "build-logic-for-testing"

include(":backintime-plugin:common")
include(":backintime-plugin:gradle")
project(":backintime-plugin:common").projectDir = file("../backintime-plugin/common")
project(":backintime-plugin:gradle").projectDir = file("../backintime-plugin/gradle")
