package com.github.kitakkun.backintime.convention.extension

import com.google.devtools.ksp.gradle.KspExtension
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.getByType
import org.jetbrains.compose.ComposeExtension

val Project.libs get() = this.extensions.getByType<VersionCatalogsExtension>().named("libs")

val Project.compose get() = this.extensions.getByType<ComposeExtension>().dependencies

val Project.ksp get() = this.extensions.getByType<KspExtension>()