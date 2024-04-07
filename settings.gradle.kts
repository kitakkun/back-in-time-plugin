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
    ":backintime-plugin-common",
    ":backintime-gradle-plugin",
    ":backintime-compiler",
    ":backintime-runtime",
    ":backintime-annotations",
    ":test",
    ":backintime-demo",
    ":backintime-demo:app",
    ":backintime-debugger:app",
    ":backintime-debugger:data",
    ":backintime-debugger:ui",
    ":backintime-debugger:featurecommon",
    ":backintime-debugger:feature:instance",
    ":backintime-debugger:feature:log",
    ":backintime-debugger:feature:server",
    ":backintime-debugger:feature:settings",
)
