package com.github.kitakkun.backintime.debugger.feature.log.view.sessionlog.content

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.github.kitakkun.backintime.debugger.feature.log.view.sessionlog.content.model.EventKind
import com.github.kitakkun.backintime.debugger.ui.customview.CommonLoadingView

sealed class SessionLogContentBindModel {
    data object Loading : SessionLogContentBindModel()
    data class Loaded(
        val logs: List<LogItemBindModel>,
        val sortRule: SortRule,
        val visibleKinds: Set<EventKind>,
    ) : SessionLogContentBindModel()
}

@Composable
fun SessionLogContentView(
    bindModel: SessionLogContentBindModel,
    modifier: Modifier = Modifier,
    onToggleSortWithTime: () -> Unit,
    onToggleSortWithKind: () -> Unit,
    onUpdateVisibleKinds: (Set<EventKind>) -> Unit,
    onSelectLogItem: (LogItemBindModel) -> Unit,
) {
    when (bindModel) {
        is SessionLogContentBindModel.Loading -> CommonLoadingView(modifier)
        is SessionLogContentBindModel.Loaded -> SessionLogContentLoadedView(
            bindModel = bindModel,
            onToggleSortWithTime = onToggleSortWithTime,
            onToggleSortWithKind = onToggleSortWithKind,
            onUpdateVisibleKinds = onUpdateVisibleKinds,
            onSelectLogItem = onSelectLogItem,
            modifier = modifier,
        )
    }
}
