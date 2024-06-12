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
    ":backintime-runtime",
    ":backintime-annotations",
    ":backintime-websocket-server",
    ":backintime-websocket-client",
    ":backintime-websocket-event",
)
