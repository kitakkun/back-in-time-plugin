import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension
import util.KotlinVersion
import util.libs
import util.parseToKotlinVersion

/**
 * This plugin will configure compiler modules.
 * - update version to kotlin-aware one (ex: 2.0.0-0.1.0)
 * - configure version-specific srcDirs (ex: v2.0.0)
 */
plugins {
    id("org.jetbrains.kotlin.jvm")
}

val kotlinVersion = libs.findVersion("kotlin").get().toString().parseToKotlinVersion()
val kotlinAwareVersion = "$kotlinVersion-$version"
version = kotlinAwareVersion

sealed interface RangedKotlinVersion {
    val version: KotlinVersion

    data class Direct(override val version: KotlinVersion) : RangedKotlinVersion
    data class Pre(override val version: KotlinVersion) : RangedKotlinVersion
}

// matches:
// - v_1
// - v_1_9
// - v_1_9_2
// - v_1_9_24
// - pre_1_9_20
// etc.
val directoryNameRegex = "^(v|pre)(_\\d){1,3}\\d?$".toRegex()

configure<KotlinJvmProjectExtension> {
    jvmToolchain(17)

    sourceSets.forEach { sourceSet ->
        val srcDirs = sourceSet.kotlin.srcDirs
        val sourceSetRootPath = srcDirs.first().toPath().parent

        val newSrcDirs = mutableSetOf<File>()
        newSrcDirs += srcDirs
        newSrcDirs += sourceSetRootPath.resolve("core").toFile()

        val versionSpecificSrcDirs = sourceSetRootPath.toFile().listFiles().orEmpty().filter {
            it.name.matches(directoryNameRegex) && it.exists()
        }

        val versionSpecificSrcDirMap = versionSpecificSrcDirs.associateBy {
            val kotlinVersion = it.name.substringAfter("_") // v_2_0_0 -> 2_0_0, pre_2_0_0 -> 2_0_0
                .replace("_", ".") // 2_0_0 -> 2.0.0
                .parseToKotlinVersion()
            when {
                it.name.startsWith("pre_") -> RangedKotlinVersion.Pre(kotlinVersion)
                it.name.startsWith("v_") -> RangedKotlinVersion.Direct(kotlinVersion)
                else -> error("Unexpected name pattern ${it.name}")
            }
        }.toList()

        val directVersionSpecificSrcDir = versionSpecificSrcDirMap.firstOrNull { it.first.version == kotlinVersion }
        val preVersionSpecificSrcDir = versionSpecificSrcDirMap.firstOrNull { it.first.version >= kotlinVersion }

        if (directVersionSpecificSrcDir != null) {
            newSrcDirs += directVersionSpecificSrcDir.second
        } else if (preVersionSpecificSrcDir != null) {
            newSrcDirs += preVersionSpecificSrcDir.second
        } else {
            newSrcDirs += sourceSetRootPath.resolve("latest").toFile()
        }

        sourceSet.kotlin.setSrcDirs(newSrcDirs)
    }
}