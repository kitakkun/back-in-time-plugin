import java.nio.file.Files
import java.nio.file.Path

/**
 * This buildscript can be applied with id "settings-conventions" in plugins block.
 * (note that you need to includeBuild before use it)
 *
 * This file is written referencing kotlinx-rpc project:
 * https://github.com/Kotlin/kotlinx-rpc/blob/main/gradle-conventions-settings/src/main/kotlin/settings-conventions.settings.gradle.kts
 */

pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        google()
    }
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        google()
    }

    versionCatalogs {
        create("libs") {
            val globalRootPath = findGlobalRootPath(rootDir.toPath())
            from(files("$globalRootPath/versions-root/libs.versions.toml"))
            System.getenv("KOTLIN_VERSION")?.let {
                version("kotlin", it)

                // FIXME: hard-coded KSP-version for the demo application
                when (it) {
                    "2.1.20" -> "$it-1.0.32"
                    "2.1.0" -> "$it-1.0.29"
                    "2.0.21" -> "$it-1.0.28"
                    "2.0.20" -> "$it-1.0.25"
                    "2.0.10" -> "$it-1.0.24"
                    "2.0.0" -> "$it-1.0.24"
                    else -> null
                }?.let { kspVersion ->
                    version("ksp", kspVersion)
                }
            }
        }
    }
}

/**
 * This plugin will be applied to all modules in the rootProject.
 * So, $rootProject doesn't always represents root directory for this repository.
 * We need to traverse directories until we find "versions-root" directory.
 */
fun findGlobalRootPath(start: Path): Path {
    var path = start

    while (
        Files.newDirectoryStream(path).use { it.toList() }.none {
            Files.isDirectory(it) && it.fileName.toString() == "versions-root"
        }
    ) {
        path = path.parent ?: error("Unable to find root path for Kondition project.")
    }

    return path
}
