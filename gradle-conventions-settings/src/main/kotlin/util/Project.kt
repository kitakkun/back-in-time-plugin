package util

import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.getByType
import org.jetbrains.compose.ComposeExtension

val Project.libs get() = extensions.getByType<VersionCatalogsExtension>().named("libs")

val Project.compose get() = extensions.getByType<ComposeExtension>().dependencies
