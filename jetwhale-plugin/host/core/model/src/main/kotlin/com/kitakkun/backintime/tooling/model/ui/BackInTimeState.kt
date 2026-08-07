package com.kitakkun.backintime.tooling.model.ui

data class BackInTimeState(
    val open: Boolean,
    val histories: List<HistoryInfo>,
    val instanceUUID: String,
)
