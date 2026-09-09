package com.kitakkun.backintime.tooling.model

data class DependencyInfo(
    val uuid: String,
    val dependsOn: List<String>,
)
