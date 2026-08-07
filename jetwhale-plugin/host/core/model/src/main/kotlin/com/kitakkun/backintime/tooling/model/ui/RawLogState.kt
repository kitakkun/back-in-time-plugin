package com.kitakkun.backintime.tooling.model.ui

import com.kitakkun.backintime.tooling.model.RawEventLog

data class RawLogState(
    val logs: List<RawEventLog>,
    val selectedLogId: String?,
)
