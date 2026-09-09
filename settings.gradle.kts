rootProject.name = "backintime"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    includeBuild("gradle-conventions")
    repositories {
        mavenCentral()
        // JetWhale pre-release builds (see JetWhale docs: "Trying an unreleased (SNAPSHOT) build").
        maven("https://central.sonatype.com/repository/maven-snapshots/")
        gradlePluginPortal()
        google()
    }
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        // JetWhale pre-release builds (see JetWhale docs: "Trying an unreleased (SNAPSHOT) build").
        maven("https://central.sonatype.com/repository/maven-snapshots/")
        gradlePluginPortal()
        google()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

include(":gradle-plugin")
include(":demo:app")

include(
    ":compiler:k2",
    ":compiler:backend",
    ":compiler:cli",
    ":compiler:yaml",
    ":compiler:common",
    ":compiler-test",
)
include(
    ":core:runtime",
    ":core:annotations",
    ":core:websocket:server",
    ":core:websocket:client",
    ":core:websocket:event",
)
include(
    ":tooling:idea-plugin",
    ":tooling:core:model",
    ":tooling:core:database",
    ":tooling:core:ui",
    ":tooling:core:usecase",
    ":tooling:core:shared",
    ":tooling:app",
    ":tooling:feature:inspector",
    ":tooling:feature:settings",
    ":tooling:feature:log",
    ":tooling:standalone",
)

include(
    ":jetwhale-plugin:agent",
    ":jetwhale-plugin:host:app",
    ":jetwhale-plugin:host:plugin",
    ":jetwhale-plugin:host:core:database",
    ":jetwhale-plugin:host:core:model",
    ":jetwhale-plugin:host:core:shared",
    ":jetwhale-plugin:host:core:ui",
    ":jetwhale-plugin:host:core:usecase",
    ":jetwhale-plugin:host:feature:inspector",
    ":jetwhale-plugin:host:feature:log",
    ":jetwhale-plugin:host:feature:settings",
    ":jetwhale-plugin:protocol",
)
