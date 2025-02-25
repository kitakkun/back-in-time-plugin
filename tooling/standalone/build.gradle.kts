import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import util.ideaPluginRepos

plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.compose.compiler)
}

repositories {
    ideaPluginRepos()
}

dependencies {
    implementation(compose.desktop.currentOs)
    implementation(compose.materialIconsExtended)
    implementation(projects.tooling.app)
    implementation(projects.tooling.core.ui)
    implementation(projects.tooling.core.shared)
    implementation(projects.tooling.core.model)
    implementation(projects.tooling.core.database)
    implementation(projects.tooling.core.usecase)
    implementation(projects.core.websocket.server)
    implementation(projects.core.websocket.event)

    implementation(libs.jewel.standalone)
    implementation(libs.kotlinx.serialization.json)
}

compose.desktop {
    application {
        mainClass = "com.kitakkun.backintime.tooling.standalone.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "Back-in-Time Debugger"
            packageVersion = "1.0.0" // FIXME: input valid version of backintime
            jvmArgs("-Dapple.awt.application.appearance=system") // Make title bar match system theme
        }
    }
}
