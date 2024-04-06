package com.github.kitakkun.backintime.convention.extension

import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.getByType

val Project.libs get() = this.extensions.getByType<VersionCatalogsExtension>().named("libs")