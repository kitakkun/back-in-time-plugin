package com.kitakkun.backintime.tooling.model.ui

data class InstanceItem(
    val name: String,
    val uuid: String,
    val superClassSignature: String,
    val properties: List<PropertyItem>,
    val superInstanceItem: InstanceItem?,
)
