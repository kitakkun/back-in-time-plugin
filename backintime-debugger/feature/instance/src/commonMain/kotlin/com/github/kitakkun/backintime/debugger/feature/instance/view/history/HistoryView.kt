package com.github.kitakkun.backintime.debugger.feature.instance.view.history

import androidx.compose.runtime.Composable
import com.github.kitakkun.backintime.debugger.ui.customview.CommonLoadingView

@Composable
fun HistoryView(
    bindModel: HistoryBindModel,
) {
    when (bindModel) {
        is HistoryBindModel.Loading -> CommonLoadingView()
        is HistoryBindModel.Loaded -> HistoryLoadedView(bindModel)
    }
}
