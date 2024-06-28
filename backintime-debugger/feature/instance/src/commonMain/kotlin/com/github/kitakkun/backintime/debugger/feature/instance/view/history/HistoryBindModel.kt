package com.github.kitakkun.backintime.debugger.feature.instance.view.history

import com.github.kitakkun.backintime.debugger.feature.instance.view.history.timeline.TimelineItemBindModel

sealed interface HistoryBindModel {
    data object Loading : HistoryBindModel
    data class Loaded(
        val timelines: List<TimelineItemBindModel>,
    ) : HistoryBindModel
}
