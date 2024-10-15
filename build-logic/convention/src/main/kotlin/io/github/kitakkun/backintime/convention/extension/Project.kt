package io.github.kitakkun.backintime.convention.extension

import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.getByType
import org.jetbrains.compose.ComposeExtension

val Project.libs: VersionCatalog get() = this.extensions.getByType<VersionCatalogsExtension>().named("libs")
val Project.compose: ComposeExtension get() = this.extensions.getByType<ComposeExtension>()
