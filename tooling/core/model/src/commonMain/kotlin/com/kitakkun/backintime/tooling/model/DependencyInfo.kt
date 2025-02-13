package com.kitakkun.backintime.tooling.model

import kotlin.js.JsExport

@JsExport
data class DependencyInfo(
    val uuid: String,
    val dependsOn: List<String>,
)
