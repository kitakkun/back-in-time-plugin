import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.backintimeDebuggerFeature)
}

val appName = "Back in Time Debugger"
val appVersion = "1.0.0"

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.runtime)
            implementation(projects.core.websocket.event)
            implementation(projects.debugTool.resources)
            implementation(projects.debugTool.feature.instance)
            implementation(projects.debugTool.feature.log)
            implementation(projects.debugTool.feature.settings)
            implementation(projects.debugTool.feature.connection)
            implementation(projects.debugTool.feature.connection)
            implementation(projects.debugTool.core.data)
            implementation(projects.debugTool.core.server)
            implementation(projects.debugTool.core.datastore)
            implementation(projects.debugTool.core.database)
        }
        jvmMain.dependencies {
            implementation(compose.desktop.currentOs)
        }
    }
}

compose.desktop {
    application {
        mainClass = "io.github.kitakkun.backintime.debugger.app.MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = appName
            packageVersion = appVersion
            // without this, the distributed app will crash with java.lang.NoClassDefFoundError: java/sql/Driver
            // FYI: https://stackoverflow.com/questions/56734786/java-lang-noclassdeffounderror-java-sql-driver
            modules("java.sql")
        }
    }
}
