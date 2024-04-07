pluginManagement {
    includeBuild("build-logic")
    includeBuild("build-logic-for-testing")
    repositories {
        mavenLocal()
        mavenCentral()
        gradlePluginPortal()
        google()
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
    ":backintime-plugin:common",
    ":backintime-plugin:gradle",
    ":backintime-plugin:compiler",
    ":test",
    ":backintime-demo",
    ":backintime-demo:app",
    ":backintime-library:runtime",
    ":backintime-library:annotations",
    ":backintime-library:websocket:server",
    ":backintime-library:websocket:client",
    ":backintime-library:websocket:event",
    ":backintime-library:websocket:test",
)
