package com.kitakkun.backintime.tooling.model.ui

data class ValueChangeInfo(
    val methodCallUUID: String,
    val time: Int,
    val value: String,
)