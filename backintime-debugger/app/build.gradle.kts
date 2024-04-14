import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.debuggerFeature)
}

val appName = "Back in Time Debugger"
val appVersion = "1.0.0"

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(project(":backintime-library:runtime"))
            implementation(project(":backintime-library:websocket:event"))
            implementation(project(":backintime-debugger:feature:instance"))
            implementation(project(":backintime-debugger:feature:log"))
            implementation(project(":backintime-debugger:feature:settings"))
            implementation(project(":backintime-debugger:feature:connection"))
        }
    }
}

compose.desktop {
    application {
        mainClass = "com.github.kitakkun.backintime.debugger.MainKt"
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
